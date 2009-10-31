/**
 * 
 */
package ch.fortysix.maven.plugin;

/**
 * @author Domi
 *
 */public class MailSender {

	private AntBuilder ant = new AntBuilder()
	
	String mailhost
	int mailport
	String subject
	String from
	String cc
	String to
	String message
	
	void sendMail(){
		ant.mail(mailhost:mailhost, mailport:"$mailport",
		         subject:subject){
		    from(address:from)
		    cc(address:cc)
		    to(address:to)
		    message(message)
		}
	}
	
}
