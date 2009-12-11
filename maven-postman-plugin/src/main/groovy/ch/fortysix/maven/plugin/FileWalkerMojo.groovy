package ch.fortysix.maven.plugin


import org.apache.maven.shared.model.fileset.util.FileSetManager 
import org.codehaus.gmaven.mojo.GroovyMojo;

import groovy.xml.MarkupBuilder


/**
 * Mojo traversing and parsing files
 *
 * @goal collect
 */
class FileWalkerMojo extends GroovyMojo {
	
	/**
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	org.apache.maven.project.MavenProject project
	
	/**
	 * The xml file to write the report information to. It is used to send mails and create a report page.
	 * @parameter default-value="${project.build.directory}/postman.xml"
	 */
	File reportFile;
	
	/**
	 * A list of <code>fileSet</code> rules to select files and directories.
	 *
	 * @parameter
	 */
	private List filesets
	
	/**
	 * A list of <code>rule</code> elements defining when and where to send a message.
	 * You might use the id of developer in the pom or an email directly. 
	 * <pre>
	 * <rules>								
	 * 	<rule>
	 * 		<regex>.*(author).*</regex>
	 * 	 	<recivers>	 
	 * 		  <reciver>developerId</reciver>
	 * 		  <reciver>sam@yy.com</reciver>
	 * 	 	</recivers>
	 * 	</rule>
	 * </rules>
	 * </pre>
	 * @parameter
	 * @required
	 */	
	private List rules
	
	private List results = new ArrayList()
	
	def writeReport2Xml = { rule2resultSet ->
		def writer = new StringWriter()
		def xml = new MarkupBuilder(writer)
		xml.report (date: new Date()){
			
			rule2resultSet.each { aRule, resultSet ->
				rule{
					regex(''){
						// access writer directly, because of character escaping of CDATA section
						writer.write "<![CDATA[$aRule.regex]]>"
					}
					
					aRule.recivers.each { aRevicer -> 
						reciver aRevicer
					}
					resultSet.results.each { aResult -> 
						result(){
							file(''){
								// access writer directly, because of character escaping of CDATA section
								writer.write "<![CDATA[$aResult.file]]>"
							}
							aResult.machedGroupLists.each{ groupList ->
								// concatenate the found groups to a string 
								def groups = groupList.join(', ')
								match(''){
									// access writer directly, because of character escaping of CDATA section
									writer.write "<![CDATA[$groups]]>"
								}
							}
						}
					}
				}
			}
			
			
		}
		writer.toString()
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
	
	void execute() {
		
		def rule2resultSet = [:]
		
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
						def result = new Result(file: oneFile)
						matches.each{ groups ->
							result.addMatchGroups(groups)
						}
						resultSet.addResult result
						
					}
				}
			}
			
			rule2resultSet.put rule, resultSet
			
		}
		
		getLog().debug "write reportFile: $reportFile.absolutePath"
		reportFile.write writeReport2Xml(rule2resultSet), "UTF-8"
		
		println writeReport2Xml(rule2resultSet)
		
	}
	
}
