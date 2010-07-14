package ch.fortysix.util.doclet.maker.helper;

public class TargetFileInfo {
	String targetName = null;
	String targetPackage = null;

	/**
	 * @return the targetName
	 */
	public String getTargetName() {
		return targetName;
	}

	/**
	 * @param targetName
	 *            the targetName to set
	 */
	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	/**
	 * @return the targetPackage
	 */
	public String getTargetPackage() {
		return targetPackage;
	}

	/**
	 * @param targetPackage
	 *            the targetPackage to set
	 */
	public void setTargetPackage(String targetPackage) {
		this.targetPackage = targetPackage;
	}

}
