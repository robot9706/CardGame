package com.bence.yugioh;

import java.awt.Image;

public class CardSlotPlayfield extends CardSlot {
	public boolean MonsterOnly;
	
	public CardSlotPlayfield(Player o, int x, int y, boolean monsterOnly){
		super(o,x,y);
		
		MonsterOnly = monsterOnly;
	}
	
	public Image GetRenderImage(){
		if(MonsterOnly)
			return Art.CardSlot_MagicTrap;
		
		return Art.CardSlot_Monsters;
	}
}
