package ch.fortysix.maven.plugin

import java.io.File;

import org.codehaus.gmaven.mojo.GroovyMojo;

/**
 * Mojo enabling the conditional sending of email
 *
 * @goal send
 */
class MailMojo extends GroovyMojo {
	
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
	
	void execute() {
		
		def xmlText = reportFile?.text
		if(xmlText != null){
			def report = new XmlSlurper().parseText(xmlText)
			
			
			log.info("seding mail...")
			
			try{
				
				def allRecivers = report.rule.reciver*.text().unique()
				
				allRecivers.each{ aReciver ->
					println aReciver
					def rulesForReciver = report.rule.findAll {
						println it.reciver.text()
						it.reciver.text().equals aReciver 
					}
					
					rulesForReciver.each{println "send this: "+it}
				}
				
				//			MailSender sender = new MailSender(from: from, 
				//					subject: subject, 
				//					message: message, 
				//					mailhost: mailhost, 
				//					mailport: mailport, 
				//					failonerror:failonerror,
				//					recivers: tos)
				//			sender.sendMail()
				
			}catch (Exception e) {
				log.error("failed executing postman plugin", e)
				if(failonerror){
					throw e
				}
			}
		}
	}
	
}
