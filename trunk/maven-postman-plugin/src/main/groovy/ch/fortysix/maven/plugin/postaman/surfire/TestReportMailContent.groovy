/**
 * 
 */
package ch.fortysix.maven.plugin.postaman.surfire



;

/**
 * @author Domi
 *
 */
class TestReportMailContent {
	
	def suiteReports = []
	
	def htmlFragment
	
	String html(){
		htmlFragment
	}
	
	String text(){
		def body = new StringBuilder()
		suiteReports.each{ report -> 
			body << "\n"
			body << report.name << " (total: " << report.tests <<  ") errors: " << report.errors <<  ", failures: " <<  report.failures << ", skipped: " <<  report.skipped
		}
		body.toString()
	}
	
}
