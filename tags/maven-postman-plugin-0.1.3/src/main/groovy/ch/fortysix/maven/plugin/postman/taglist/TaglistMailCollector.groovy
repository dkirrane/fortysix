package ch.fortysix.maven.plugin.postman.taglist

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.logging.Log;

import ch.fortysix.maven.report.support.HtmlExtractor

class TaglistMailCollector {
	
	Log log;
	
	/**
	 * The mails subject
	 */
	def subject
	
	/**
	 * sender address
	 */
	def fromAddress
	
	/**
	 * Maps a taglist tag (by displayName) to a set of receivers.
	 */
	List tagClasses
	
	/**
	 * A set of receivers who wanted to get notified about everything
	 */
	Set generalReceivers
	
	/**
	 * The xml report of the <code>taglist-maven-plugin</code>.
	 */
	File taglistReportXml
	
	/**
	 * The html report of the <code>taglist-maven-plugin</code>.
	 */
	File taglistReportHtml
	
	HtmlExtractor htmlExtractor = new HtmlExtractor()
	
	List getMails(){
		
		log.info("postman: prepare taglist mails...")
		
		def xmlText 
		if(taglistReportXml && taglistReportXml.exists()){
			xmlText = taglistReportXml?.text	
		}else{
			log.warn "could not load taglist report, file does not exist ($taglistReportXml)"
		}
		
		def mails = []
		
		if(xmlText){
			
			def report
			try{ 
				report = new XmlSlurper().parseText(xmlText)
			}catch (Exception e){
				log.warn "could not load taglist report, seems not to be a valid report file! ($taglistReportXml)"
				return mails
			}
			
			// uniquely get all receivers (in a Set)
			def allReceivers = tagClasses?.receivers.flatten{} as Set
			def receiver2DisplayNames = [:]
			allReceivers.each{ aReceiver -> 
				def displayNames = tagClasses?.findAll{ tagClass ->
					tagClass.receivers.contains(aReceiver)
				}.displayName
				log.debug "prepare taglist-mail for $aReceiver with displayNames: $displayNames"
				receiver2DisplayNames.put(aReceiver, displayNames)
			}
			
			
			// search all tags for a receiver (by tag tag displayName) 
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
				
				mails << [receivers: [aReceiver], subject: subject, from: fromAddress, text: asText(tags4Receiver), html: html]
			}
			
			if(generalReceivers){
				log.debug "add mails for general receivers: $generalReceivers"
				mails << getMailForGeneralReceivers(report)
			}
		}
		
		if(log.isDebugEnabled()){
			mails.each { log.debug it }
		}
		
		return mails
	}
	
	
	private Map getMailForGeneralReceivers(def report){
		if(report?.tags?.tag){
			// get the html of the generated taglist report (taglist-plugin)
			def html
			if(taglistReportHtml.text){
				html = htmlExtractor.extractHTMLTagById(html: taglistReportHtml.text, tagName: "div", tagId: "bodyColumn")
			}			
			return [receivers: generalReceivers as List, subject: subject, from: fromAddress, text: "Taglist plugin found some tags, please check report", html: html]
		}
		return null
	}
	
	
	/**
	 * Gets the tags as a string message 
	 */
	private String asText(def tags4Receiver){
		def body = new StringBuilder()
		tags4Receiver.each{ tag ->
			body << "\n"
			body << tag.@count << " comments for tagClass: '"<< tag.@name << "'"
			body << "\n"
			tag.files.file.eachWithIndex{ file, i ->
				body << (i+1) << ". file: " << file.@name << "\n"
				file.comments.comment.each{
					body << "\tLineNumber: " << it.lineNumber << ", Comment: "<< it.comment << "\n" 
				}
			}
			
		}
		return body.toString()
	}
	
}
