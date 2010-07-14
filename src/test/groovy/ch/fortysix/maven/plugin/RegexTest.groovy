package ch.fortysix.maven.plugin;


import groovy.util.GroovyTestCase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.junit.Assert;

/**
 * @author Domi
 *
 */
class RegexTest extends GroovyTestCase {
	
	
	def toBeFound = "hallo domi"
	def someText = '''
some text that should not be found
hallo eva
bisch au do?
		'''
	
	def regex = ".*(domi).*"
	
	@Test
	public void testRegex() throws Exception {
		
		Pattern p = Pattern.compile("o(b.*r)f", Pattern.DOTALL)
		Matcher m = p.matcher("foobarfoo")
		
		Assert.assertTrue( m[0] == ["obarf", "bar"] )
		assert m[0] == ["obarf", "bar"]
		assert m[0][1] == "bar"
		
		// concatenate text
		def text = toBeFound << someText 
		
		// two ways of creating a regex matcher...
		//Pattern compiledRegex = Pattern.compile(regex, Pattern.DOTALL)
		//Matcher regexMatcher = compiledRegex.matcher(someText)
		def matcher = text =~ regex
		assert matcher[0][1] == "domi"
		assert toBeFound == matcher[0][0]
		
		println "******"
		matcher.collect{ println "**-->"+it }
		println "******"
		
		matcher = "cheese please" =~ /([^e]+)e+/
		matcher.each { println it }
		matcher.reset()
		assert matcher.collect { it }  ==
		[["chee", "ch"], ["se", "s"], [" ple", " pl"], ["ase", "as"]]
		
	}
}
