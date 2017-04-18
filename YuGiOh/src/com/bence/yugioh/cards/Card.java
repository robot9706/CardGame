package com.bence.yugioh.cards;

import java.awt.Image;

/**
 * K�rtya alaposzt�ly.
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
	 * Ad egy kl�nt a k�rty�r�l.
	 */
	public abstract Card Clone();
}
