package ch.fortysix.maven.report.taglist
;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.logging.Log;

import ch.fortysix.maven.plugin.sender.support.TagClassMailContent;
import ch.fortysix.maven.plugin.sender.support.TaglistReport;

class TaglistReportSender {
	
	Log log;
	
	List tagClasses
	
	Map getMails(File reportFile){
		
		log.info("prepare taglist mails...")
		
		def xmlText 
		if(reportFile && reportFile.exists()){
			xmlText = reportFile?.text	
		}else{
			log.warn "could not load taglist report, file does not exist ($reportFile)"
		}
		
		def receiver2Mail = [:]
		
		if(xmlText){
			
			def report
			try{ 
				report = new XmlSlurper().parseText(xmlText)
			}catch (Exception e){
				log.warn "could not load taglist report, seems not to be a valid report file! ($reportFile)"
				return receiver2Mail
			}
			
			// uniquely get all receivers (in a Set)
			def allReceivers = tagClasses?.receivers.flatten{
			} as Set
			def receiver2DisplayNames = [:]
			allReceivers.each{ aReceiver -> 
				def displayNames = tagClasses?.findAll{ tagClass ->
					tagClass.receivers.contains(aReceiver)
				}.displayName
				log.debug "prepare taglist-mail for $aReceiver with displayNames: $displayNames"
				receiver2DisplayNames.put(aReceiver, displayNames)
			}
			
			receiver2DisplayNames.each{aReceiver, displayNames ->
				def tags4Receiver = []
				displayNames.eachWithIndex { displayName, i ->
					// the taglist.xml contains only one tag for each tagClass 
					// (therefore we only use 'find{}' and not 'findAll{}'
					tags4Receiver << report.tags.tag.find { tag ->
						def name = tag['@name']
						log.debug "$name <-> '$displayName'"
						name == displayName 
					}				
				}
				if(log.isDebugEnabled()){
					def s = tags4Receiver.size()
					log.debug "found $s tags for '$aReceiver'"
				}
				// assign a mailcontent for each receiver
				def mailContent = new TagClassMailContent(tagsFromReportFile: tags4Receiver) 
				receiver2Mail.put(aReceiver, mailContent)
			}
		}
		
		if(log.isDebugEnabled()){
			receiver2Mail.each {key, value -> 
				log.debug value.asMailBody()
			}
		}
		
		return receiver2Mail
	}
	
}
