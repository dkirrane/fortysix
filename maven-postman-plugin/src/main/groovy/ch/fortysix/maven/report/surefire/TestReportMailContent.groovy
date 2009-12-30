/**
 * 
 */
package ch.fortysix.maven.report.surefire

import org.apache.maven.doxia.sink.Sink;

import ch.fortysix.maven.report.HtmlSnipplet;
import ch.fortysix.maven.report.SinkSnipplet;
import ch.fortysix.maven.report.TextSnipplet;
;

/**
 * @author Domi
 *
 */
class TestReportMailContent implements HtmlSnipplet, TextSnipplet, SinkSnipplet{
	
	def suiteReports = []
	
	def htmlFragment
	
	String html(){
		htmlFragment
	}
	
	String text(){
		def body = new StringBuilder()
		suiteReports.each{ report -> 
			body << "\n"
			body << report.name << "\n"
		}
		body.toString()
	}
	
	void addToSink(Sink sink){
		sink.table()
		sink.tableRow()
		suiteReports.each{ report ->
			sink.tableRow()
			sink.tableCell()
			sink.text report.name
			sink.tableCell_()
			sink.tableRow_()
		}
		sink.table_()
	}
	
}
