/**
 * 
 */
package ch.fortysix.maven.report.support

import java.util.List;
import java.util.ResourceBundle;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

/**
 * @author Domi
 *
 */
class SinkReporter {
	
	def bodyGenerator;
	
	Log log;
	
	public void doGenerateReport( ResourceBundle bundle, Sink sink, String nlsPrefix, Log log)
	throws MojoExecutionException {
		
		sinkBeginReport( sink, bundle, nlsPrefix );
		
		if(bodyGenerator){
			bodyGenerator.generateBody(sink)
		}else{
			sink.text "Error: no Bodygenerator set!" 
		}
		
		sinkEndReport( sink );
	}
	
	private void sinkBeginReport( Sink sink, ResourceBundle bundle, String nlsPrefix ) {
		sink.head();
		
		sink.title();
		sink.text( bundle.getString( nlsPrefix + "header" ) );
		sink.title_();
		
		sink.head_();
		
		sink.body();
		
		sink.section1();
		
		sinkSectionTitle1( sink, bundle.getString( nlsPrefix + "header" ) );
	}
	
	private void sinkEndReport( Sink sink ) {
		sink.section1_();
		
		sink.body_();
		
		sink.flush();
		
		sink.close();
	}
	
	private void sinkSectionTitle1( Sink sink, String text ) {
		sink.sectionTitle1();
		
		sink.text( text );
		
		sink.sectionTitle1_();
	}	
}
