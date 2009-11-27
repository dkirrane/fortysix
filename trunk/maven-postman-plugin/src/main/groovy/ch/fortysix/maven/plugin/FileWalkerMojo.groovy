package ch.fortysix.maven.plugin

import java.util.regex.Pattern;

import org.apache.maven.shared.model.fileset.util.FileSetManager 
import org.apache.maven.shared.model.fileset.FileSet
import org.codehaus.gmaven.mojo.GroovyMojo;

/**
 * Mojo traversing and parsing files
 *
 * @goal walk
 */
class FileWalkerMojo extends GroovyMojo {
	
	/**
	 * A list of <code>fileSet</code> rules to select files and directories.
	 *
	 * @parameter
	 */
	private List filesets
	
	boolean voteTrueOnPositiveTest = true
	
	/**
	 * A list of <code>rule</code> elements defining when and where to send a message
	 *
	 * @parameter
	 * @required
	 */	
	private List rules
	
	
	private List results = new ArrayList()
	
	void execute() {
		
		FileSetManager fileSetManager = new FileSetManager(getLog())
		rules.each{ 
			
			getLog().debug "evaluate rule: $it"
			def rule = it
			// one message for each rule
			def result = new RuleResultInfo(rule: rule)
			
			filesets.each{
				def allIncludes = convertToFileList(it.getDirectory(), fileSetManager.getIncludedFiles( it ))
				allIncludes .each{
					getLog().debug "get matches for $it"
					List matches = getMatches(rule.regex, it)
					if(matches.size() > 0){
						
						// we found matches in the file, save the info
						def info = ""
						matches.each{  
							println "Found in file: $it"
							info << it << "\n"
						}
						result.addMessage it.name, info
						
					}
				}
			}
			
			results.add result
			
		}
		
		Set messageRecivers = new HashSet()
		
		def l = (rules*.recivers) 
		
		println "L="+l.class
		
		def s = l as Set
		println "S="+s.class
		
		l.unique().each{
			println "**************->"+it
		}
		
		
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
