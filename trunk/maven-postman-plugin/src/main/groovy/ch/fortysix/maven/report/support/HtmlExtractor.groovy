/**
 * 
 */
package ch.fortysix.maven.report.support;

/**
 * @author Domi
 *
 */
class HtmlExtractor {
	
	String extractHTMLTagById(Map args){
		assert args.htmlFile
		assert args.tagName
		assert args.tagId
		
		def taglistReportHtml = args.htmlFile
		def tagName = args.tagName.toUpperCase()
		def tagId = args.tagId
		
		def htmlTag
		if(taglistReportHtml && taglistReportHtml.isFile()){
			// 1. parse the html
			def doc = new XmlParser( new org.cyberneko.html.parsers.SAXParser() ).parse(taglistReportHtml)
			// 2. get the first tag with the given id 
			htmlTag = doc.depthFirst()."$tagName".find{
//				println it
				it['@id'] == tagId 
			}
		}
//		println htmlTag.text()
//		return htmlTag
		return args.htmlFile.text
	}
	
}
