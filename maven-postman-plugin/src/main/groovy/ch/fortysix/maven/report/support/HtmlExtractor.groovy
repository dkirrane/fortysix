/**
 * 
 */
package ch.fortysix.maven.report.support;

/**
 * @author Domi
 *
 */
class HtmlExtractor {
	
	String extractHTMLTagById_old(Map args){
		assert args.htmlFile
		assert args.tagName
		assert args.tagId
		
		def taglistReportHtml = args.htmlFile
		def tagName = args.tagName.toUpperCase()
		def tagId = args.tagId
		
		def htmlTag
		//		if(taglistReportHtml && taglistReportHtml.isFile()){
		// 1. parse the html
		//			def doc = new XmlParser( new org.cyberneko.html.parsers.SAXParser() ).parse(taglistReportHtml)
		//			def divHtml = "" << new groovy.xml.StreamingMarkupBuilder().bind {xml -> xml.mkp.yield divTag}
		
		//			def doc = new XmlSlurper( new org.cyberneko.html.parsers.SAXParser() ).parse(taglistReportHtml)
		//			println "-->"+doc.getClass()
		//			println "-->"+doc.getBody().toString()
		
		//			htmlTag = doc.find {
		//				println "*** $it"
		//				it['@id'] == tagId  
		//			}
		
		
		// 2. get the first tag with the given id 
		//			htmlTag = doc.depthFirst()."$tagName".find{
		//			htmlTag = doc.find{
		//				println "OK? $it"
		//				it['@id'] == tagId 
		//			}
		//	}
		//		println "TAG: "+htmlTag?.text()
		return htmlTag
		//		return args.htmlFile.text
	}
	
	
	String extractHTMLTagById(Map args){
		assert args.html
		assert args.tagName
		assert args.tagId
		
		def html = args.html
		def tagName = args.tagName.toUpperCase()
		def tagId = args.tagId
		
		def htmlTag
		// 1. parse the html
		//			def doc = new XmlParser( new org.cyberneko.html.parsers.SAXParser() ).parse(taglistReportHtml)
		//			def divHtml = "" << new groovy.xml.StreamingMarkupBuilder().bind {xml -> xml.mkp.yield divTag}
		
		//		def doc = new XmlSlurper( new org.cyberneko.html.parsers.SAXParser() ).parseText(html)
		//		println "-->"+doc.getClass()
		//		println "-->"+doc.getBody().toString()
		//		
		//		htmlTag = doc.find {
		//			println "*** $it"
		//			it['@id'] == tagId  
		//		}
		
		
		// 2. get the first tag with the given id 
		//			htmlTag = doc.depthFirst()."$tagName".find{
		//			htmlTag = doc.find{
		//				println "OK? $it"
		//				it['@id'] == tagId 
		//			}
		//		println "TAG: "+htmlTag?.text()
		//		return htmlTag
		return args.html
	}	
	
}
