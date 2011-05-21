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
				
				def errors = testsuite?.@errors?.text() ? testsuite.@errors.toString() as Integer : 0
				def skipped = testsuite?.@skipped?.text() ? testsuite?.@skipped?.toString() as Integer : 0
				def failures = testsuite?.@failures?.text() ? testsuite.@failures.toString() as Integer : 0
				def tests = testsuite?.@tests?.text() ? testsuite.@tests.toString() as Integer : 0
				
				log?.debug "found ${testsuite.@name}: errors=$errors, skipped=$skipped, failures=$failures, tests=$tests"
				
				def suiteReport = new TestSuiteReport(
							name: testsuite.@name, 
							errors: errors,
							skipped: skipped,
							failures: failures,
							tests: tests
						)
				suiteReports << suiteReport
			}
		}else{
			log?.warn "report directory $reportDir does not exist"
		}
		return suiteReports
	}
}
