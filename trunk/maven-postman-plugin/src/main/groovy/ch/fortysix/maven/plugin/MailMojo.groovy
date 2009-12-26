package ch.fortysix.maven.plugin


import ch.fortysix.maven.plugin.sender.support.TaglistReport;
import ch.fortysix.maven.plugin.sender.PostmanReportSender;

import ch.fortysix.maven.plugin.sender.TaglistReportSender;

import java.io.File;

import org.codehaus.gmaven.mojo.GroovyMojo;

/**
 * Sends the mails/reports collected by the 'collect' goal.
 *
 * @goal send
 */
class MailMojo extends GroovyMojo {
	
	
	/**
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	org.apache.maven.project.MavenProject project
	
	/**
	 * The xml file to read the postman report from.
	 * @parameter default-value="${project.build.directory}/postman.xml"
	 */
	File reportFile;
	
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
	 * flag to indicate whether to halt the build on any error. The default value is true.
	 * @parameter default-value="true"
	 */
	boolean failonerror = true;
	
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
	 * <pre>
	 * &lt;taglistReport&gt;								
	 * 	&lt;tagClasses&gt;
	 * 		&lt;tagClass&gt;
	 * 			&lt;displayName&gt;TODO Work&lt;/displayName&gt;
	 * 	 		&lt;receivers&gt;	 
	 * 		  		&lt;receiver&gt;developerId&lt;/receiver&gt;
	 * 		  		&lt;receiver&gt;sam@topland.com&lt;/receiver&gt;
	 * 	 		&lt;/receivers&gt;
	 * 		&lt;/tagClass&gt;
	 * 	&lt;/tagClasses&gt;
	 * &lt;/taglistReport&gt;
	 * </pre>
	 * @parameter 
	 */	
	TaglistReport taglistReport;
	

	
	/**
	 * Indicates whether you need TLS/SSL
	 * @parameter  default-value="false"
	 */
	boolean mailssl	= false

	
	  		
	void execute() {
		
		if(reportFile){
			PostmanReportSender sender = new PostmanReportSender(log: getLog())
			def receiver2Rules = sender.getMails(reportFile)
			receiver2Rules.each sendReport
		}
		
		if(taglistReport && !taglistReport.skip){
			TaglistReportSender taglistSender = new TaglistReportSender(log: getLog(), taglistReport: taglistReport)
			def receiver2Taglist = taglistSender.getMails(taglistReportXml)
			receiver2Taglist.each sendReport
		}else{
			getLog().warn "postman is skipping 'Taglist report'"
		}
		
		
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
		
		println message
		if(getLog().isDebugEnabled()){
			
		}
		
		def tos = [receiver]
		MailSender sender = new MailSender(from: from, 
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
	
}
