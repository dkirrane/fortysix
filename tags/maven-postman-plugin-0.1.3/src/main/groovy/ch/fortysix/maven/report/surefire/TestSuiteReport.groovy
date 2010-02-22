/**
 * 
 */
package ch.fortysix.maven.report.surefire
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
