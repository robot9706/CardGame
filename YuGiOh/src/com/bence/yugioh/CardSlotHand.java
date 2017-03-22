package com.bence.yugioh;

import java.awt.Image;

public class CardSlotHand extends CardSlot {
	public CardSlotHand(Player o,int x, int y){
		super(o,x,y);
	}
	
	public Image GetRenderImage(){
		return Art.CardSlot_Hand;
	}
}
