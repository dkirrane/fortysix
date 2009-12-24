/**
 * 
 */
package ch.fortysix.maven.report;

import java.util.ResourceBundle;

import org.apache.maven.doxia.sink.Sink;

/**
 * @author Domi
 *
 */
class HtmlReporter {
	
	private void sinkBeginReport( Sink sink, ResourceBundle bundle, String reportName ) {
		sink.head();
		
		sink.title();
		sink.text( bundle.getString( "report."+reportName+".header" ) );
		sink.title_();
		
		sink.head_();
		
		sink.body();
		
		sink.section1();
		
		sinkSectionTitle1( sink, bundle.getString( "report."+reportName+".header" ) );
	}
	
	private void sinkEndReport( Sink sink ) {
		sink.section1_();
		
		sink.body_();
		
		sink.flush();
		
		sink.close();
	}
}
