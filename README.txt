###########################
# 
# postman report plugin 
# 
# provides utilities to send emails based on different prerequisits/conditions

Maven Profiles:
===============
"integration-tests" - configures the integration tests, activation with: -Dshit

Deploy a new version (SNAPSHOT):
=====================
increase the heap first:
$> MAVEN_OPTS="-Xmx1024m -Xms1024m"
$> export MAVEN_OPTS
$> mvn clean deploy site-deploy

Make a release:
===============
First Install GPG (http://www.gnupg.org/) see http://www.sonatype.com/people/2010/01/how-to-generate-pgp-signatures-with-maven/
$> mvn release:prepare 
$> mvn release:perform 
$> mvn site-deploy



Run integration tests:
======================
$> mvn clean integration-test -Dshit