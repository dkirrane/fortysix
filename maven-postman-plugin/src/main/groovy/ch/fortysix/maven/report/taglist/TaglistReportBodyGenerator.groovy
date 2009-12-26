/**
 * 
 */
package ch.fortysix.maven.report.taglist
;

import org.apache.maven.doxia.sink.Sink;
/**
 * Generates the html body for the postman taglist report
 * @author Domi
 */
class TaglistReportBodyGenerator {
	
	def receiver2TestReport
	
	void generateBody(Sink sink){
		sink.paragraph()
		
		sink.table()
		sink.tableHeaderCell()
		sink.text "Receiver"
		sink.tableHeaderCell_()
		sink.tableHeaderCell()
		sink.text "Mail Content"
		sink.tableHeaderCell_()
		
		receiver2TestReport.each { receiver, report ->
			sink.tableRow()
			sink.tableCell()
			sink.text receiver
			sink.tableCell_()
			sink.tableCell()
			sink.text report.asMailBody()
			sink.tableCell_()
			sink.tableRow_()
		}
		
		sink.table_()
		
		sink.paragraph_()
	}
	
}
