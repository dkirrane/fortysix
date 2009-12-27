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
	 * Host name of the SMTP server. The default value is localhost.
	 * @parameter expression="${mailhost}" default-value="localhost"
	 */
	String mailhost;
	
	/**
	 * TCP port of the SMTP server. The default value is 25.
	 * @parameter expression="${mailport}" default-value="25"
	 */
	String mailport;
	
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
	 * @parameter expression="${subject}" default-value="postman has a delivery"
	 */
	String subject;
	
	/**
	 * Email address of sender.
	 * @parameter expression="${from}" 
	 * @required
	 */
	String from;	
	
	/**
	 * Report output directory. Note that this parameter is only relevant if the goal is run from the command line or
	 * from the default build lifecycle. If the goal is run indirectly as part of a site generation, the output
	 * directory configured in the Maven Site Plugin is used instead.
	 * 
	 * @parameter default-value="${project.reporting.outputDirectory}"
	 */
	File outputDirectory;	
	
	/**
	 * flag to indicate whether to halt the build on any error. The default value is true.
	 * @parameter default-value="true"
	 */
	boolean failonerror = true;	
	
	/**
	 * prefix within the bundle files - has to be overwriten by implemtors!
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
		
		def address = ""
		
		if(!receiver.contains("@")){
			def email = project.developers?.find {  
				it.id == receiver
			}?.email
			if(email){
				getLog().info "replace [$receiver] by [$email]"
				receiver = email
			}else{
				getLog().warn "not able to find email for [$receiver]"
			}
		}
		getLog().info "send mail to: $receiver"
		
		def message = mailContent.asMailBody()
		
		def tos = [receiver]
		def sender = new MailSender(from: from, 
		subject: subject, 
		message: message, 
		mailhost: mailhost, 
		mailport: mailport, 
		failonerror: failonerror,
		ssl: mailssl,
		user: mailuser,
		password: mailpassword,
		receivers: tos)
		sender.sendMail()
	}	
	
	
	/**
	 * Initializes the MOJO.
	 * If sourceEncoding is not set, we use system 'file.encoding'.
	 */
	void init(){
		if(!sourceEncoding){
			sourceEncoding = session.getExecutionProperties()."file.encoding"
			getLog().warn "Using platform sourceEncoding ($sourceEncoding actually) to copy filtered resources, i.e. build is platform dependent!"
		}
	}
	
}
