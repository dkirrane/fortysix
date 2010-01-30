/**
 * 
 */
package ch.fortysix.maven.report.criterion
;

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
