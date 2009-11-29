/**
 * 
 */
package ch.fortysix.maven.plugin;

/**
 * @author Domi
 *
 */
	
	private AntBuilder ant = new AntBuilder()
	
	String mailhost
	String mailport
	String subject
	String from
	String cc
	String message
	List recivers
	boolean failonerror
	
	void sendMail(){
		
		ant.mail(mailhost: mailhost, 
		mailport: mailport,
		subject: subject,
		failonerror: failonerror){
			
			recivers.each{ to(address:it) }
			
			from(address:from)
			cc(address:cc)
			message(message)
		}
	}
	
}