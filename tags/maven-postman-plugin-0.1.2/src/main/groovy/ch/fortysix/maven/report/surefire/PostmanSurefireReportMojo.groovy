package ch.fortysix.maven.report.surefire

/**
 * 
 */

import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.MavenReportException;

import ch.fortysix.maven.report.AbstractReportMojo;
import ch.fortysix.maven.report.support.SinkReporter;


/**
 * Sends mails based on surefire (test) results.
 * 
 * @author Domi
 * @goal surefire-mail
 * @phase site
 */
class PostmanSurefireReportMojoo extends AbstractReportMojo {
	
	/**
	 * The postfix used in the email subject 
	 * @parameter default-value="surfire test notification"
	 */
	String subjectPostFix
	
	String getSubjectPostFix(){
		return subjectPostFix
	}
	
	String getNlsPrefix(){
		"report.postman.surefire."
	}
	
	public String getOutputName() {
		"postman-surefire-report"
	}	
	
	/**
	 * Who should receive a mail? One can use an id of a developer registered in the pom or an email address directly.
	 * <pre>
	 * 	 		&lt;receivers&gt;	 
	 * 		  		&lt;receiver&gt;developerId&lt;/receiver&gt;
	 * 		  		&lt;receiver&gt;sam@topland.com&lt;/receiver&gt;
	 * 	 		&lt;/receivers&gt;
	 * </pre>
	 * @parameter 
	 * @required
	 */	
	Set receivers;	
	
	/**
	 * The file pattern to be used to search for the surefire reports in the 'testReportsDirectory'-directory.
	 * 
	 * @parameter default-value="TEST-.*.xml"
	 */	
	String reportFilePattern
	
	/**
	 * This allows to redefine the condition to send the mail. e.g. one can define that there 
	 * must not be more then 20 skipped test cases (<code>skipped > 20</code>).
	 * The default condition sends mails if there are any errors.
	 * The following variables are available for usage in the condition:
	 * <ul>
	 * 	<li><code>errors</code>: number of all errors while running surefire</li>
	 * 	<li><code>skipped</code>: number of all skipped test cases</li>
	 * 	<li><code>failures</code>: number of all failures in the test cases</li>
	 * 	<li><code>total</code>: number of all test cases</li>
	 * </ul>
	 * Other (usefull or not...) examples:
	 * <ul>
	 * 	<li><code>errors > 0</code> : sends a mail only if there are errors</li>
	 * 	<li><code>skipped > failures</code> : sends mails if there are more skipped then failed test cases</li>
	 * 	<li><code>total == skipped</code> : Sends mails if all tests are skipped</li>
	 * </ul>
	 * To avoid problems with XML syntax, one can use a <a href="http://www.w3schools.com/xmL/xml_cdata.asp">CDATA element</a>.
	 * The default only send a mail if there are errors or failures, but ignores the skipped ones.
	 * @parameter default-value="errors > 0 || failures > 0"
	 */
	String groovyCondition
	
	/**
	 * Base directory where all surefire test reports are read from.  
	 * @parameter expression="${project.build.directory}/surefire-reports" default-value="${project.build.directory}/surefire-reports"
	 */	
	File testReportsDirectory	
	
	protected void executePostmanReport(Locale locale) throws MavenReportException {
		
		if(!testReportsDirectory || !testReportsDirectory.exists()){
			getLog().warn """
				'testReportsDirectory' could not be found ($testReportsDirectory).
				This could have multiple reasons:
				1. order of report plugin execution might not be correct.
				   'maven-surefire-report-plugin' must be defined before 'maven-postman-plugin' in the pom!
				2. The 'taglist-maven-plugin' does not generate any report information (html or xml) 
				   if it can't find any test cases.			
				"""
			return
		}
		
		SurefireMailCollector testReportSender = new SurefireMailCollector(log: getLog(), reportFilePattern: reportFilePattern)
		def mailContent = testReportSender.getSingleMail(testReportsDirectory)
		
		def receiver2Mail = [:]	
		// we send the same report to all receivers
		if(mailContent.suiteReports){
			receivers.each{ aReceiver -> 
				receiver2Mail.put(aReceiver, mailContent)
			}
		}
		
		////////////////////////////////////////////////////
		//
		// evaluate if the condition tells us to send the mails
		
		// - prepare variables
		def errors = mailContent.suiteReports.inject(0) { count, suiteReport -> count + suiteReport.errors }
		log.debug "ERRORS: "+ errors
		def skipped = mailContent.suiteReports.inject(0) { count, suiteReport -> count + suiteReport.skipped }
		log.debug "SKIPPED: "+ skipped
		def failures = mailContent.suiteReports.inject(0) { count, suiteReport -> count + suiteReport.failures }
		log.debug "FAILURES: "+ failures
		def tests = mailContent.suiteReports.inject(0) { count, suiteReport -> count + suiteReport.tests }
		log.debug "TOTAL: "+ tests
		
		// - bind the variables
		Binding binding = new Binding();
		binding.setVariable("errors", errors);
		binding.setVariable("skipped", skipped);
		binding.setVariable("failures", failures);
		binding.setVariable("total", tests);
		
		// - evaluate
		log.info "evaluating groovy condition [$groovyCondition]"
		GroovyShell shell = new GroovyShell(binding);
		def value 
		try{
			value = shell.evaluate(groovyCondition);
		}catch (Exception e){
			throw new MojoExecutionException("postman is not able to evaluate the configured 'groovyCondition': $groovyCondition", e)
		}
		boolean sendIt = value as Boolean
		
		if(skipMails){
			log.info "postman skips sending mails!"
		} else if(sendIt){
			receiver2Mail.each sendReport
		} else{
			log.info "postman surfire report groovy condition [$groovyCondition] not fulfilled, don't send the mails..."
		}
		
		
		def report = new SinkReporter(bodyGenerator: new SurefireReportBodyGenerator(receiver2TestReport: receiver2Mail))
		report.doGenerateReport( getBundle( locale ), getSink(), nlsPrefix, getLog() )
	}
	
}