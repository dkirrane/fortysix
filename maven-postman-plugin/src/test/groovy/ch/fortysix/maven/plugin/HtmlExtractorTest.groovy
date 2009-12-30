/**
 * 
 */
package ch.fortysix.maven.plugin;

import static org.junit.Assert.*;

import junit.framework.TestCase;

import org.junit.Test;

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
		  <body>
				<div id="breadcrumbs">
					<b>
					crumb1
					</b>
				</div>
		</body>
		"""
	
	
	/**
	 * Test method for {@link ch.fortysix.maven.report.support.HtmlExtractor#extractHTMLTagById(java.util.Map)}.
	 */
	//	@Test
	//	public void testExtractHTMLTagById() {
	//		HtmlExtractor extractor = new HtmlExtractor() 		
	//		// get the html of the generated taglist report (taglist-plugin)
	//		def html = extractor.extractHTMLTagById(html: html, tagName: "div", tagId: "breadcrumbs")
	//		assert html, 'no tag returned'
	//	}
	
	
	public void testThis(){
		def doc = new XmlSlurper( new org.cyberneko.html.parsers.SAXParser() ).parseText(html)
		def divHtml = "" << new groovy.xml.StreamingMarkupBuilder().bind {xml ->
			println "------ $xml"
//			xml.mkp.yield divTag
			}
		
		println "++++ $doc"
		def htmlTag = doc.DIV.find {
			println "****-> $it"
			it['@id'] == "breadcrumbs"
		}
		println "RESULT: $htmlTag"
		assert htmlTag		
	}
	
	/**
	 * Test method for {@link ch.fortysix.maven.report.support.HtmlExtractor#extractHTMLTagById(java.util.Map)}.
	 */
	//	public void dummyExtractHTMLTagById_old() {
	//		//		def taglistReportHtml = new File("src/test/resources/taglist.html")
	//		HtmlExtractor extractor = new HtmlExtractor() 		
	//		// get the html of the generated taglist report (taglist-plugin)
	//		def html = extractor.extractHTMLTagById(htmlFile: taglistReportHtml, tagName: "div", tagId: "bodyColumn")
	//		assert html, 'no tag subtag returned'
	//	}
	
}
