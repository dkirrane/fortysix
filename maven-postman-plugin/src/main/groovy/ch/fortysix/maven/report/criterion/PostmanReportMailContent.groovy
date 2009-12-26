/**
 * 
 */
package ch.fortysix.maven.report.criterion
;

/**
 * @author Domi
 *
 */
class PostmanReportMailContent {
	
	def rules = []
	
	String asMailBody(){
		def body = new StringBuilder()
		rules.each{	tag -> 
			body << "\n"
			body << "Rule: '" << tag.regex << "'"
			body << "\n"
			tag.result.eachWithIndex{ result, i ->
				body << (i+1) << ". file: " << result.file << "\n"
				result.match.each{
					body << "\tMatch: " << it << "\n" 
				}
			}
		}
		
		return body.toString()
	}
}
