/**
 * 
 */
package ch.fortysix.test.domain;

/**
 * @author Domi
 * 
 */
public class Mouse {

	private String name;
	private Gender gender;
	private boolean hasChild;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param gender
	 *            the gender to set
	 */
	public void setGender(Gender gender) {
		this.gender = gender;
	}

	/**
	 * @return the hasChild
	 */
	public boolean isHasChild() {
		return hasChild;
	}

	/**
	 * @param hasChild
	 *            the hasChild to set
	 */
	public void setHasChild(boolean hasChild) {
		this.hasChild = hasChild;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Mouse [gender=" + gender + ", hasChild=" + hasChild + ", name=" + name + "]";
	}

}
