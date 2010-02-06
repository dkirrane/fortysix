package ch.fortysix.maven.report.support

import java.security.Security;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.HtmlEmail;
import org.apache.maven.plugin.logging.Log 

/**
 * @author Domi
 *
 */class MailSender {
	
	Log log
	
	String mailcontenttype
	boolean multipartSupported = true
	
	String mailhost
	String mailport
	String user
	String password
	boolean ssl
	boolean failonerror
	boolean mailAltConfig
	
	
	private def sessionProps
	private def auth
	private session 
	
	void sendMail(Map args){
		assert args.subject 
		assert args.from
		assert args.htmlmessage || args.txtmessage 
		assert args.receivers
		
		HtmlEmail email = new HtmlEmail();
		email.setHostName(mailhost);
		email.setSmtpPort(mailport as Integer);
		
		if(user && !ssl){
			email.setAuthentication(user, password);
		}
		if(ssl && mailAltConfig){
			log.debug "using altMailConfig"
			if(user){
				email.setAuthenticator(new DefaultAuthenticator(user, password));
			}
			email.getMailSession().getProperties().put("mail.smtp.auth", "true");
			email.getMailSession().getProperties().put("mail.debug", log.isDebugEnabled());
			email.getMailSession().getProperties().put("mail.smtp.port", mailport);
			email.getMailSession().getProperties().put("mail.smtp.socketFactory.port", mailport);
			email.getMailSession().getProperties().put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			email.getMailSession().getProperties().put("mail.smtp.socketFactory.fallback", "false");
			email.getMailSession().getProperties().put("mail.smtp.starttls.enable", "true");
		}
		
		if(ssl && ! mailAltConfig){
			log.debug "using default ssl mail config"
			email.setTLS true
			email.setSSL true
		}		
		
		args.receivers.each { email.addTo(it) }		
		email.setFrom(args.from);
		email.setSubject(args.subject);
		
		
		if(multipartSupported && (args.txtmessage || args.htmlmessage)){
			// if multipart is supported, we set text and html
			if(args.txtmessage){
				email.setTextMsg(args.txtmessage);
			}
			if(args.htmlmessage){
				email.setHtmlMsg(args.htmlmessage);
			}
		}else{
			// if multipart is not supported, we prefer html over text
			if(mailcontenttype.contains("html") && args.htmlmessage){
				log.debug "send html mail"
				email.setHtmlMsg(args.htmlmessage);
			}else if(args.txtmessage){
				log.debug "send text mail"
				email.setTextMsg(args.txtmessage);
			}else{
				def msg = "no mail content provided!"
				log.warn msg
				email.setTextMsg(msg);
			}
		}
		
		if(args.attachments){
			args.attachments.each { email.attach(it.url, , it.name, it.description) }
		}
		
		email.setDebug(log.isDebugEnabled());
		
		email.send();
		
	}	
	
	
	/**
	 * Initialize the mail sender.
	 */
	void init(){
		sessionProps = new Properties()
		sessionProps.put("mail.smtp.host", mailhost)
		sessionProps.put("mail.smtp.port", mailport)
		sessionProps.put("mail.debug", log.isDebugEnabled().toString())
		if(user){
			sessionProps.put("mail.smtp.auth", "true")
			auth = new MailAuthenticator(userName: user, password: password)
		}
		if(ssl){
			log.info "configure SSL connection for mail"
			// not sure if we do have this class always available
			Security.addProvider( this.getClass().forName("com.sun.net.ssl.internal.ssl.Provider").newInstance() )
			sessionProps.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory" );
			sessionProps.put("mail.smtp.socketFactory.fallback", "false" );			
			sessionProps.put("mail.smtp.socketFactory.port", mailport );
		}		
	}
	
	
}
