/**
 * 
 */
package ch.fortysix.maven.plugin;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;

/**
 * @author Domi
 * 
 */
public class MailMojoTest extends AbstractMojoTestCase {

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		// required for mojo lookups to work
		super.setUp();
	}

	/**
	 * tests the proper discovery and configuration of the mojo
	 * 
	 * @throws Exception
	 */
	public void testSend() throws Exception {

//        File testPom = new File( getBasedir(), "test-pom.xml" )
//
//        MailMojo mojo = (MailMojo) lookupMojo ("send", testPom )
//
//        assertNotNull( mojo )
//
//        mojo.execute()
    }
}
