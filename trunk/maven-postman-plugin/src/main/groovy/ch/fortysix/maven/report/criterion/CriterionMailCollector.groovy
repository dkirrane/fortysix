/**
 * 
 */
package ch.fortysix.maven.report.criterion
;

import java.io.File;

import org.apache.maven.plugin.logging.Log;

import ch.fortysix.maven.report.support.HtmlExtractor;


/**
 * @author Domi
 *
 */
class CriterionMailCollector {
	
	Log log;
	
	Map getMails(File reportFile){
		
		def xmlText = reportFile?.text
		def receiver2Rules = [:]
		
		if(xmlText != null){
			def report = new XmlSlurper().parseText(xmlText)
			
			log.info("postman: prepare postman mails...")
			
			try{
				
				// we collect all receivers mention in the report
				def allReceivers = report.rule.receiver*.text().unique()
				
				
				allReceivers.each{ aReceiver ->
					def rulesForReceiver = report.rule.findAll {
						// get all receivers within one rule
						def receiversForRule = it.receiver*.text()
						// check if the list contains the current receiver
						receiversForRule.contains(aReceiver)
					}
					if(getLog().isDebugEnabled()){
						rulesForReceiver.each{getLog().debug "send this to [$aReceiver]: "+it}
					}
					def mailContent = new PostmanReportMailContent(rules: rulesForReceiver)
					receiver2Rules.put aReceiver, mailContent 
				}
				
			}catch (Exception e) {
				log.error("postman failed prepare mails", e)
				if(failonerror){
					throw e
				}
			}
		}	
		return receiver2Rules
	}
	
}
