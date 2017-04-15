package com.bence.yugioh.cards;

import java.awt.Image;

import com.bence.yugioh.Art;

public abstract class Card {
	public String Name;
	public boolean IsRotated;
	
	public Card(String name){
		Name = name;
	}
	
	public Image GetBackImage(){
		return Art.CardBack;
	}
	
	public Image GetFrontImage(){
		return Art.CardFront_Temp;
	}
	
	public abstract Card Clone();
}
