package ch.fortysix.util.doclet.maker.helper;

import java.io.File;
import java.io.FileWriter;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

public class TemplateOperator {

	protected Log logger = null;

	public TemplateOperator() {
		logger = LogFactory.getLog(getClass());
	}

	public void apply(File targetFile, String templateFile,
			Map<String, Object> contextInfo) {

		try {

			VelocityContext context = new VelocityContext();

			for (String key : contextInfo.keySet()) {
				context.put(key, contextInfo.get(key));
				// context.put("helper", classFileHelper);
				// context.put("filecontext", fileContext);
				// context.put("classcontext", type);
			}

			org.apache.velocity.Template template = null;

			try {
				template = Velocity.getTemplate(templateFile);
			} catch (Exception e) {
				logger.error("Could not get template ", e);
				throw new RuntimeException(e);
			}

			// String targetPackage =
			// ContextInformation.getExecutionFieldString(ContextInformation.SUBPACKAGE_KEY);
			// String dirStr = targetPackage.replace('.', '/');
			// File packageDir = null;
			// try {
			// packageDir = new File(dirStr);
			// packageDir.mkdirs();
			// } catch (Exception e) {
			// throw new
			// RuntimeException("could not create target package directory: " +
			// packageDir.getAbsolutePath());
			// }

			// File newFile = new File(packageDir, targetName + ".java");

			// logger.info("build: " + newFile.getAbsolutePath());
			FileWriter writer = new FileWriter(targetFile);

			template.merge(context, writer);
			writer.flush();
			writer.close();

		} catch (Exception e) {
			logger.error("Could not create file ", e);
		}
	}

}
