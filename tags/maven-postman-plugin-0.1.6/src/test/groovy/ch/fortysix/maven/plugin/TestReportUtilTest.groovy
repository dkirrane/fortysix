/**
 * 
 */
package ch.fortysix.maven.plugin;

import static org.junit.Assert.*;

import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.monitor.logging.DefaultLog;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.codehaus.groovy.tools.xml.DomToGroovy;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;

import ch.fortysix.maven.plugin.postaman.surfire.TestReportUtil;
import ch.fortysix.maven.report.support.HtmlExtractor;

/**
 * @author Domi
 *
 */
class TestReportUtilTest extends TestCase{

	def testReportsDirectory = new File("src/test/resources")
	def reportFilePattern = "TEST-NoSkipped.xml"

	public void testNoSkipped(){

		Log log = new SystemStreamLog();

		TestReportUtil util = new TestReportUtil(log: log)
		def suiteReports = util.getTestSuiteReport( testReportsDirectory, reportFilePattern)
		Assert.assertNotNull "suitereports must not be null", suiteReports
		Assert.assertTrue "there must be one report", suiteReports.size() == 1
		Assert.assertTrue "there must be 2 errors", suiteReports[0].errors == 2
		Assert.assertTrue "there must be 0 skipped", suiteReports[0].skipped == 0
		Assert.assertTrue "there must be 4 failures", suiteReports[0].failures == 4
		Assert.assertTrue "there must be 1 tests", suiteReports[0].tests == 1
	}
}
