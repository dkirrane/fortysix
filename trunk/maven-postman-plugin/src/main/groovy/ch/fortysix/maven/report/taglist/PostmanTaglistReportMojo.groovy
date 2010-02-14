package ch.fortysix.maven.report.taglist

/**
 * 
 */

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.MavenReportException;

import ch.fortysix.maven.report.AbstractReportMojo;
import ch.fortysix.maven.report.support.SinkReporter;
import ch.fortysix.maven.report.surefire.SurefireReportBodyGenerator;

/**
 * This goal is able to send 'reminder mails' based on the findings of the <code>org.codehaus.mojo:taglist-maven-plugin</code>.
 * One should use at least version 2.4 of the taglist-plugin.
 * 
 * @author Domi
 * @goal taglist-mail
 * @phase site
 */
class PostmanTaglistReportMojo extends AbstractReportMojo {
	
	/**
	 * The postfix used in the email subject 
	 * @parameter default-value="taglist notification"
	 */
	String subjectPostFix
	
	String getSubjectPostFix(){
		return subjectPostFix
	}	
	
	String getNlsPrefix(){
		"report.postman.taglist."
	}
	
	public String getOutputName() {
		"postman-taglist-report"
	}	
	
	//	/**
	//	 * Who should receive a mail? One can use an id of a developer registered in the pom or an email address directly.
	//	 * <pre>
	//	 * 	 		&lt;receivers&gt;	 
	//	 * 		  		&lt;receiver&gt;developerId&lt;/receiver&gt;
	//	 * 		  		&lt;receiver&gt;sam@topland.com&lt;/receiver&gt;
	//	 * 	 		&lt;/receivers&gt;
	//	 * </pre>
	//	 * @parameter 
	//	 * @required
	//	 */	
	//	Set receivers;	
	
	/**
	 * The generated taglist report (previously generated by 'taglist-maven-plugin'). 
	 * @parameter default-value="${project.build.directory}/taglist/taglist.xml"
	 */
	File taglistReportXml;
	
	/**
	 * The generated taglist html report (previously generated by 'taglist-maven-plugin'). 
	 * @parameter default-value="${project.build.directory}/site/taglist.html"
	 */
	File taglistReportHtml;
	
	/**
	 * Maps the tag class (from taglist plugin) to a list of receivers. The <code>displayName</code> has to match the 
	 * same from the <code>taglist-maven-plugin</code> configuration. 
	 * Who should receive a mail? One can use an id of a developer registered in the pom or an email address directly.
	 * <pre>
	 * 								&lt;tagClasses&gt;
	 * 									&lt;tagClass&gt;
	 * 										&lt;displayName&gt;Todo Work&lt;/displayName&gt;
	 * 										&lt;receivers&gt;
	 * 											&lt;receiver&gt;developerId&lt;/receiver&gt;
	 * 											&lt;receiver&gt;dude@xx.com&lt;/receiver&gt;
	 * 										&lt;/receivers&gt;
	 * 									&lt;/tagClass&gt;
	 * 								&lt;/tagClasses&gt; 
	 * </pre>
	 * @parameter 
	 * @required
	 */		
	List tagClasses = []
	
	void addTagClasse(TagClass tagClass){
		tagClasses.add(tagClass)
	}	
	
	/**
	 * do the report and send the mails
	 */
	protected void executePostmanReport(Locale locale) throws MavenReportException {
		def tagClass2Ancher = prepareAnchers()
		
		TaglistMailCollector taglistSender = new TaglistMailCollector(log: getLog(), tagClasses: tagClasses)
		def receiver2Mail = taglistSender.getMails(taglistReportXml, taglistReportHtml)
		
		if(!skipMails){
			receiver2Mail.each sendReport
		} else{
			log.info "postman skips sending mails!"
		}
		
		def report = new SinkReporter(bodyGenerator: new TaglistReportBodyGenerator(receiver2TestReport: receiver2Mail, 
				tagClass2Ancher: tagClass2Ancher,
				targetTaglistHtmlPage: taglistReportHtml?.getName()))
		report.doGenerateReport( getBundle( locale ), getSink(), nlsPrefix, getLog() )
	}
	
	/**
	 * Parses the <code>taglist.html</code> and prepares a map with the mapping 'tagClass.displayName:htmlAncher'.
	 */
	Map prepareAnchers(){
		def tagClass2htmlAncher = [:]
		if(taglistReportHtml && taglistReportHtml.exists()){
			// 1. parse the html
			def doc = new XmlParser( new org.cyberneko.html.parsers.SAXParser() ).parse(taglistReportHtml)
			// 2. get all <a>-tags starting with '#' (anchers) 
			doc.depthFirst().A.findAll{
				it['@href']?.startsWith("#")
			}.each{
				// 3. fill them in to the prepared map
				// each item is still a XML/XHTML Snipplet!
				tagClass2htmlAncher.put it.text(), it['@href']	
			}
			if(getLog().isDebugEnabled()){
				tagClass2htmlAncher.each{  key, value -> getLog().debug "$key === $value" }
			}
		}else{
			getLog().warn """
			taglist report could not be found ($taglistReportHtml).
			This could have multiple reasons:
			1. order of report plugin execution might not be correct.
			   'taglist-maven-plugin' must be defined before 'maven-postman-plugin' in the pom!
			2. The 'taglist-maven-plugin' does not generate any report information (html or xml) 
			   if it can't find any java code.			
			"""
		}
		return tagClass2htmlAncher
	}
	
}
