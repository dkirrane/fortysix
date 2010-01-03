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
class TestReportMailContent implements HtmlSnipplet, TextSnipplet{
	
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
	
}
