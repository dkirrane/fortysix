###########################
# 
# postman report plugin 
# 
# provides utilities to send emails based on different prerequisits/conditions

General
=======
increasing the heap might be needed:
$> MAVEN_OPTS="-Xmx1024m -Xms1024m"
$> export MAVEN_OPTS

Maven Profiles:
===============
"integration-tests" - configures the integration tests, activation with: -Dit

Deploy a new version (SNAPSHOT):
=====================
$> mvn clean deploy site-deploy

Make a release:
===============
First Install GPG (http://www.gnupg.org/) see http://www.sonatype.com/people/2010/01/how-to-generate-pgp-signatures-with-maven/
Add this to the settings.xml
 		<profile>
			<id>domi-default</id>
			<properties>
			       <gpg.keyname>XXX</gpg.keyname>
			       <gpg.passphrase>XXXXXXXX</gpg.passphrase>
			</properties>
$> mvn release:prepare 
$> mvn release:perform
a second way to get around a bug (http://jira.codehaus.org/browse/MGPG-9): $> mvn -Dgpg.passphrase="XXXX" -Darguments="-Dgpg.passphrase=XXXX" release:perform

Run integration tests:
======================
$> mvn clean integration-test -Dit




