/**
 * 
 */
package ch.fortysix.maven.report.criterion
;

/**
 * This class has to be in the same package as the plugin using it.
 * @author Domi
 *
 */
class Rule {
	String regex
	Set receivers
	
	String definition() {
		return "["+regex+"]"
	}
	
	String toString(){
		return "[Rule: "+regex+", receivers="+receivers+"]"
	}
}
