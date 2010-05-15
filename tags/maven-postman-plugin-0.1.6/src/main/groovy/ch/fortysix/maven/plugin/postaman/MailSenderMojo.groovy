/**
 * 
 */
package ch.fortysix.maven.plugin.postaman;

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
	 * A list of <code>fileSet</code>s to be attached to the mail.
	 *
	 * @parameter
	 */
	private List fileSets
	
	/**
	 * nothing to prepare
	 */
	protected boolean prepareMojo(){
		true
	}
	
	
	/* (non-Javadoc)
	 * @see org.apache.maven.plugin.Mojo#execute()
	 */
	public void executeMojo() throws MojoExecutionException, MojoFailureException {
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
				multipartSupported: multipartSupported,
				mailSender: mailSender,
				)
		
		
		
		def mails = this.collectMails()
		context.run mails
	}
	
	
	public List collectMails(){
		def filesToAttach = []
		if(fileSets){
			FileSetManager fileSetManager = new FileSetManager(getLog())
			fileSets.each{
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
		
		return [[receivers: receivers, from: from, subject: subject, text: txtBody, html: htmlBody, attachments: filesToAttach]]
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
