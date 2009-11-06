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

	List recivers = ["domi5@dodo.ch", "domi4@dodo.ch", "domi3@dodo.ch", "domi2@dodo.ch", "domi1@dodo.ch" ]

	void sendMail(){

	    
		ant.mail(mailhost:mailhost, mailport:"$mailport",
		         subject:subject){
			
			recivers.each{
					to(address:it)
				}
			
		    from(address:from)
		    cc(address:cc)
		    message(message)
		}
	}
	
}
