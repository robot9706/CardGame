package com.bence.yugioh.slots;

import java.awt.Image;

import com.bence.yugioh.Art;
import com.bence.yugioh.player.Player;

/**
 * Slot, mely egy jatekos kezeben levo kartyat tartalmaz.
 * @author Bence
 *
 */
public class CardSlotHand extends CardSlot {
	public CardSlotHand(Player o,int x, int y){
		super(o,x,y);
	}
	
	public Image GetRenderImage(){
		return Art.CardSlot_Hand;
	}
}
