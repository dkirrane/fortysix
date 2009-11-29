package ch.fortysix.maven.plugin

import java.util.regex.Pattern;

import org.apache.maven.shared.model.fileset.util.FileSetManager 
import org.apache.maven.shared.model.fileset.FileSet
import org.codehaus.gmaven.mojo.GroovyMojo;

/**
 * Mojo traversing and parsing files
 *
 * @goal deliver
 */
class FileWalkerMojo extends GroovyMojo {
	
	/**
	 * Host name of the SMTP server. The default value is localhost.
	 * @parameter expression="${postman.mailhost}" default-value="localhost"
	 */
	String mailhost;
	
	/**
	 * TCP port of the SMTP server. The default value is 25.
	 * @parameter expression="${postman.mailport}" default-value="25"
	 */
	String mailport;	
	
	/**
	 * A list of <code>fileSet</code> rules to select files and directories.
	 *
	 * @parameter
	 */
	private List filesets
	
	/**
	 * A list of <code>rule</code> elements defining when and where to send a message
	 * <pre>
	 * <rules>								
	 * 	<rule>
	 * 		<regex>.*(author).*</regex>
	 * 	 	<recivers>	 
	 * 		  <reciver>dude@xx.com</reciver>
	 * 		  <reciver>sam@yy.com</reciver>
	 * 	 	</recivers>
	 * 	</rule>
	 * </rules>
	 * </pre>
	 * @parameter
	 * @required
	 */	
	private List rules
	
	/**
	 * flag to indicate whether to halt the build on any error. The default value is true.
	 * @parameter default-value="true"
	 */
	boolean failonerror = true;
	
	/**
	 * Email address of sender.
	 * @parameter expression="${postman.from}" 
	 * @required
	 */
	String from;	
	
	
	private List results = new ArrayList()
	
	void execute() {
		
		// prepare the messages for the recivers
		Map recivers2Msg = new HashMap()
		rules*.recivers*.each{ 
			recivers2Msg.put( it, new Message()	) 
		}
		
		FileSetManager fileSetManager = new FileSetManager(getLog())
		rules.each{ rule ->
			
			getLog().debug "evaluate rule: $rule"
			// one message for each rule
			def resultSet = new RuleResultSet(rule: rule)
			
			filesets.each{
				def allIncludes = convertToFileList(it.getDirectory(), fileSetManager.getIncludedFiles( it ))
				allIncludes.each{ oneFile ->
					getLog().debug "get matches in $oneFile"
					List matches = getMatches(rule.regex, oneFile)
					if(matches){
						
						// we found matches in the file, save the info
						def info = new StringBuilder()
						matches.eachWithIndex { txt, i ->
							def id = i+1
							getLog().info "Found in file: $txt"
							info << "$id - $txt\n"
						}
						resultSet.addResult oneFile.getCanonicalPath(), info.toString()
						
					}
				}
			}
			
			// add the result to each reciver's (of this rule) message
			rule.recivers.each{
				recivers2Msg.get(it)?.resultSets?.add(resultSet)
			}
			
		}
		
		//send all the messages to the recivers
		recivers2Msg.each { key, message ->
			sendMsg(key, message)
		}
		
		
	}
	
	
	/**
	 * sends a message
	 */
	def sendMsg = { to, message ->
		
		def msgBody = new StringBuilder()
		message.resultSets*.each{ resultSet ->
			
			// add rule teaser
			msgBody << "Results for rule:" << resultSet.rule?.definition() << "\n"
			
			// add results for rule
			resultSet.results.each{file, text -> 
				msgBody << "$file:\n$text\n"
			}
		}
		
		println msgBody
		
		List tos = new ArrayList()
		tos.add to
		
		MailSender sender = new MailSender(recivers: tos, 
		subject: message.subject, 
		message: msgBody, 
		mailhost: this.mailhost, 
		mailport: this.mailport, 
		failonerror: this.failonerror,
		from: this.from
		)
		
		sender.sendMail()
	}
	
	
	/**
	 * Returns the lines matching the given regex in the file
	 * @param regex the regex to match
	 * @param fileToTest the file to test
	 * @return list of matching lines
	 */
	List getMatches(String regex, File fileToTest){
		List result = null
		def text = fileToTest?.text
		getLog().debug "search with regex: $regex, fileHasText?"+(text != null)
		if(text != null){
			def matcher = text =~ regex
			result = matcher.collect { it }
		}
		return result
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
