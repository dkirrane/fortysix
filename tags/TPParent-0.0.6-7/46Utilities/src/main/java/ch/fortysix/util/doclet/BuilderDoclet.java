package ch.fortysix.util.doclet;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.fortysix.util.doclet.maker.CompositeMaker;
import ch.fortysix.util.doclet.maker.DomainObjectBuilderMaker;
import ch.fortysix.util.doclet.maker.DomainObjectMaker;
import ch.fortysix.util.doclet.maker.helper.ContextInformation;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.LanguageVersion;
import com.sun.javadoc.RootDoc;

/**
 * 
 * @author Domi
 * 
 */
public class BuilderDoclet {

	public static boolean start(RootDoc root) {
		try {
			readOptions(root.options());

			ClassDoc[] classes = root.classes();
			Set<ClassDoc> classesToProcess = filterClasses(classes);

			// Set up CodeMakers to run
			CompositeMaker codemakers = new CompositeMaker("Simple Java Makers");
			codemakers.addMaker(new DomainObjectMaker("Domain Object Builder"));

			String namePrefix = ContextInformation.getExecutionFieldString(ContextInformation.NAME_POSTFIX);
			String template = ContextInformation.getExecutionFieldString(ContextInformation.TEMPLATE_KEY);
			String singlename = ContextInformation.getExecutionFieldString(ContextInformation.SINGLENAME);

			String type = ContextInformation.getExecutionFieldString(ContextInformation.GENERAT_TYPE_KEY);

			// Iterate through all classes and execute the "make" method the
			// composite codemaker.
			if ("domain".equalsIgnoreCase(type)) {

				for (ClassDoc aClass : classesToProcess) {
					codemakers.make(aClass, namePrefix, template);
				}

			} else {

				String templateFile = ContextInformation.getExecutionFieldString(ContextInformation.TEMPLATE_KEY);

				DomainObjectBuilderMaker builderMaker = new DomainObjectBuilderMaker(classesToProcess);
				builderMaker.createTemplateContextInformation(null, singlename, templateFile);
				builderMaker.make(null, singlename, templateFile);

			}

		} finally {
			// do some cleaning
			clean();
		}

		return true;
	}

	/**
	 * Filters the classes to be processed
	 * 
	 * @param classes
	 *            all classes
	 * @return subset of all classes
	 */
	private static Set<ClassDoc> filterClasses(ClassDoc[] classes) {
		String singlename = ContextInformation.getExecutionFieldString(ContextInformation.SINGLENAME);

		Set<ClassDoc> classesToProcess = new HashSet<ClassDoc>();
		for (int i = 0; i < classes.length; i++) {
			ClassDoc classdoc = classes[i];
			if (checkPackage(classdoc.containingPackage().name()) && !singlename.equals(classdoc.typeName())) {
				classesToProcess.add(classdoc);
			}
		}
		return classesToProcess;
	}

	/**
	 * Checks if the given package should be processed.
	 * 
	 * @param packageName
	 *            The package name to be checked
	 * @return <code>true</code> if the processing should be done
	 */
	@SuppressWarnings("unchecked")
	private static boolean checkPackage(String packageName) {
		List<String> packages = (List<String>) ContextInformation
				.getExecutionField(ContextInformation.PACKAGES_KEY);
		if (packages == null) {
			return true;
		} else {
			for (String aPackageName : packages) {
				if (packageName.startsWith(aPackageName)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Clean some resources
	 */
	private static void clean() {
		// do some cleaning
		File css = new File("stylesheet.css");
		if (css.exists()) {
			css.delete();
		}
	}

	public static int optionLength(String option) {
		if (option.equals("-" + ContextInformation.SUBPACKAGE_KEY)) {
			return 2;
		} else if (option.equals("-" + ContextInformation.TEMPLATE_KEY)) {
			return 2;
		} else if (option.equals("-" + ContextInformation.TEMPLATE_DIRECTORY_KEY)) {
			return 2;
		} else if (option.equals("-" + ContextInformation.PACKAGES_KEY)) {
			return 2;
		} else if (option.equals("-" + ContextInformation.NAME_POSTFIX)) {
			return 2;
		} else if (option.equals("-" + ContextInformation.GENERAT_TYPE_KEY)) {
			return 2;
		} else if (option.equals("-" + ContextInformation.SINGLENAME)) {
			return 2;
		}
		return 0;
	}

	private static void readOptions(String[][] options) {
		for (int i = 0; i < options.length; i++) {
			String[] opt = options[i];
			if (opt[0].equals("-" + ContextInformation.SUBPACKAGE_KEY)) {
				ContextInformation.addExecutionField(ContextInformation.SUBPACKAGE_KEY, opt[1]);
			} else if (opt[0].equals("-" + ContextInformation.TEMPLATE_KEY)) {
				ContextInformation.addExecutionField(ContextInformation.TEMPLATE_KEY, opt[1]);
			} else if (opt[0].equals("-" + ContextInformation.SINGLENAME)) {
				ContextInformation.addExecutionField(ContextInformation.SINGLENAME, opt[1]);
			} else if (opt[0].equals("-" + ContextInformation.TEMPLATE_DIRECTORY_KEY)) {
				ContextInformation.addExecutionField(ContextInformation.TEMPLATE_DIRECTORY_KEY, opt[1]);
			} else if (opt[0].equals("-" + ContextInformation.NAME_POSTFIX)) {
				ContextInformation.addExecutionField(ContextInformation.NAME_POSTFIX, opt[1]);
			} else if (opt[0].equals("-" + ContextInformation.GENERAT_TYPE_KEY)) {
				ContextInformation.addExecutionField(ContextInformation.GENERAT_TYPE_KEY, opt[1]);
			} else if (opt[0].equals("-" + ContextInformation.PACKAGES_KEY)) {

				String[] packages = opt[1].split(";");
				List<String> allPackages = Arrays.asList(packages);

				ContextInformation.addExecutionField(ContextInformation.PACKAGES_KEY, allPackages);
			}
		}
	}

	// public static boolean validOptions(String options[][],
	// DocErrorReporter reporter) {
	// boolean foundTagOption = false;
	// for (int i = 0; i < options.length; i++) {
	// String[] opt = options[i];
	// if (opt[0].equals("-outputDirectory")) {
	// if (foundTagOption) {
	// reporter.printError("Only one -tag option allowed.");
	// return false;
	// } else {
	// foundTagOption = true;
	// }
	// }
	// }
	// if (!foundTagOption) {
	// reporter
	// .printError("Usage: javadoc -outputDirectory <outputDir> -doclet "
	// + BuilderDoclet.class + "...");
	// }
	// return foundTagOption;
	// }

	public static LanguageVersion languageVersion() {
		return LanguageVersion.JAVA_1_5;
	}
}
