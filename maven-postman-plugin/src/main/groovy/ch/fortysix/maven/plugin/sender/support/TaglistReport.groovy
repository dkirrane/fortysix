package ch.fortysix.maven.plugin.sender.support;

import java.io.File;

class TaglistReport {
	
	boolean skip
	
	File reportFile;
	
	List tagClasses = []
	
	void addTagClasse(TagClass tagClass){
		tagClasses.add(tagClass)
	}
	
}
