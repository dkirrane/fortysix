/**
 * 
 */
package ch.fortysix.maven.report.surefire;

import org.apache.maven.doxia.sink.Sink;
/**
 * Generates the html body for the postman surefire report
 * @author Domi
 *
 */
class SurefireReportBodyGenerator {
	
	boolean mailsSkiped = false
	
	//[receivers: receivers, from: from, subject: subject, text: mailContent.text(), html: mailContent.html()]
	def mailList
	
	void generateBody(Sink sink){
		if(mailsSkiped){
			sink.paragraph()
			sink.bold()
			sink.text "The mail sending has been skiped! (no mails send)"
			sink.bold_()
			sink.paragraph_()
		}
		sink.paragraph()
		sink.text "The reminder mail check the test results was send to this users."
		sink.table()
		sink.tableHeaderCell()
		sink.text "Receiver"
		sink.tableHeaderCell_()
		//		sink.tableHeaderCell()
		//		sink.text "Mail Content"
		//		sink.tableHeaderCell_()
		
		mailList.each { mail -> 
			sink.tableRow()
			sink.tableCell()
			sink.text mail.receiver
			sink.tableCell_()
			sink.tableCell()
			sink.text mail.text
			sink.tableCell_()
			sink.tableRow_()
		}
		
		sink.table_()
		
		sink.paragraph_()
	}
	
}
