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
	
	/**
	 * Test method for {@link ch.fortysix.maven.report.support.HtmlExtractor#extractHTMLTagById(java.util.Map)}.
	 */
	@Test
	public void testExtractHTMLTagById() {
		def taglistReportHtml = new File("src/test/resources/taglist.html")
		HtmlExtractor extractor = new HtmlExtractor() 		
		// get the html of the generated taglist report (taglist-plugin)
		def html = extractor.extractHTMLTagById(htmlFile: taglistReportHtml, tagName: "div", tagId: "bodyColumn")
		assert html, 'no tag subtag returned'
	}
	
}
