/**
 * 
 */
package ch.fortysix.maven.plugin.postaman.surfire

;

/**
 * @author Domi
 *
 */
class TestSuiteReport {
	Integer errors
	Integer skipped
	Integer tests
	Integer failures
	String name
	
	public String toString(){
		"$name - errors: $errors, skipped: $skipped, failures: $failures, tests: $tests"
	}
}
