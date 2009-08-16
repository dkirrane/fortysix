package ch.fortysix.util.doclet.maker;

import java.io.File;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.runtime.resource.loader.FileResourceLoader;

import ch.fortysix.util.doclet.maker.helper.ClassFileHelper;
import ch.fortysix.util.doclet.maker.helper.ContextInformation;
import ch.fortysix.util.doclet.maker.helper.TemplateOperator;

import com.sun.javadoc.ClassDoc;

public abstract class AbstractVelocityMaker implements Maker {

	protected Log logger = null;

	protected ClassFileHelper classFileHelper = new ClassFileHelper();

	/**
	 * Create the Java code maker with a given name.
	 */
	public AbstractVelocityMaker(String name) {
		logger = LogFactory.getLog(getClass());
		try {
			this.initVelocity();
		} catch (Exception e) {
			throw new RuntimeException("not able to init velocity", e);
		}
	}

	/**
	 * Returns the objects to be added to the velocitycontext of the one
	 * template execution.
	 * 
	 * @param type
	 *            the class to create a new file with
	 * @param targetName
	 *            the targetname of the new class
	 * @param templateFile
	 *            the velocity template
	 * @return a map with the information to be added to the template context.
	 */
	public abstract Map<String, Object> createTemplateContextInformation(ClassDoc type, String targetName,
			String templateFile);

	/**
	 * The entry point for code generation.
	 */
	/**
	 * The entry point for code generation.
	 */
	public void make(ClassDoc type, String namePostix, String velocityTemplate) {

		String targetName = namePostix;
		if (type != null) {
			targetName = type.typeName() + namePostix;
		}

		String templateFile = ContextInformation.getExecutionFieldString(ContextInformation.TEMPLATE_KEY);

		try {

			Map<String, Object> context = createTemplateContextInformation(type, targetName, templateFile);

			String targetPackage = ContextInformation.getExecutionFieldString(ContextInformation.SUBPACKAGE_KEY);
			File packageDir = this.createTargetPackage(targetPackage);

			File newFile = new File(packageDir, targetName + ".java");
			logger.info("build: " + newFile.getAbsolutePath());

			TemplateOperator operator = new TemplateOperator();
			operator.apply(newFile, velocityTemplate, context);

		} catch (Exception e) {
			logger.error("Could not create file ", e);
		}

	}

	/**
	 * Creates the physical directory if it does not exist.
	 * 
	 * @param targetPackage
	 *            the package directory to be created (e.g.
	 *            com.domain.subpackage).
	 * @return the created directory.
	 */
	private File createTargetPackage(String targetPackage) {
		String dirStr = targetPackage.replace('.', '/');
		File packageDir = null;
		try {
			packageDir = new File(dirStr);
			if (!packageDir.exists()) {
				packageDir.mkdirs();
			}
		} catch (Exception e) {
			throw new RuntimeException("could not create target package directory: " + packageDir.getAbsolutePath());
		}
		return packageDir;
	}

	/**
	 * Initializes the velocity engine
	 * 
	 * @throws Exception
	 *             if an exception occurred
	 */
	private void initVelocity() throws Exception {

		Velocity.setProperty(VelocityEngine.RESOURCE_LOADER, "file, classpath");
		Velocity.setProperty("classpath." + VelocityEngine.RESOURCE_LOADER + ".class", ClasspathResourceLoader.class
				.getName());

		Velocity.setProperty("file." + VelocityEngine.RESOURCE_LOADER + ".class", FileResourceLoader.class.getName());
		Velocity.setProperty(VelocityEngine.FILE_RESOURCE_LOADER_PATH, ContextInformation
				.getExecutionField(ContextInformation.TEMPLATE_DIRECTORY_KEY));
		Velocity.setProperty("file.resource.loader.cache", "false");
		Velocity.setProperty("file.resource.loader.modificationCheckInterval", "0");

		Velocity.init();
	}
}