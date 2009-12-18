/**
 * 
 */
package ch.fortysix.maven.plugin;

/**
 * @author Domi
 *
 */
class Rule {
	String regex
	boolean positiveTest
	Set receivers
	
	String definition() {
		return "["+regex+"]"
	}
	
	String toString(){
		return "[Rule: "+regex+", positiveTest="+positiveTest+", receivers="+receivers+"]"
	}
}
