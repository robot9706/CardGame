package com.bence.yugioh.cards;

import java.awt.Image;

import com.bence.yugioh.Art;

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
	
	public abstract Card Clone();
}
