/**
 * 
 */
package ch.fortysix.maven.plugin;

import static org.junit.Assert.*;

import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.codehaus.groovy.tools.xml.DomToGroovy;
import org.junit.Test;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;

import ch.fortysix.maven.report.support.HtmlExtractor;

/**
 * @author Domi
 *
 */
class HtmlExtractorTest extends TestCase{
	//	<div id="banner">
	//    <span id="bannerLeft">
	//		Taglist Report Tester
	//	</span>
	//    <div class="clear">
	//		<hr/>
	//	</div>
	//</div>
	
	def html = """
		  <body xmlns="http://www.w3.org/1999/xhtml">
				<div id="bodyColumn">
					<b>
					crumb1
					</b>
				</div>
		</body>
		"""
	
	
	public void testSimpleHtmlFragment(){
		
		HtmlExtractor extractor = new HtmlExtractor() 		
		// get the html of the generated taglist report (taglist-plugin)
		def html = extractor.extractHTMLTagById(html: html, tagName: "div", tagId: "bodyColumn")
		assert html, 'no tag returned'
		assert !html.contains(":DIV"), 'DIV-tag has a namespace'
		assert !html.contains(":div"), 'div-tag has a namespace'
		
	}
	
	/**
	 * Test method for {@link ch.fortysix.maven.report.support.HtmlExtractor#extractHTMLTagById(java.util.Map)}.
	 */
	public void testExtractHTMLTagById() {
		def taglistReportHtml = new File("src/test/resources/taglist.html")
		HtmlExtractor extractor = new HtmlExtractor() 		
		// get the html of the generated taglist report (taglist-plugin)
		def html = extractor.extractHTMLTagById(html: taglistReportHtml.text, tagName: "div", tagId: "bodyColumn")
		assert html, 'no tag returned'
		assert !html.contains(":DIV"), 'DIV-tag has a namespace'
		assert !html.contains(":div"), 'div-tag has a namespace'
	}
	
	
	public void testNewStyle() throws Exception {
		def taglistReportHtml = new File("src/test/resources/taglist.html")
		def fac = DocumentBuilderFactory.newInstance()
		fac.setNamespaceAware(false)
		fac.setExpandEntityReferences false
		fac.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
		fac.setValidating(false);
		fac.setFeature("http://xml.org/sax/features/namespaces", false);
		fac.setFeature("http://xml.org/sax/features/validation", false);
		fac.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
		fac.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		
		def builder     = fac.newDocumentBuilder()
		
		def inputStream = new FileInputStream(taglistReportHtml) // ByteArrayInputStream(XmlExamples.CAR_RECORDS.bytes)
		def document    = builder.parse(inputStream)
		def output      = new StringWriter()
		def converter   = new DomToGroovy(new PrintWriter(output))
		
		//		converter.print(document)
		//		println output.toString()
		
	}
	
}
