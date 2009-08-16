package ch.fortysix.util.doclet.maker;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import ch.fortysix.util.doclet.maker.helper.ClassFileHelper;
import ch.fortysix.util.doclet.maker.helper.ContextInformation;
import ch.fortysix.util.doclet.maker.helper.TargetFileInfo;

import com.sun.javadoc.ClassDoc;

public class DomainObjectBuilderMaker extends AbstractVelocityMaker {

	private Set<ClassDoc> builderTypes = null;

	/**
	 * Create the Java code maker with a given name.
	 */
	public DomainObjectBuilderMaker(Set<ClassDoc> builderTypes) {
		super("DomainBuilderMaker");
		this.builderTypes = builderTypes;
	}

	@Override
	public Map<String, Object> createTemplateContextInformation(ClassDoc type, String targetName, String templateFile) {
		ClassFileHelper classFileHelper = new ClassFileHelper();
		TargetFileInfo fileContext = new TargetFileInfo();
		fileContext.setTargetName(targetName);
		fileContext.setTargetPackage(ContextInformation.getExecutionFieldString(ContextInformation.SUBPACKAGE_KEY));

		Map<String, Object> context = new HashMap<String, Object>();
		context.put("helper", classFileHelper);
		context.put("filecontext", fileContext);
		context.put("types", builderTypes);
		context.put("template", templateFile);

		return context;
	}

}