package ch.fortysix.maven.report.criterion

/**
 * 
 */

import groovy.xml.MarkupBuilder;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;
import org.apache.maven.shared.model.fileset.util.FileSetManager;

import ch.fortysix.maven.report.AbstractReportMojo;
import ch.fortysix.maven.report.support.HtmlReporter;
import ch.fortysix.maven.report.taglist.TaglistReportBodyGenerator;

/**
 * @author Domi
 * @goal postman-criterion-report
 * @phase site
 */
class PostmanCriterionReportMojortMojo extends AbstractReportMojo {
	
	/**
	 * The xml file to read/write the postman report from/to.
	 * @parameter default-value="${project.build.directory}/postman.xml"
	 */
	File reportFile;
	
	/**
	 * A list of <code>rule</code> elements defining when and where to send a message.
	 * You might use the id of developer in the pom or an email directly. 
	 * <pre>
	 * &lt;rules&gt;								
	 * 	&lt;rule&gt;
	 * 		&lt;regex&gt;.*(author).*&lt;/regex&gt;
	 * 	 	&lt;receivers&gt;	 
	 * 		  &lt;receiver&gt;developerId&lt;/receiver&gt;
	 * 		  &lt;receiver&gt;sam@yy.com&lt;/receiver&gt;
	 * 	 	&lt;/receivers&gt;
	 * 	&lt;/rule&gt;
	 * &lt;/rules&gt;
	 * </pre>
	 * @parameter
	 * @required
	 */	
	private List rules	
	
	/**
	 * A list of <code>fileSet</code>s to select the files to be analyzed.
	 *
	 * @parameter
	 */
	private List filesets
	
	String getNlsPrefix(){
		"report.postman.criterion."
	}
	
	public String getOutputName() {
		"postman-criterion-report"
	}	
	
	protected void executeReport(Locale locale) throws MavenReportException {
		// init the mojo
		init()
		
		// analyze the files
		prepareReportXml()
		
		def receiver2Mail = [:]
		
		if(reportFile && reportFile.isFile()){
			CriterionMailCollector sender = new CriterionMailCollector(log: getLog())
			receiver2Mail = sender.getMails(reportFile)
			receiver2Mail.each sendReport
		}else{
			getLog().warn "ReportFile is not readable ($reportFile)!"
			skipMails = true
		}
		
		if(!skipMails){
			receiver2Mail.each sendReport
		} else{
			log.info "postman skips sending mails!"
		}
		
		def report = new HtmlReporter(bodyGenerator: new CriterionReportBodyGeneratorerator(receiver2TestReport: receiver2Mail))
		report.doGenerateReport( getBundle( locale ), getSink(), nlsPrefix, getLog() )
	}
	
	
	void prepareReportXml(){
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
	
}
