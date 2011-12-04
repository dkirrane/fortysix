/**
 * 
 */
package ch.fortysix.maven.plugin.util;

/**
 * @author Domi
 *
 */
class AddressResolver {
	
	def log
	def mavenProject
	
	public String resolveEMailAddress(String receiver){
		
		def mailAddress = receiver
		
		if(!receiver.contains("@")){
			def email = mavenProject?.developers?.find { it.id == receiver }?.email
			if(email){
				getLog().info "replace [$receiver] by [$email]"
				mailAddress = email
			}else{
				getLog().warn "not able to find email address for [$receiver]"
				// exit the closure
				return
			}
		} 
		
		return mailAddress
	}
	
}
