package ch.fortysix.util.doclet.maker.helper;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Domi
 * 
 */
public class ContextInformation {

	public static final String SUBPACKAGE_KEY = "subpackage";

	public static final String TEMPLATE_KEY = "template";

	public static final String TEMPLATE_DIRECTORY_KEY = "templateDir";

	public static final String PACKAGES_KEY = "packages";

	public static final String NAME_POSTFIX = "postfix";

	public static final String GENERAT_TYPE_KEY = "type";

	public static final String SINGLENAME = "name";

	private static Map<String, Object> context = new HashMap<String, Object>();

	static {
		context.put(TEMPLATE_KEY, "/META-INF/builder.vm");
		context.put(SUBPACKAGE_KEY, "builder");
		context.put(TEMPLATE_DIRECTORY_KEY, System.getProperty("user.dir"));
		context.put(PACKAGES_KEY, null);
		context.put(NAME_POSTFIX, "Builder");
		context.put(GENERAT_TYPE_KEY, "domain");
		context.put(SINGLENAME, "DomainObjectBuilder");
	}

	public static void addExecutionField(String key, Object value) {
		context.put(key, value);
	}

	public static Object getExecutionField(String key) {
		return context.get(key);
	}

	public static String getExecutionFieldString(String key) {
		return (String) context.get(key);
	}

}
