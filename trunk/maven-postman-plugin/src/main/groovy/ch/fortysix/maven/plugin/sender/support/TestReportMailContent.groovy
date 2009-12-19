/**
 * 
 */
package ch.fortysix.maven.plugin.sender.support;

/**
 * @author Domi
 *
 */
class TestReportMailContent {
	
	def suiteReports = []
	
	String asMailBody(){
		def body = new StringBuilder()
		suiteReports.each{ report -> 
			body << "\n"
			body << report.name << "\n"
		}
		return body.toString()
	}
}
