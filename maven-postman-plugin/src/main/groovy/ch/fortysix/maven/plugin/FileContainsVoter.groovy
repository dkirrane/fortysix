package ch.fortysix.maven.plugin;

import java.io.File;
import java.io.IOException;

import org.slf4j.LoggerFactory;

import org.slf4j.Logger;

/**
 * @author Domi
 * 
 */
public class FileContainsVoter {

	Logger log = LoggerFactory.getLogger("file-contains-voter")
	
	boolean voteTrueOnPositiveTest = true
	File fileToTest 
	String text
	String regex

	public boolean vote() {
		if(!text && !regex){
			throw new IllegalArgumentException("'text' or 'regex' must be given");
		}
		if(text && regex){
			throw new IllegalArgumentException("only one of 'text' or 'regex' must be given");
		}
		if(fileToTest == null || !fileToTest.exists()){
			def fileStr = fileToTest ? fileToTest.getAbsolutePath() : "no <fileToTest> defined!"
			throw new IOException("'fileToTest' does not exist or can't be read: "+fileStr)
		}

		String logStr = regex ? "regex ["+regex+"]" : "text ["+text+"]"
		log.info("check file for "+logStr+": "+fileToTest.getAbsolutePath())
		
		boolean ok = regex ? fileToTest.text.matches(regex) : fileToTest.text.contains(text);
		return ( ok == voteTrueOnPositiveTest )
	}

}
