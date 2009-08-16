package ch.fortysix.util.doclet.maker.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ConstructorDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.Type;

public class ClassFileHelper {

	/** initialize the logging */
	static final Log log = LogFactory.getLog(ClassFileHelper.class);

	/**
	 * Gets all methods from - also the inherited ones.
	 * 
	 * @param clazz
	 *            The class to get the methods from
	 * @param methods
	 *            The list to add the new methods to. (must not be null)
	 */
	public void getAllMethodsRecursive(ClassDoc clazz, List<MethodDoc> methods) {
		if (clazz == null)
			return;
		if (methods == null) {
			throw new IllegalArgumentException("methodes must not be null");
		}
		MethodDoc[] origins = clazz.methods();

		if (origins != null && origins.length > 0) {
			for (MethodDoc methodDoc : origins) {
				methods.add(methodDoc);
			}
		}
		getAllMethodsRecursive(clazz.superclass(), methods);
	}

	/**
	 * Returns only the methods not overwritten.
	 * 
	 * @param originMethods
	 *            the original list of the methods.
	 * @return list of methods not overwritten.
	 */
	public List<MethodDoc> removeOverwitenMethods(List<MethodDoc> originMethods) {
		if (originMethods == null || originMethods.size() == 0)
			return originMethods;

		List<MethodDoc> newList = new ArrayList<MethodDoc>(originMethods);

		for (MethodDoc methodDoc : originMethods) {
			MethodDoc doc = methodDoc.overriddenMethod();
			// if we find that the method does have is overwriting, remove it
			// from the new list
			if (doc != null) {
				newList.remove(methodDoc);
			}
		}

		return newList;
	}

	/**
	 * Returns a list of all methods for the given class no doubles!
	 * 
	 * @param clazz
	 *            The class to get the methods for.
	 * @return list of unique methods.
	 */
	public List<MethodDoc> getObjectMethods(ClassDoc clazz) {
		return removeOverwitenMethods(getAllMethods(clazz));
	}

	/**
	 * Checks if the method is a setter by using this checks:
	 * <ul>
	 * <li>is a public method</li>
	 * <li>is not abstract</li>
	 * <li>is not static</li>
	 * <li>has 1 argument</li>
	 * <li>name starts with "set"</li>
	 * <li>return type is "void"</li>
	 * </ul>
	 * 
	 * @param The
	 *            method to check
	 * @return <code>true</code> if it fits the rules.
	 */
	public boolean isSetter(MethodDoc method) {
		if (method == null) {
			return false;
		}
		return method.isPublic() && !method.isAbstract() && !method.isStatic()
				&& method.parameters().length == 1
				&& method.name().startsWith("set")
				&& "void".equals(method.returnType().toString());
	}

	/**
	 * Removes 'set' from the methods name.
	 * 
	 * @param method
	 * @return setter name
	 */
	public String getSetterName(MethodDoc method) {
		return method.name().charAt(3) + method.name().substring(4);
	}

	/**
	 * Returns the first parameter of a method signature
	 * 
	 * @param method
	 *            the method
	 * @return the first parameter
	 */
	public Parameter getSingleParameter(MethodDoc method) {
		if (method == null) {
			return null;
		}
		if (method.parameters() == null || method.parameters().length == 0) {
			return null;
		}
		return method.parameters()[0];
	}

	/**
	 * Tries to get some mockup data for the given type.
	 * 
	 * @param type
	 * @return
	 */
	public String getMockupData(Parameter parameter) {
		Type type = parameter.type();
		if (type.isPrimitive()) {
			return PRIMITIVE_MOKCUP.get(type.typeName());
		} else {
			ClassDoc classDoc = type.asClassDoc();
			ConstructorDoc[] constructors = classDoc.constructors();
			for (ConstructorDoc constructor : constructors) {
				if (constructor.parameters() == null
						|| constructor.parameters().length == 0) {
					return "new " + constructor.qualifiedName()
							+ type.dimension() + "()";
				}
			}
		}
		return null;
	}

	private static Map<String, String> PRIMITIVE_MOKCUP = new HashMap<String, String>();
	static {
		PRIMITIVE_MOKCUP.put("void", null);
		PRIMITIVE_MOKCUP.put("int", "1");
		PRIMITIVE_MOKCUP.put("boolean", "true");
		PRIMITIVE_MOKCUP.put("char", "'C'");
		PRIMITIVE_MOKCUP.put("long", "10000L");
		PRIMITIVE_MOKCUP.put("double", "2.2");
	}

	/**
	 * Returns all methods of the given class - it might have doubles because of
	 * over writhing.
	 * 
	 * @param clazz
	 *            the class to get the methods for
	 * @return list with all methods
	 */
	public List<MethodDoc> getAllMethods(ClassDoc clazz) {
		List<MethodDoc> methodList = new ArrayList<MethodDoc>();
		getAllMethodsRecursive(clazz, methodList);
		return methodList;
	}

	/**
	 * Returns a list of all possible includes for the class.
	 * 
	 * @param clazz
	 *            the class to analyze
	 * @return list of types
	 */
	public Set<Type> getAllNeedImports(ClassDoc clazz) {
		List<MethodDoc> methods = getObjectMethods(clazz);
		Set<Type> notVoidMethods = new HashSet<Type>();
		for (MethodDoc method : methods) {
			if (method != null && method.returnType() != null) {
				if (!method.returnType().isPrimitive()
						&& !isBasicType(method.returnType())) {
					notVoidMethods.add(method.returnType());
				} else if (method.parameters().length == 1
						&& !method.parameters()[0].type().isPrimitive()
						&& !isBasicType(method.parameters()[0].type())) {
					notVoidMethods.add(method.parameters()[0].type());
				}
			}
		}
		return notVoidMethods;
	}

	private boolean isBasicType(Type type) {
		return type.qualifiedTypeName().startsWith("java.lang");
	}

	/**
	 * Returns the type of a method <code>build()</code>
	 * 
	 * @param classDoc
	 *            the builder
	 * @return the product of a builder
	 */
	public Type getBuilderProduct(ClassDoc classDoc) {
		List<MethodDoc> methods = this.getAllMethods(classDoc);
		for (MethodDoc methodDoc : methods) {
			if ("build".equals(methodDoc.name())) {
				return methodDoc.returnType();
			}
		}
		return null;
	}

	/**
	 * Lowers the first character of a given string.
	 * 
	 * @param string
	 *            original.
	 * @return same string, but first character to lower case.
	 */
	public String firstCharToLowerCase(String string) {
		if (string == null) {
			return null;
		}
		String tail = string.substring(1);
		Character first = string.charAt(0);
		return Character.toLowerCase(first) + tail;
	}
}
