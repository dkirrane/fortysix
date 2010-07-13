package ch.fortysix.util.doclet.maker;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.sun.javadoc.ClassDoc;

/**
 * A <code>CompositeMaker</code> is a <code>Composite</code> of Makers. It runs
 * a collections of Makers. Here is an example:
 * 
 * <pre>
 * CompositeMaker makers = new CompositeMaker(&quot;test makers&quot;);
 * makers.addMaker(new MyTestMaker1(&quot;Maker1&quot;));
 * makers.addMaker(new MyTestMaker2(&quot;Maker2&quot;));
 * </pre>
 * 
 * @see Maker
 */

public class CompositeMaker implements Maker {

	private Logger logger = null;

	/**
	 * Container for the makers.
	 */
	private List<Maker> makers = new ArrayList<Maker>(10);

	/**
	 * Name of the Maker.
	 */
	private String name;

	/**
	 * Construct a CompositeMaker with a given name
	 */
	public CompositeMaker(String name) {
		this.name = name;
		logger = Logger.getLogger(getClass().getName());
	}

	/**
	 * Add a maker to the composite.
	 */
	public boolean addMaker(Maker cm) {
		return makers.add(cm);
	}

	/**
	 * Returns the number of makers in the collection.
	 */
	public int size() {
		return makers.size();
	}

	/**
	 * Executes all the Makers by running their <code>make</code> method.
	 */
	public void make(ClassDoc classdoc, String namePostfix, String velocityTemplate) {
		logger.info("Executing " + this.toString());
		for (Maker maker : makers) {
			maker.make(classdoc, namePostfix, velocityTemplate);
		}
	}

	/**
	 * Helper method to print the CompositeMaker's name.
	 */
	public String toString() {
		if (name != null) {
			return name;
		} else {
			return "";
		}
	}

}
