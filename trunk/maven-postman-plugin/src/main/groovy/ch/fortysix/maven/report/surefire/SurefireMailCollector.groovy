package ch.fortysix.maven.report.surefire
;

import java.io.File;
import java.util.Map;

import org.apache.maven.plugin.logging.Log;

class SurefireMailCollector {
	
	String reportFilePattern
	
	Log log;
	
	TestReportMailContent getSingleMail(File reportDir){
		
		def mailContent
		if(reportDir != null){
			
			log.info("postman: prepare surefire mail...")
			
			mailContent = new TestReportMailContent() 
			reportDir.eachFileMatch( ~reportFilePattern ) { reportFile -> 
				log.debug "-->$reportFile" 
				def xmlText = reportFile?.text
				def testsuite = new XmlSlurper().parseText(xmlText)
				def suiteReport = new TestSuiteReport(
						name: testsuite.@name, 
						errors: testsuite.@errors.toString() as Integer,
						skipped: testsuite.@skipped.toString() as Integer,
						failures: testsuite.@failures.toString() as Integer,
						tests: testsuite.@tests.toString() as Integer
						)
				mailContent.suiteReports << suiteReport
			}
			
			return mailContent
			
		}
		
	}
	
}
