package ch.fortysix.maven.plugin.postaman.surfire

;

import java.io.File;

import org.apache.maven.plugin.logging.Log;

import ch.fortysix.maven.report.support.HtmlExtractor;

class SurefireMailCollector {
	
	String reportFilePattern
	
	File surefireReportHtml
	
	Log log;
	
	HtmlExtractor htmlExtractor = new HtmlExtractor()
	
	TestReportMailContent getSingleMail(File reportDir){
		
		def mailContent
		
		if(reportDir && reportDir.exists()){
			
			def html
			if(surefireReportHtml && surefireReportHtml.exists()){
				// get the html of the generated surefire report (maven-surefire-report-plugin)
				if(surefireReportHtml.text){
					html = htmlExtractor.extractHTMLTagById(html: surefireReportHtml.text, tagName: "div", tagId: "bodyColumn")
				}
				
			} else{
				log.warn "can't include HTML report to postman-surefire mail ($surefireReportHtml not found)"
			}
			
			log.info("analyze: surefire reports...")
			
			mailContent = new TestReportMailContent(htmlFragment: html) 
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
			
		}
		
		return mailContent
		
	}
	
}
