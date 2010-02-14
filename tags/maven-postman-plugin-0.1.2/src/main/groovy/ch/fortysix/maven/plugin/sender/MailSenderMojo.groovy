/**
 * 
 */
package ch.fortysix.maven.plugin.sender;

import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.shared.model.fileset.util.FileSetManager;
import org.codehaus.gmaven.mojo.GroovyMojo;

import ch.fortysix.maven.plugin.util.MailSenderContext;
import ch.fortysix.maven.report.support.MailSender;

/**
 * Sends a mail with optional attachments.
 * @author Domi
 * @goal send-mail
 */
class MailSenderMojo extends AbstractSenderMojo {
	
	/**
	 * A list of <code>attachmentSet</code>s to be attached to the mail.
	 * It is the same as a fileSet and also supports 'includes' and 'excludes'.
	 *
	 * @parameter
	 */
	private List attachmentSets
	
	private MailSenderContext context 
	
	/* (non-Javadoc)
	 * @see org.apache.maven.plugin.Mojo#execute()
	 */
	public void execute() throws MojoExecutionException, MojoFailureException {
		// create a mailsender
		MailSender mailSender = new MailSender(
		mailcontenttype: mailcontenttype,
		multipartSupported: multipartSupported,
		mailAltConfig: mailAltConfig,
		log: getLog(),
		failonerror: failonerror,
		ssl: mailssl,
		user: mailuser,
		password: mailpassword,
		mailhost: mailhost, 
		mailport: mailport)				
		
		
		context = new MailSenderContext(
				session: session, 
				project: project,
				log: log, 
				skipMails: skipMails,
				multipartSupported: multipartSupported,
				mailSender: mailSender,
				)
		
		
		context.run this
	}
	
	
	public void executeInternal(){
		def filesToAttach = []
		if(attachmentSets){
			FileSetManager fileSetManager = new FileSetManager(getLog())
			attachmentSets.each{
				def allIncludes = convertToFileList(it.getDirectory(), fileSetManager.getIncludedFiles( it ))
				allIncludes.each{ oneFile ->
					
					if(oneFile.exists()){
						getLog().info "add attachment $oneFile"
						filesToAttach << [url: oneFile.toURL(), name: oneFile.name, description: oneFile.name]
					}else{
						getLog().warn "attachment $oneFile does not exist"	
					}
				}
			}	
		}
		// prepare mail body, file content overrules plain text
		def htmlBody = htmlMessageFile?.text ? htmlMessageFile?.text : htmlMessage
		def txtBody =  textMessageFile?.text ? textMessageFile?.text : textMessage
		
		def mail = [receivers: receivers, from: from, subject: subject, text: txtBody, html: htmlBody, attachments: filesToAttach]
		
		def mails = [mail]
		mails.each context.sendReport 
	}
	
	
	/**
	 * Prepends the given list of file paths with the directory and creates returns the resulting files in a collection.
	 * @param dir the directory
	 * @param relativeFiles files relative to the given directory
	 * @return a list of files
	 */
	List convertToFileList(String dir, String[] relativeFiles){
		List all = new ArrayList(relativeFiles.length)
		relativeFiles.each{
			all.add(new File(dir, it))
		}
		return all
	}	
	
}
