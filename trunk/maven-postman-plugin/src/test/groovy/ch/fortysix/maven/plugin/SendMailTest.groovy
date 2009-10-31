package ch.fortysix.maven.plugin;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class SendMailTest {
//	fixture.assertEmailArrived(from:'cruise@myhost.org',
//            subject:'Test build')

	@Test
	public void testname() throws Exception {
		int port = 1025
		def fixture = new EmailFixture(port)

		Logger logger = LoggerFactory.getLogger(SendMailTest.class)
		
		MailSender sender = new MailSender(
					mailhost:'localhost', 
					mailport:port,
					subject:'Successful build',
				    from:'dummy@dummy.org',
				    cc:'dummy1@mycompany.org',
				    to:'dummy2@mycompany.org',
				    message:"Successful build for ${new Date()}"
				)

		logger.info("befor seding")
		sender.sendMail()
		logger.info("after seding")

		fixture.assertEmailArrived('dummy@dummy.org',
		                           'Successful build')
	}
}
