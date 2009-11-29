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
	Set recivers
	
	String definition() {
		return "["+regex+"]"
	}
	
	String toString(){
		return "[Rule: "+regex+", positiveTest="+positiveTest+", recivers="+recivers+"]"
	}
}
