package com.bence.yugioh;

import java.awt.Image;

public class Card {
	public String Name;
	public boolean IsRotated;
	
	public Card(){
		Name = "YOLO";
	}
	
	public Image GetBackImage(){
		return Art.CardBack;
	}
	
	public Image GetFrontImage(){
		return Art.CardFront_Temp;
	}
}
