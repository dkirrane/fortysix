package ch.fortysix.maven.plugin

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
	 * Indicates whether you need TLS/SSL
	 * @parameter  default-value="false"
	 */
	boolean mailssl	= false
	
	void execute() {
		
		def xmlText = reportFile?.text
		if(xmlText != null){
			def report = new XmlSlurper().parseText(xmlText)
			
			
			log.info("seding mail...")
			
			try{
				
				// we collect all receivers mention in the report
				def allReceivers = report.rule.receiver*.text().unique()
				def receiver2Rules = [:]
				
				allReceivers.each{ aReceiver ->
					def rulesForReceiver = report.rule.findAll {
						// get all receivers within one rule
						def receiversForRule = it.receiver*.text()
						// check if the list contains the current receiver
						receiversForRule.contains(aReceiver)
					}
					if(getLog().isDebugEnabled()){
						rulesForReceiver.each{getLog().debug "send this to [$aReceiver]: "+it}
					}
					receiver2Rules.put aReceiver, rulesForReceiver 
				}
				
				receiver2Rules.each sendReport
				
			}catch (Exception e) {
				log.error("postman failed sening mails", e)
				if(failonerror){
					throw e
				}
			}
		}
	}
	
	
	def sendReport = { receiver, rules ->
		
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
		
		def message = rules.toString()
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
