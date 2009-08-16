package ch.fortysix.util.doclet.maker;

import com.sun.javadoc.ClassDoc;


public interface Maker {
	public void make(ClassDoc classDoc, String namePostfix, String velocityTemplate);
}
