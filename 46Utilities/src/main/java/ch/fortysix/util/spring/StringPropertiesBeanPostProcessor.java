/**
 * 
 */
package ch.fortysix.util.spring;

import java.lang.reflect.Field;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * This PostProcessor is able to process all string fields/properties of all
 * spring beans. It checks whether the current value matches the defined regex
 * and replaces the value with a value loaded from the backend.
 * 
 * @author Domi
 * 
 */
public class StringPropertiesBeanPostProcessor implements BeanPostProcessor {

	private static final String DEFAULT_PROPERTY_PREFIX = "#{";
	private static final String DEFAULT_PROPERTY_SUFFIX = "}";

	private boolean processAfterInit = false;
	private boolean processBeforInit = true;
	private String prefix = DEFAULT_PROPERTY_PREFIX;
	private String suffix = DEFAULT_PROPERTY_SUFFIX;

	private PropertyResolver propertyResolver;

	/**
	 * @param propertyResolver
	 *            the propertyResolver to set
	 */
	public void setPropertyResolver(PropertyResolver propertyResolver) {
		this.propertyResolver = propertyResolver;
	}

	/**
	 * @param prefix
	 *            the prefix to set
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	/**
	 * @param suffix
	 *            the suffix to set
	 */
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.springframework.beans.factory.config.BeanPostProcessor#
	 * postProcessAfterInitialization(java.lang.Object, java.lang.String)
	 */
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (processAfterInit) {
			this.replaceFieldStringValues(bean);
		}
		return bean;
	}

	/**
	 * @param processAfterInit
	 *            the processAfterInit to set
	 */
	public void setProcessAfterInit(boolean processAfterInit) {
		this.processAfterInit = processAfterInit;
	}

	/**
	 * @param processBeforInit
	 *            the processBeforInit to set
	 */
	public void setProcessBeforInit(boolean processBeforInit) {
		this.processBeforInit = processBeforInit;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.springframework.beans.factory.config.BeanPostProcessor#
	 * postProcessBeforeInitialization(java.lang.Object, java.lang.String)
	 */
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		if (processBeforInit) {
			this.replaceFieldStringValues(bean);
		}
		return bean;
	}

	/**
	 * Checks whether a field of the passed object is of type string. All string
	 * values will be checked if it matches a matcher/regex to be replaced.
	 * 
	 * @param bean
	 *            the object to be checked
	 */
	private void replaceFieldStringValues(Object bean) {
		Field[] fields = bean.getClass().getDeclaredFields();

		try {
			for (int i = 0; i < fields.length; i++) {
				if (fields[i].getType().equals(String.class)) {
					fields[i].setAccessible(true);
					String original = (String) fields[i].get(bean);

					// check if we have to replace the value
					if (original.startsWith(prefix) && original.endsWith(suffix)) {
						String key = original.substring(prefix.length(), original.length() - suffix.length());
						String newValue = propertyResolver.resolvePropertyValue(key);
						if (newValue == null) {
							throw new IllegalArgumentException("could not replace config property: " + original);
						}
						System.out.println("-->replace [" + original + "] with [" + newValue + "]");
						fields[i].set(bean, newValue);
					}
				}
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

}
