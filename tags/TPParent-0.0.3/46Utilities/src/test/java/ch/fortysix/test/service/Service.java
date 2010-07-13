/**
 * 
 */
package ch.fortysix.test.service;

import ch.fortysix.util.spring.PropertyResolver;

/**
 * @author Domi
 * 
 */
public class Service implements PropertyResolver {

	@Override
	public String resolvePropertyValue(String name) {
		return "§§§" + name + "§§§";
	}

}
