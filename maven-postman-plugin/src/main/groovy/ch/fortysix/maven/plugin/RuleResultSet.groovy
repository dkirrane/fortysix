package ch.fortysix.maven.plugin;

/**
 * @author Domi
 *
 */
class RuleResultSet {
	
	public Rule rule
	
	def results = []
	
	def addResult(Result result){
		results.add result
	}
	
}
