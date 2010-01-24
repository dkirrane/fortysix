package ch.fortysix.maven.report.criterion
;

import java.io.File;

import ch.fortysix.maven.report.taglist.TagClass;


class PostmanReport {
	
	boolean skip
	
	File reportFile;
	
	List tagClasses = []
	
	void addTagClasse(TagClass tagClass){
		tagClasses.add(tagClass)
	}
	
}
