/**
 * 
 */
package ch.fortysix.maven.report.criterion

import java.io.File

import org.apache.maven.doxia.sink.Sink;

import ch.fortysix.maven.report.HtmlSnipplet;
import ch.fortysix.maven.report.SinkSnipplet;
import ch.fortysix.maven.report.TextSnipplet;

/**
 * @author Domi
 *
 */
class PostmanReportMailContent implements HtmlSnipplet, TextSnipplet, SinkSnipplet{
	
	def rules = []
	
	def htmlFragment
	
	String html(){
		htmlFragment
	}
	
	String text(){
		def body = new StringBuilder()
		rules.each{	tag -> 
			body << "\n"
			body << "Rule: '" << tag.regex << "'"
			body << "\n"
			tag.result.eachWithIndex{ result, i ->
				body << (i+1) << ". file: " << result.file << "\n"
				result.match.each{
					body << "\tMatch: " << it << "\n" 
				}
			}
		}
		
		body.toString()
	}
	
	void addToSink(Sink sink){
		sink.table()
		sink.tableRow()
		rules.each{ tag ->
			sink.tableRow()
			sink.tableCell()
			sink.text tag.regex.text()
			tag.result.eachWithIndex{ result, i ->
				sink.table()
				sink.tableRow()
				sink.tableCell()
				sink.text "$i file: " 
				sink.text result.file.text() 
				//				result.match.each{
				//					body << "\tMatch: " << it << "\n" 
				//				}
				sink.tableCell_()
				sink.tableRow_()
			}			
			sink.tableCell_()
			sink.tableRow_()
		}
		sink.table_()
	}	
}
