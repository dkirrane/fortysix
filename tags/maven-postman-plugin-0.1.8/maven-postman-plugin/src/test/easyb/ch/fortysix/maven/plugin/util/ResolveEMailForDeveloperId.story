package ch.fortysix.maven.plugin.util

import groovy.xml.MarkupBuilder;

scenario "a developer id is used to resolve its email in the pom",{
	
	given "a developer is configured in the pom",{
		
		mailAddress = 'dummy@pom.xml'
		// create same structure as in pom.xml
		def developers = [[id:'1111', name:'Dominik',email:mailAddress]]
		project = [developers: developers]
		
	}
	
	when "the receiver in the plugin config is not an email but an developer id",{  
		userId = "1111" 
	}
	
	then "the email should be resolved by the receivers id",{
		
		// imitate a logger
		def logHelper = [warn:{println it}, info:{println it}]
		
		def resolver = new AddressResolver(mavenProject: project, log: logHelper)
		def address = resolver.resolveEMailAddress(userId)
		address.shouldBe mailAddress
	}
	
}