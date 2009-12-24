package ch.fortysix.maven.plugin.sender.support;

import java.io.File;
import java.util.Set;

class SurefireReport {
	
	boolean skip
	
	String reportFilePattern = "TEST-.*.xml"
	
	Set receivers;	
}
