package ch.fortysix.maven.plugin;

import org.junit.Assert;
import org.subethamail.wiser.Wiser

public class EmailFixture {
	
	private wiser = new Wiser()
	EmailFixture(int port) {
		wiser.port = port
		wiser.start()
	}
	
	def assertEmailArrived(String from, String to) {
		wiser.stop()
		assert wiser.messages.size() != 0, 'No messages arrived!'
		//        def message = wiser.messages[0].mimeMessage
		
		def messages = wiser.messages.findAll{it.from[0].toString() == from}
		def receiversMessages = messages.getAllRecipients().findAll{it.toString() == to}
		
		assert receiversMessages.size() > 0, "no message for this match [from: $from, to: $to] was received!"
		receiversMessages.each{ println "Got mail: $it" }
		
		//        Assert.assertEquals(from, message?.from[0].toString())
		//        Assert.assertNotNull(message.getAllRecipients().find{it.toString() == to})
		
		
	}
	
}
