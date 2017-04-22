package com.bence.yugioh.slots;

import java.awt.Graphics;
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
	
	public void Draw(Graphics g, Player viewer){
		super.Draw(g, viewer);
		
		if(!Owner.HandCardManager.CardPlacementAllowed && Card != null){ //Ha a jatekos mar nem rakhat le kartyat
			g.drawImage(Art.No, X, Y, Width, Height, null);
		}
	}
}
