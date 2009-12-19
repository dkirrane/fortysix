package ch.fortysix.maven.plugin.sender;

import java.io.File;
import java.util.Map;

import org.apache.maven.plugin.logging.Log;

import ch.fortysix.maven.plugin.sender.support.SurefireReport;
import ch.fortysix.maven.plugin.sender.support.TagClassMailContent;
import ch.fortysix.maven.plugin.sender.support.TaglistReport;
import ch.fortysix.maven.plugin.sender.support.TestReportMailContent;
import ch.fortysix.maven.plugin.sender.support.TestSuiteReport;

class SurefireReportSender {
	
	Log log;
	
	SurefireReport surefireReport
	
	Map getMails(File reportDir){
		
		def receiver2Mail = [:]
		
		if(reportDir != null){
			def mailContent = new TestReportMailContent() 
			reportDir.eachFileMatch( ~surefireReport.reportFilePattern ) { reportFile -> 
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
			// if we found a report, we send it to all receivers
			if(mailContent.suiteReports){
				surefireReport.receivers.each{ aReceiver -> 
					receiver2Mail.put(aReceiver, mailContent)
				}
			}
		}
		
		return receiver2Mail
	}
	
}
