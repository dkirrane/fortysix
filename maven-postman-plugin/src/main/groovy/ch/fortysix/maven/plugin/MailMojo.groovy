package ch.fortysix.maven.plugin

import org.codehaus.gmaven.mojo.GroovyMojo;

/**
 * Mojo enabling the conditional sending of email
 *
 * @goal send
 */
class MailMojo
    extends GroovyMojo
{
    /**
     * The hello message to display.
     *
     * @parameter expression="${message}" default-value="message from maven build"
     */
    String message

    /**
     * Email subject line. 
     * @parameter expression="${subject}" default-value="maven build"
     */
    String subject;

    /**
     * Email address of sender.
     * @parameter expression="${from}" 
     * @required
     */
    String from;
    
    /**
     * a list of mail recivers.
     * @parameter 
     * @required
     */
    List recivers;

    /**
     * flag to indicate whether to halt the build on any error. The default value is true.
     * @parameter expression="${failonerror}" default-value="true"
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
    	log.info("seding mail...")
    	
    	MailSender sender = new MailSender(from: from, 
        		subject: subject, 
        		message: message, 
        		mailhost: mailhost, 
        		mailport: mailport, 
        		failonerror:failonerror,
        		recivers: recivers)
    	sender.sendMail()
    }
}
