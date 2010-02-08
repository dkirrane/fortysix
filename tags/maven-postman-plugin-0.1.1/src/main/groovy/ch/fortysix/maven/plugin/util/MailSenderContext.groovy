package ch.fortysix.maven.plugin.util;

import java.util.Locale;

import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;

import org.apache.maven.reporting.MavenReportException;

import ch.fortysix.maven.report.support.MailSender;

class MailSenderContext {
	
	org.apache.maven.execution.MavenSession session
	
	org.apache.maven.project.MavenProject project
	
	org.apache.maven.plugin.logging.Log log
	
	/**
	 * Indicates whether the mail context should be created.
	 */
	boolean skipMails	= false
	
	/**
	 * flag to indicate whether to halt the build on any error. The default value is true.
	 */
	boolean failonerror = true	
	
	/**
	 * Whether 'multipart/alternative' mails can be send.
	 * This is detected automatically, but it allows a user to disable it and force the usage of 'mailcontenttype'. 
	 */
	boolean multipartSupported = true
	
	
	MailSender mailSender
	
	/**
	 * do the report and send the mails
	 */
	protected void run(Object callback) throws Exception {
		
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
				
				mailSender.init()	
			}
			
		}
		
		
		
		try{
			// execute the callback
			callback.executeInternal()
			
		} finally {
			
			if(cl){
				getLog().debug "restore 'original' classlaoder"
				Thread.currentThread().setContextClassLoader( cl )
			}
			
		}
		
	}
	
	
	/**
	 * Sends one mail
	 */
	public def sendReport = { mailContent ->
		assert mailContent.receivers
		assert mailContent.subject
		assert mailContent.from
		
		def resolvedReceivers = []
		
		mailContent.receivers.each { receiver ->
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
			resolvedReceivers << receiver
		}
		
		
		def attchements = []
		def txt = mailContent.text ? mailContent.text : "No text content defined"
		def html = mailContent.html ? mailContent.html : "<body>No html content defined</body>"
		
		mailSender.sendMail(from: mailContent.from, 
		subject: mailContent.subject, 
		txtmessage: txt,
		htmlmessage: html,
		receivers: resolvedReceivers,
		attchements: attchements)
	}		
	
}
