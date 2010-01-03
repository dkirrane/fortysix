/**
 * 
 */
package ch.fortysix.maven.report.support;

/**
 * @author Domi
 *
 */
class HtmlExtractor {
	
	def private final static PARSER = new org.cyberneko.html.parsers.SAXParser()
	static{
		// we want all namespace definitions from the original html to disappear 
		PARSER.setFeature("http://xml.org/sax/features/namespaces", false);
		// all the elements should end up in lower case
		PARSER.setProperty("http://cyberneko.org/html/properties/names/elems", "lower");
	}
	
	/**
	 * Searches for the first html tag given by 'tagName' and the 'tagId' within the passed 'html' content.
	 */
	synchronized String extractHTMLTagById(Map args){
		assert args.html
		assert args.tagName
		assert args.tagId
		
		def html = args.html
		def tagName = args.tagName.toLowerCase()
		def tagId = args.tagId
		
		// 1. parse the html
		def doc = new XmlSlurper(PARSER).parseText(html)
		
		// 2. search for the requested TAG
		//    get the first tag with the given id (all tags are lower case, see parser config)
		def divTag = doc.'body'."$tagName".find { 
			if(it.@id){
				it.@id == tagId
			}
		}
		
		// 3. get the result as a String
		def htmlTag = "" << new groovy.xml.StreamingMarkupBuilder().bind {xml ->
			xml.mkp.yield divTag
		}
		
		return htmlTag
	}	
	
}
