println "*******************************"
println "******** verify ***************"
println "*******************************"

// available variables:
//	- basedir 				java.io.File 	The absolute path to the base directory of the test project. 	
//	- localRepositoryPath 	java.io.File 	The absolute path to the local repository used for the Maven invocation on the test project. 	
//	- context 				java.util.Map 	The storage of key-value pairs used to pass data from the pre-build hook script to the post-build hook script.

def smtpServer = context."test.smtp.server"
assert smtpServer
def project = context."project"
assert project
def fromAddress = project.properties?.testFromAddress.text()
assert fromAddress  

// validate if all mails have been send
// this emails are defiend in the pom of the test project
smtpServer.assertEmailArrived(fromAddress, "anyone@sink.com", 1)
smtpServer.assertEmailArrived(fromAddress, "domi@pom.xml", 1)
// two expected, because one is defined directly via email and the other via devleoperId from the pom
smtpServer.assertEmailArrived(fromAddress, "dude@pom.xml", 2) 


// validate the HTML report
def html = new File(basedir, 'target/site/postman-taglist-report.html')
assert html.exists()
def content = html.text

assert content.contains ("Postman Taglist Mail Report")

return true
