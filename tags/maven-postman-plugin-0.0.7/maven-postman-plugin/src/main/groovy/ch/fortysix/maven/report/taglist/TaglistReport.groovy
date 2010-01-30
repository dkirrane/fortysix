package ch.fortysix.maven.report.taglist
;

import java.io.File;



class TaglistReport {
	
	boolean skip
	
	File reportFile;
	
	List tagClasses = []
	
	void addTagClasse(TagClass tagClass){
		tagClasses.add(tagClass)
	}
	
}
