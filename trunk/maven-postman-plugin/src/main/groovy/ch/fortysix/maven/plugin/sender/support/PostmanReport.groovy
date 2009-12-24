package ch.fortysix.maven.plugin.sender.support;

import java.io.File;

class PostmanReport {
	
	boolean skip
	
	File reportFile;
	
	List tagClasses = []
	
	void addTagClasse(TagClass tagClass){
		tagClasses.add(tagClass)
	}
	
}
