package com.bence.yugioh.slots;

import java.awt.Graphics;
import java.awt.Image;

import com.bence.yugioh.Art;
import com.bence.yugioh.player.Player;

public class CardSlotPlayfield extends CardSlot {
	public boolean MonsterOnly;
	public boolean Used;
	
	public CardSlotPlayfield(Player o, int x, int y, boolean monsterOnly){
		super(o,x,y);
		
		MonsterOnly = monsterOnly;
		Used = false;
	}
	
	public Image GetRenderImage(){
		if(MonsterOnly)
			return Art.CardSlot_MagicTrap;
		
		return Art.CardSlot_Monsters;
	}
	
	protected Image GetCardImage(Player viewer){
		return Card.FrontImage;
	}
	
	public void Draw(Graphics g, Player viewer){
		super.Draw(g, viewer);
		
		if(Used && Card != null){
			g.drawImage(Art.No, X, Y, Width, Height, null);
		}
	}
}
