/**
 * 
 */
package ch.fortysix.maven.report;

import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap 

import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;

import com.sun.activation.registries.LogSupport;

import ch.fortysix.maven.plugin.util.AddressResolver;
import ch.fortysix.maven.plugin.util.MailSenderContext;
import ch.fortysix.maven.report.support.MailSender;

/**
 * @author Domi
 *
 */
abstract class AbstractReportMojo extends AbstractMavenReport {
	
	/**
	 * @parameter expression="${session}"
	 * @required
	 * @readonly
	 */
	org.apache.maven.execution.MavenSession session
	
	/**
	 * Encoding of the source. 
	 * Advice is taken from: <a href="http://docs.codehaus.org/display/MAVENUSER/POM+Element+for+Source+File+Encoding">POM Element for Source File Encoding</a>
	 * @parameter expression="${encoding}" default-value="${project.build.sourceEncoding}"
	 */
	String sourceEncoding;	
	
	/**
	 * Indicates whether this report should skip the sending mails (no mails send).
	 * @parameter  default-value="false"
	 */
	boolean skipMails	= false
	
	/**
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	org.apache.maven.project.MavenProject project
	
	/**
	 * Indicates whether you need TLS/SSL
	 * @parameter  default-value="false"
	 */
	boolean mailssl	= false
	
	/**
	 * Indicates to use an alternative way to configure the ssl connection to the smtp server. 
	 * This might be needed in specific environments.
	 * @parameter  default-value="false"
	 */
	boolean mailAltConfig = false
	
	/**
	 * Host name of the SMTP server. The default value is localhost.
	 * @parameter expression="${mailhost}" default-value="localhost"
	 */
	String mailhost
	
	/**
	 * TCP port of the SMTP server. The default value is 25.
	 * @parameter expression="${mailport}" default-value="25"
	 */
	String mailport
	
	/**
	 * User name for SMTP auth
	 * @parameter 
	 */
	String mailuser
	
	/**
	 * Password for SMTP auth
	 * @parameter 
	 */
	String mailpassword
	
	/**
	 * Email subject line. 
	 * @parameter default-value="[${project.artifactId}] "
	 */
	String subject
	
	/**
	 * Email address of sender.
	 * @parameter 
	 * @required
	 */
	String from	
	
	/**
	 * Report output directory. Note that this parameter is only relevant if the goal is run from the command line or
	 * from the default build lifecycle. If the goal is run indirectly as part of a site generation, the output
	 * directory configured in the Maven Site Plugin is used instead.
	 * 
	 * @parameter default-value="${project.reporting.outputDirectory}"
	 */
	File outputDirectory
	
	/**
	 * The url where to find the deployed documentation. 
	 * This is used to create a link to reports from within mails.
	 * 
	 * @parameter default-value="http://localhost/${artifactId}/"
	 */
	String docuSite
	
	/**
	 * flag to indicate whether to halt the build on any error. The default value is true.
	 * @parameter default-value="true"
	 */
	boolean failonerror = true
	
	/**
	 * The content type to use for the message. 
	 * This is only the fallback contenttype if the environment does not support 'multipart/alternative'.
	 * @parameter default-value="text/html"
	 */
	String mailcontenttype	
	
	/**
	 * Whether 'multipart/alternative' mails can be send.
	 * This is detected automatically, but it allows a user to disable it and force the usage of 'mailcontenttype'. 
	 * @parameter default-value="true"
	 */
	boolean multipartSupported = true
	
	/**
	 * Sends the emails...
	 */
	def mailSender
	
	/**
	 * is able to resolve the email for a given developerId in the pom.xml (maven project)
	 */
	def mailAddressResolver
	
	protected MailSenderContext context 
	
	/**
	 * The list of mails send/processed by the report. This one gets filled during <code>#executePostmanReport()</code>
	 */
	def mailList = []
	
	/**
	 * prefix within the bundle files - has to be overwritten by implementors!
	 */
	abstract String getNlsPrefix()
	
	/**
	 * @see org.apache.maven.reporting.AbstractMavenReport#getOutputDirectory()
	 */
	protected String getOutputDirectory() {
		outputDirectory.getAbsolutePath()
	}
	
	/**
	 * @see org.apache.maven.reporting.AbstractMavenReport#getProject()
	 */
	protected MavenProject getProject() {
		project
	}
	
	/**
	 * @see org.apache.maven.reporting.AbstractMavenReport#getSiteRenderer()
	 */
	protected Renderer getSiteRenderer() {
		siteRenderer
	}
	
	public String getDescription(Locale locale) {
		getBundle( locale ).getString( nlsPrefix + "description" )
	}
	
	public String getName(Locale locale) {
		getBundle( locale ).getString( nlsPrefix + "name" )
	}	
	
	public ResourceBundle getBundle( Locale locale ) {
		ResourceBundle.getBundle( getOutputName(), locale, this.getClass().getClassLoader() )
	}
	
	/**
	 * Sends one mail
	 */
	//	def sendReport = { receiver, mailContent ->
	//		
	//		def mailAddress = mailAddressResolver.resolveEMailAddress(receiver)
	//		
	//		getLog().info "send mail to: $mailAddress"
	//		
	//		def tos = [mailAddress]
	//		def newSubject = subject + " " + getSubjectPostFix()
	//		
	//		mailSender.sendMail(from: from, 
	//		subject: newSubject, 
	//		txtmessage: mailContent.text(),
	//		htmlmessage: mailContent.html(),
	//		receivers: tos)
	//	}	
	
	/**
	 * do the report and send the mails
	 */
	protected void executeReport(Locale locale) throws MavenReportException {
		
		if(!this.prepareReport (locale)){
			getLog().warn "not able to create report"
			return
		}
		
		// create a mailsender
		mailSender = new MailSender(
				mailcontenttype: mailcontenttype,
				multipartSupported: multipartSupported,
				mailAltConfig: mailAltConfig,
				log: getLog(),
				failonerror: failonerror,
				ssl: mailssl,
				user: mailuser,
				password: mailpassword,
				mailhost: mailhost, 
				mailport: mailport)		
		mailSender.init()
		
		context = new MailSenderContext(
				session: session, 
				project: project,
				log: log, 
				skipMails: skipMails,
				multipartSupported: multipartSupported,
				mailSender: mailSender,
				)
		
		
		def mailList = this.collectMails()
		this.executePostmanReport locale, mailList
		
	}
	
	/**
	 * Prepares and/or checks the preconditions for the report 
	 * @return
	 */
	protected abstract boolean prepareReport(Locale locale)
	
	/**
	 * Collects the mails and returns them for further processing in the report 
	 * @return a list of mails to be send
	 */
	protected abstract List collectMails()
	
	/**
	 * To be implemented by subclasses. Should also fill the list
	 * 
	 */
	protected abstract void executePostmanReport(Locale locale, List mailList) throws MavenReportException;
	
	/**
	 * returns the postfix for the mail subject
	 */
	protected abstract String getSubjectPostFix()
	
	/**
	 * Returns the actual url to the documentation site
	 */
	String getActualDocuSite(){
		if(!docuSite.endsWith('/')){
			docuSite = docuSite + '/'
		}
		return docuSite
	}
	
}
