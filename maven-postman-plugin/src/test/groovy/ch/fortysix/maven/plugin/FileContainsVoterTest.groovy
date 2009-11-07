package ch.fortysix.maven.plugin;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Domi
 *
 */
public class FileContainsVoterTest {

	static File testFile = new File("dummy.txt");
	static String testText = "some super cool text"
	static String regex = ".*(super).*" 
	
	/**
	 * @throws java.lang.Exception
	 */@BeforeClass
	 public static void setUpBeforeClass() throws Exception {
		 if(testFile.exists()){
			 testFile.delete()
		 }
		 testFile << testText
	 }
	
	
	/**
	 * Test method for {@link ch.fortysix.maven.plugin.FileContainsVoter#vote()}.
	 */
	@Test
	public void testVotePositive(){
		FileContainsVoter voter = new FileContainsVoter(
					fileToTest: testFile,
					text: testText
				)
		org.junit.Assert.assertTrue("text not found in file", voter.vote())
	}

	/**
	 * Test method for {@link ch.fortysix.maven.plugin.FileContainsVoter#vote()}.
	 */
	@Test
	public void testVoteNegative(){
		FileContainsVoter voter = new FileContainsVoter(
					fileToTest: testFile,
					text: testText,
					voteTrueOnPositiveTest: false
				)
		org.junit.Assert.assertFalse("its a negative test, even the text was found, it should return 'false'", voter.vote())
	}	

	/**
	 * Test method for {@link ch.fortysix.maven.plugin.FileContainsVoter#vote()}.
	 */
	@Test
	public void testVoteRegexPositive(){
		FileContainsVoter voter = new FileContainsVoter(
					fileToTest: testFile,
					regex: regex
				)
		org.junit.Assert.assertTrue("text not found in file", voter.vote())
	}	

	/**
	 * Test method for {@link ch.fortysix.maven.plugin.FileContainsVoter#vote()}.
	 */
	@Test
	public void testVoteRegexNegative(){
		FileContainsVoter voter = new FileContainsVoter(
					fileToTest: testFile,
					regex: regex,
					voteTrueOnPositiveTest: false
				)
		org.junit.Assert.assertFalse("its a negative test, even the text was found, it should return 'false'", voter.vote())
	}		
	
}