package ch.fortysix.maven.plugin


import org.apache.maven.shared.model.fileset.util.FileSetManager 
import org.codehaus.gmaven.mojo.GroovyMojo;

import groovy.xml.MarkupBuilder


/**
 * Collects the information defined in the rules and creates a report file for further processing.
 *
 * @goal collect
 */
class FileWalkerMojo extends GroovyMojo {
	
	/**
	 * @parameter expression="${session}"
	 * @required
	 * @readonly
	 */
	org.apache.maven.execution.MavenSession session
	
	/**
	 * Encoding of the source
	 * Advice taken from http://docs.codehaus.org/display/MAVENUSER/POM+Element+for+Source+File+Encoding
	 * @parameter expression="${encoding}" default-value="${project.build.sourceEncoding}"
	 */
	private String sourceEncoding;
	
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
	 * 	 	<receivers>	 
	 * 		  <receiver>developerId</receiver>
	 * 		  <receiver>sam@yy.com</receiver>
	 * 	 	</receivers>
	 * 	</rule>
	 * </rules>
	 * </pre>
	 * @parameter
	 * @required
	 */	
	private List rules
	
	private List results = new ArrayList()
	
	/**
	 * writes the results of the collected information to XML
	 */
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
					
					aRule.receivers.each { aRevicer -> 
						receiver aRevicer
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
	 * Returns the lines matching the given regex in the file
	 * @param regex the regex to match
	 * @param fileToTest the file to test
	 * @return list of matching lines
	 */
	List getMatches(String regex, File fileToTest){
		List result = null
		def text = fileToTest?.getText(sourceEncoding)
		getLog().debug "search with regex: $regex, fileHasText? "+(text != null)
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
	
	/**
	 * execute the MOJO
	 */
	void execute() {
		init()
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
		
	}
	
	/**
	 * Initializes the MOJO.
	 * If sourceEncoding is not set, we use system 'file.encoding'.
	 */
	void init(){
		if(!sourceEncoding){
			sourceEncoding = session.getExecutionProperties()."file.encoding"
			getLog().warn "Using platform sourceEncoding ($sourceEncoding actually) to copy filtered resources, i.e. build is platform dependent!"
		}
	}
	
}
