package ch.fortysix.maven.plugin;

/**
 * @author Domi
 *
 */
class RuleResultSet {
	
	public Rule rule
	
	public Map results = new HashMap()
	
	def addResult(String cause, String info){
		results.put cause, info
	}
	
}
