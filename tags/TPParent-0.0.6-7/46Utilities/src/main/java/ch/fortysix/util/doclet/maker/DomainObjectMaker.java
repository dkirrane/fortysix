package ch.fortysix.util.doclet.maker;

import java.util.HashMap;
import java.util.Map;

import ch.fortysix.util.doclet.maker.helper.ClassFileHelper;
import ch.fortysix.util.doclet.maker.helper.ContextInformation;
import ch.fortysix.util.doclet.maker.helper.TargetFileInfo;

import com.sun.javadoc.ClassDoc;

public class DomainObjectMaker extends AbstractVelocityMaker {

	/**
	 * Create the Java code maker with a given name.
	 */
	public DomainObjectMaker(String name) {
		super(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.imod.labor.doc.maker.AbstractVelocityMaker#
	 * createTemplateContextInformation(com.sun.javadoc.ClassDoc,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public Map<String, Object> createTemplateContextInformation(ClassDoc classdoc, String targetName,
			String templateFile) {
		
		ClassFileHelper classFileHelper = new ClassFileHelper();
		TargetFileInfo fileContext = new TargetFileInfo();
		fileContext.setTargetName(targetName);
		fileContext.setTargetPackage(ContextInformation.getExecutionFieldString(ContextInformation.SUBPACKAGE_KEY));

		Map<String, Object> context = new HashMap<String, Object>();

		context.put("helper", classFileHelper);
		context.put("filecontext", fileContext);
		context.put("classcontext", classdoc);
		context.put("template", templateFile);
		return context;
	}
}