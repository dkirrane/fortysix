/**
 * 
 */
package ch.fortysix.maven.report.criterion

;

import org.apache.maven.doxia.sink.Sink;
/**
 * Generates the html body for the postman criterion report
 * @author Domi
 */
class CriterionReportBodyGeneratorerator {
	
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
			report.addToSink(sink)
			sink.tableCell_()
			sink.tableRow_()
		}
		
		sink.table_()
		
		sink.paragraph_()
	}
	
}
