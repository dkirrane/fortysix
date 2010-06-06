/**
 * 
 */
package ch.fortysix.maven.report.support;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * @author Domi
 *
 */
class MailAuthenticator extends Authenticator {
	
	String userName
	String password
	
	protected PasswordAuthentication getPasswordAuthentication()
	{
		assert userName, 'userName not set'
		assert password, 'password not set'
		return new PasswordAuthentication( userName, password );
	}
}
