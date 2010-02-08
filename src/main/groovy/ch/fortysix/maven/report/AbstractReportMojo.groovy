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
	 * @parameter default-value="${artifactId}: "
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
	def sendReport = { receiver, mailContent ->
		
		if(!receiver.contains("@")){
			def email = project.developers?.find {  
				it.id == receiver
			}?.email
			if(email){
				getLog().info "replace [$receiver] by [$email]"
				receiver = email
			}else{
				getLog().warn "not able to find email for [$receiver]"
				// exit the closure
				return
			}
		}
		getLog().info "send mail to: $receiver"
		
		def tos = [receiver]
		def newSubject = subject + " " + getSubjectPostFix()
		
		mailSender.sendMail(from: from, 
		subject: newSubject, 
		txtmessage: mailContent.text(),
		htmlmessage: mailContent.html(),
		receivers: tos)
	}	
	
	/**
	 * do the report and send the mails
	 */
	protected void executeReport(Locale locale) throws MavenReportException {
		
		if(!sourceEncoding){
			sourceEncoding = session.getExecutionProperties()."file.encoding"
			getLog().warn "Using platform sourceEncoding ($sourceEncoding actually) to copy filtered resources, i.e. build is platform dependent!"
		}
		
		def cl
		if(!skipMails){
			// Since the javax.activation.* implementation/distribution is included in the JRE since java6,
			// we some times discovered problems with loading the correct mail mimetypes from the 'mailcap's file.
			// As the project has dependencies to the mail.jar and the activation.jar we know a correct implementation
			// is on the classpath and we can force the loading from it if we tweak the classloader hierarchy 
			// Check if the correct mimetypes could have been loaded from the activation.jar
			if(multipartSupported){
				
				def java = session.getExecutionProperties()."file.encoding"
				// save the classloader for later restoring 
				cl = Thread.currentThread().getContextClassLoader()
				// set the classloader of the current class as the classlaoder of the current thread
				// this has to be done every time, otherwise only the first plugin execution (in a reportSet) will work! 
				Thread.currentThread().setContextClassLoader( getClass().getClassLoader() )	
				
				def mimeToCheck = "multipart/mixed"
				// the user wants to try to send multipart messages
				MailcapCommandMap mc = (MailcapCommandMap)CommandMap.getDefaultCommandMap();
				
				if(getLog().isDebugEnabled()){
					mc.getMimeTypes().each{ getLog().debug "Original  MIME-TYPE: $it" }
				}
				
				if(!mc.getAllCommands (mimeToCheck)){
					getLog().debug "Mail MimeType not registred, tweaking classloader..."
					CommandMap.setDefaultCommandMap(new MailcapCommandMap());
					MailcapCommandMap newMc = (MailcapCommandMap)CommandMap.getDefaultCommandMap();
					
					if(getLog().isDebugEnabled()){
						newMc.getMimeTypes().each{ getLog().debug "new MIME-TYPE: $it" }
					}
					
					if(!newMc.getAllCommands (mimeToCheck)){
						multipartSupported = false
						getLog().warn "not able to load MimeType 'multipart/mixed', can only send 'text/*' mails"
					}
				} 
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
		}
		
		try{
			// execute the report
			executePostmanReport(locale)
			
		} finally {
			
			if(cl){
				getLog().debug "restore 'original' classlaoder"
				Thread.currentThread().setContextClassLoader( cl )
			}
			
		}
		
	}
	
	/**
	 * To be implemented by subclasses
	 */
	protected abstract void executePostmanReport(Locale locale) throws MavenReportException;
	
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
