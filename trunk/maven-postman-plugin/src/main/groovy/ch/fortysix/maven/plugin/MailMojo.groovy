package ch.fortysix.maven.plugin

import java.io.File;

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
    List tos;

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
     * Should the message be send in case the text/regex was found or when it was not found?
     * @parameter expression="${sendOnMatch}" default-value="true"
     */
	boolean sendOnMatch = true

    /**
     * The file to make the test on.
     * @parameter 
     * @required
     */	
	File fileToTest

    /**
     * Text to be searched for in the file.
     * Either 'text' or 'regex' must be given.
     * @parameter 
     */
	String text

    /**
     * Regex to check the text in the file with.
     * Either 'text' or 'regex' must be given.
     * @parameter 
     */
	String regex
	

    void execute() {
		validate()
    	log.info("seding mail...")

    	try{
    		if(log.isDebugEnabled()){
    			
    		}
    		
	    	FileContainsVoter voter = new FileContainsVoter(
						fileToTest: fileToTest,
						text: text,
						regex: regex,
						voteTrueOnPositiveTest: sendOnMatch
					)
	
	    	if(voter.vote()){
		    	MailSender sender = new MailSender(from: from, 
		        		subject: subject, 
		        		message: message, 
		        		mailhost: mailhost, 
		        		mailport: mailport, 
		        		failonerror:failonerror,
		        		recivers: tos)
		    	sender.sendMail()
	    	}
	    	
    	}catch (Exception e) {
			log.error("failed executing postman plugin", e)
			if(!failonerror){
				throw e
			}
		}
    }


	void validate(){
		if(!text && !regex){
			throw new IllegalArgumentException("[postman]: 'text' or 'regex' must be given");
		}
		if(text && regex){
			throw new IllegalArgumentException("[postman]: only one of 'text' or 'regex' must be given");
		}		
	}
}
