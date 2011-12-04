
import org.subethamail.wiser.Wiser

println "*******************************"
println "******** setup ***************"
println "*******************************"

//available variables:
//- basedir 				java.io.File 	The absolute path to the base directory of the test project. 	
//- localRepositoryPath 	java.io.File 	The absolute path to the local repository used for the Maven invocation on the test project. 	
//- context 				java.util.Map 	The storage of key-value pairs used to pass data from the pre-build hook script to the post-build hook script.

// load the POM
def pom = new File(basedir, "pom.xml")
def project = new XmlSlurper().parseText(pom.text)
println "loaded pom from: "+pom.getAbsolutePath()
// store a reference to the project (pom) in the context for reuse in the 'verify.groovy'
context.put ("project", project)

// get the defined dummy SMTP port
def smtpPort = project?.properties?.testSmtpServerPort?.text()?.toInteger()
assert smtpPort

// start a dummy SMTP server and store it in the context for use in the 'verify.groovy'
// the test cases run in this project will send emails to this instance...
def smtpServer = new EmailFixture(smtpPort)
context.put("test.smtp.server",smtpServer)


/**
 * A simple SMTP receiver class to validate if mails have been send.
 */
public class EmailFixture {
	
	private wiser = new Wiser()
	EmailFixture(int port) {
		println "listening for incoming mails at: $port"
		wiser.port = port
		wiser.start()
	}
	
	/**
	 * @param from expected sender email
	 * @param to expected receiver email
	 */
	def assertEmailArrived(String from, String to) {
		assertEmailArrived(from, to, null, null) 
	}
		
	/**
	 * @param from expected sender email
	 * @param to expected receiver email
	 * @param mailCount how many mails are expected of this combination (from/to/subject)
	 */
	def assertEmailArrived(String from, String to, Integer mailCount) {
		assertEmailArrived(from, to, null, mailCount) 
	}
		
	/**
	 * This methode can be used to test if a message arrived or not.
	 * Might be called in the 'verify.groovy' 
	 * @param from expected sender email
	 * @param to expected receiver email
	 * @param subject expected subject
	 * @param mailCount how many mails are expected of this combination (from/to/subject)
	 */
	def assertEmailArrived(String from, String to, String subject, Integer mailCount) {
		wiser.stop()
		assert wiser.messages.size() != 0, 'No messages arrived!'
		wiser.messages.each{println "--> from $it.envelopeSender to $it.envelopeReceiver"}
		
		def receiversMessages  = wiser.messages.findAll{((it.envelopeSender == from) && (it.envelopeReceiver == to))}
		if(subject){
			receiversMessages  = receiversMessages.findAll{
				it.mimeMessage?.subject == subject
			}
		}
		
		if(!mailCount){
			assert receiversMessages.size() > 0, "no message for this match [from: $from, to: $to] was received!"
		}else{
			assert receiversMessages.size() == mailCount, "expected [$mailCount] but got [$receiversMessages.size] message for this match [from: $from, to: $to]!"
		}
		
		
		receiversMessages.each{ 
			println "Got mail: $it"
			it.mimeMessage?.writeTo(System.out)			
		}
	}
	
}