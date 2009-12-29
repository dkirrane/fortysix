###########################
# 
# postman report plugin 
# 
# provides utilities to send emails based on different prerequisits/conditions

Maven Profiles:
===============
"integration-tests" - configures the integration tests, activation with: -Dshit

Deploy a new version:
=====================
$> mvn clean deploy site-deploy


Run integration tests:
======================
$> mvn clean integration-test -Dshit