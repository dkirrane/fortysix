package ch.fortysix.maven.plugin;

import org.junit.Test;
public class SendMailTest {
//	fixture.assertEmailArrived(from:'cruise@myhost.org',
//            subject:'Test build')

	@Test
	public void testname() throws Exception {
		int port = 1025
		def fixture = new EmailFixture(port)

		def ant = new AntBuilder()
		ant.mail(mailhost:'localhost', mailport:"$port",
		         subject:'Successful build'){
		    from(address:'cruise@mycompany.org')
		    cc(address:'partners@mycompany.org')
		    to(address:'devteam@mycompany.org')
		    message("Successful build for ${new Date()}")
		}

		fixture.assertEmailArrived('cruise@mycompany.org',
		                           'Successful build')
	}
}
