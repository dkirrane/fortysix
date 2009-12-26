/**
 * 
 */
package ch.fortysix.maven.report;

import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;

import ch.fortysix.maven.plugin.sender.PostmanReportSender;
import ch.fortysix.maven.report.taglist.TaglistReportBodyGenerator;

/**
 * @author Domi
 * @goal postman-regex-report
 * @phase site
 */
class PostmanReportMojo extends AbstractReportMojo {
	
	/**
	 * The xml file to read the postman report from.
	 * @parameter default-value="${project.build.directory}/postman.xml"
	 */
	File reportFile;
	
	String getNlsPrefix(){
		"report.postman."
	}
	
	public String getOutputName() {
		"postman-report"
	}	
	
	protected void executeReport(Locale locale) throws MavenReportException {
		
		def receiver2Mail = [:]
		
		if(reportFile && reportFile.isFile()){
			PostmanReportSender sender = new PostmanReportSender(log: getLog())
			receiver2Mail = sender.getMails(reportFile)
			receiver2Mail.each sendReport
		}else{
			getLog().warn "ReportFile is not readable ($reportFile)!"
			skipMails = true
		}
		
		if(!skipMails){
			receiver2Mail.each sendReport
		} else{
			log.info "postman skips sending mails!"
		}
		
		def report = new HtmlReporter(bodyGenerator: new TaglistReportBodyGenerator(receiver2TestReport: receiver2Mail))
		report.doGenerateReport( getBundle( locale ), getSink(), nlsPrefix, getLog() )
	}
	
}
