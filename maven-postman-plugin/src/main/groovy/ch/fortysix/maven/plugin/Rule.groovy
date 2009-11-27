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
	String[] recivers
	
	String toString(){
		return "[Rule: "+regex+", positiveTest="+positiveTest+", recivers="+recivers+"]"
	}
}
