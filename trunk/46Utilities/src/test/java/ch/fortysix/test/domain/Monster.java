/**
 * 
 */
package ch.fortysix.test.domain;

/**
 * @author Domi
 * 
 */
public class Monster {

	private String name;
	private boolean isAgressiv;
	private Gender gender;

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
	 * @return the isAgressiv
	 */
	public boolean isAgressiv() {
		return isAgressiv;
	}

	/**
	 * @param isAgressiv
	 *            the isAgressiv to set
	 */
	public void setAgressiv(boolean isAgressiv) {
		this.isAgressiv = isAgressiv;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Monster [gender=" + gender + ", isAgressiv=" + isAgressiv + ", name=" + name + "]";
	}
}
