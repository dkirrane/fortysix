package ch.fortysix.maven.report.taglist
;

import java.util.Iterator;

import org.apache.maven.doxia.sink.Sink;

import com.sun.mail.imap.protocol.BODY;

class TagClassMailContent {
	def tagsFromReportFile = []
	
	/**
	 * Formats the content and builds the body 
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
	
	/**
	 * Adds the body to the given sink. This will display it on the HTML report.
	 */
	void addToSink(Sink sink){
		
		tagsFromReportFile.each{ tag ->
			sink.text tag.@count.toString()
			sink.text " comments for tagClass: "
			sink.bold()
			sink.text tag.@name?.toString()
			sink.bold_()
			sink.paragraph()
			
			sink.definitionList()
			tag.files.file.eachWithIndex{ file, i ->
				sink.definitionListItem()
				sink.text "file: "
				sink.text file.@name.toString()
				sink.table()
				file.comments.comment.each{
					sink.tableRow()
					sink.tableCell() 
					sink.text "at line: $it.lineNumber"
					sink.tableCell_()
					sink.tableCell() 
					sink.text it.comment.toString()
					sink.tableCell_()					
					sink.tableRow()
				}
				sink.table_()
				sink.definitionListItem_()
			}
			sink.definitionList_()
		}
	}
	
	Iterator iterator(){
		tagsFromReportFile?.iterator()
	}
	
}
