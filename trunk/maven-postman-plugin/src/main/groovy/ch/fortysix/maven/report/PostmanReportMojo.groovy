/**
 * 
 */
package ch.fortysix.maven.report;

import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;

/**
 * @author Domi
 * @goal postman-report
 * @phase site
 */
class PostmanReportMojo extends AbstractReportMojo {
	
	protected void executeReport(Locale locale) throws MavenReportException {
		def report = new HtmlReporter()
		report.doGenerateEmptyReport( getBundle( locale ), getSink(), "postaman" )
	}
	
	public String getDescription(Locale locale) {
		getBundle( locale ).getString( "report.postman.description" )
	}
	
	public String getName(Locale locale) {
		return getBundle( locale ).getString( "report.postman.name" )
	}
	
	public String getOutputName() {
		"postman-report"
	}
	
	private ResourceBundle getBundle( Locale locale ) {
		return ResourceBundle.getBundle( "postaman-report", locale, this.getClass().getClassLoader() )
	}
	
}
