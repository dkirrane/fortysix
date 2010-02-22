/**
 * 
 */
package ch.fortysix.maven.report.taglist

import org.apache.maven.doxia.sink.Sink
/**
 * Generates the html body for the postman taglist report
 * @author Domi
 */
class TaglistReportBodyGenerator {
	
	def targetTaglistHtmlPage
	
	//	[receivers: receivers, from: from, subject: subject, text: mailContent.text(), html: mailContent.html()]
	def receiver2TestReport = [:]
	
	def tagClass2Anchor = [:]
	
	void generateBody(Sink sink){
		
		sink.table()
		sink.tableHeaderCell()
		sink.text "Receiver"
		sink.tableHeaderCell_()
		sink.tableHeaderCell()
		sink.text "Mail Content"
		sink.tableHeaderCell_()
		
		receiver2TestReport.each { mail ->
			//			report.addToSink(sink)
			sink.tableRow()
			sink.tableCell()
			sink.text mail.receivers
			sink.tableCell_()
			sink.tableCell()
			sink.text "Check out these taglist reports:"		
			sink.lineBreak()
			mail.tagsFromReportFile.each{ tag ->
				// create a link (e.g. 'taglist.html#tag_class_1')
				sink.link targetTaglistHtmlPage+tagClass2Anchor[tag.@name?.toString()]
				sink.text tag.@name?.toString()
				sink.link_()
				sink.lineBreak()
			}
			
			//				report.addToSink(sink)
			
			sink.tableCell_()
			sink.tableRow_()
		}
		
		sink.table_()
		//	
	}
	
}
