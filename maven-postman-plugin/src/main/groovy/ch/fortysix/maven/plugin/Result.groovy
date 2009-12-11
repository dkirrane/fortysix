/**
 * 
 */
package ch.fortysix.maven.plugin;

/**
 * Represents a result/match found in one file
 * @author Domi
 *
 */
class Result {
	String file;
	def machedGroupLists = []
	void addMatchGroups(List matchedGroups){
		machedGroupLists.add matchedGroups
	}
}
