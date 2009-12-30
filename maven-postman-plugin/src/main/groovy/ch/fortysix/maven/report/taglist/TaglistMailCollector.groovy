package ch.fortysix.maven.report.taglist
;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.logging.Log;

import ch.fortysix.maven.report.support.HtmlExtractor

class TaglistMailCollector {
	
	Log log;
	
	List tagClasses
	
	HtmlExtractor htmlExtractor = new HtmlExtractor()
	
	Map getMails(File taglistReportXML, File taglistReportHtml){
		
		log.info("postman: prepare taglist mails...")
		
		def xmlText 
		if(taglistReportXML && taglistReportXML.exists()){
			xmlText = taglistReportXML?.text	
		}else{
			log.warn "could not load taglist report, file does not exist ($taglistReportXML)"
		}
		
		def receiver2Mail = [:]
		
		if(xmlText){
			
			def report
			try{ 
				report = new XmlSlurper().parseText(xmlText)
			}catch (Exception e){
				log.warn "could not load taglist report, seems not to be a valid report file! ($taglistReportXML)"
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
				
				// get the html of the generated taglist report (taglist-plugin)
				def html
				if(taglistReportHtml.text){
					html = htmlExtractor.extractHTMLTagById(html: taglistReportHtml.text, tagName: "div", tagId: "bodyColumn")
				}
				
				// assign a mailcontent for each receiver
				def mailContent = new TagClassMailContent(tagsFromReportFile: tags4Receiver, htmlFragment: html) 
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
