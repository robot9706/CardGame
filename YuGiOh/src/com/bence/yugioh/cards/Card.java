package com.bence.yugioh.cards;

import java.awt.Image;

/**
 * Kártya alaposztály.
 *
 */
public abstract class Card {
	public String Name;
	public boolean IsRotated;
	public Image FrontImage;
	
	public int Category;
	
	public int SaveUID;
	
	public Card(String name, int uid){
		Name = name;
		SaveUID = uid;
	}
	
	/**
	 * Ad egy klónt a kártyáról.
	 */
	public abstract Card Clone();
}
