/**
 * 
 */
package ch.fortysix.maven.plugin.postaman.surfire;

import java.io.File;
import java.util.List;

import org.apache.maven.plugin.logging.Log;

/**
 * @author Domi
 *
 */
class TestReportUtil {
	
	Log log
	
	List<ch.fortysix.maven.plugin.postaman.surfire.TestSuiteReport> getTestSuiteReport(File reportDir, String reportFilePattern){
		
		log?.info("analyze: surefire reports...")
		
		def suiteReports  = [] 
		if(reportDir && reportDir.exists()){
			reportDir.eachFileMatch( ~reportFilePattern ) { reportFile -> 
				log?.debug "-->$reportFile" 
				def xmlText = reportFile?.text
				def testsuite = new XmlSlurper().parseText(xmlText)
				def suiteReport = new TestSuiteReport(
						name: testsuite.@name, 
						errors: testsuite.@errors.toString() as Integer,
						skipped: testsuite.@skipped.toString() as Integer,
						failures: testsuite.@failures.toString() as Integer,
						tests: testsuite.@tests.toString() as Integer
						)
				suiteReports << suiteReport
			}
		}
		return suiteReports
	}
}
