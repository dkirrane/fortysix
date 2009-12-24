/**
 * 
 */
package ch.fortysix.maven.report;

import java.io.File;
import java.util.Locale;

import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;

/**
 * @author Domi
 *
 */
abstract class AbstractReportMojo extends AbstractMavenReport {
	
	/**
	 * Report output directory. Note that this parameter is only relevant if the goal is run from the command line or
	 * from the default build lifecycle. If the goal is run indirectly as part of a site generation, the output
	 * directory configured in the Maven Site Plugin is used instead.
	 * 
	 * @parameter default-value="${project.reporting.outputDirectory}"
	 */
	private File outputDirectory;	
	
	/**
	 * @see org.apache.maven.reporting.AbstractMavenReport#getOutputDirectory()
	 */
	protected String getOutputDirectory() {
		return outputDirectory.getAbsolutePath();
	}
	
	/**
	 * @see org.apache.maven.reporting.AbstractMavenReport#getProject()
	 */
	protected MavenProject getProject() {
		return project;
	}
	
	/**
	 * @see org.apache.maven.reporting.AbstractMavenReport#getSiteRenderer()
	 */
	protected Renderer getSiteRenderer() {
		return siteRenderer;
	}

	
}
