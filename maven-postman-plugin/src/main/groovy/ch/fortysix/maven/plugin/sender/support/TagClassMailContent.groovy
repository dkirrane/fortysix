package ch.fortysix.maven.plugin.sender.support;

import java.util.Iterator;

import com.sun.mail.imap.protocol.BODY;

class TagClassMailContent {
	def tagsFromReportFile = []
	
	/**
	 * Formats the content and build the body 
	 */
	String asMailBody(){
		def body = new StringBuilder()
		tagsFromReportFile.each{ tag ->
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
	
	Iterator iterator(){
		tagsFromReportFile?.iterator()
	}
	
}
