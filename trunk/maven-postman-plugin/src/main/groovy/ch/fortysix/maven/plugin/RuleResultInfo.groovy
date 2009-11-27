package ch.fortysix.maven.plugin;

/**
 * @author Domi
 *
 */
class RuleResultInfo {
	
	public Rule rule
	
	private Map results = new HashMap()
	
	def addMessage(String cause, String info){
		results.put cause, info
	}
	
}
