/**
 * 
 */
package ch.fortysix.maven.report.support
;

/**
 * @author Domi
 *
 */class MailSender {
	
	private AntBuilder ant = new AntBuilder()
	
	String mailhost
	String mailport
	String subject
	String from
	String cc
	String message
	List receivers
	String user
	String password
	boolean ssl
	boolean failonerror
	
	void sendMail(){
		
		ant.mail(taskname: "postman", mailhost: mailhost, 
		mailport: mailport,
		subject: subject,
		user: user, 
		password: password,
		ssl: ssl,
		failonerror: failonerror){
			
			receivers.each{ to(address:it) }
			
			from(address:from)
			cc(address:cc)
			message(message)
		}
	}
	
}
