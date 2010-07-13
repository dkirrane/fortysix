/**
 * 
 */
package ch.fortysix.test.domain;

import java.util.List;

/**
 * @author Domi
 * 
 */
public class Zoo {
	/**
	 * @return the monsters
	 */
	public List<Monster> getMonsters() {
		return monsters;
	}

	/**
	 * @return the mouses
	 */
	public List<Mouse> getMouses() {
		return mouses;
	}

	private List<Monster> monsters;
	private List<Mouse> mouses;

	public void showAnimals() {
		for (Mouse mouse : mouses) {
			System.out.println("mouse: " + mouse);
		}
		for (Monster monster : monsters) {
			System.out.println("monster: " + monster);
		}
	}

	/**
	 * @param monsters
	 *            the monsters to set
	 */
	public void setMonsters(List<Monster> monsters) {
		this.monsters = monsters;
	}

	/**
	 * @param mouses
	 *            the mouses to set
	 */
	public void setMouses(List<Mouse> mouses) {
		this.mouses = mouses;
	}
}
