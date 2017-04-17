package com.bence.yugioh.cards;

import java.awt.Image;

public class CardMonster extends Card {
	public int Attack;
	public int Defense;
	
	public MonsterSpecial Special;
	
	public CardMonster(String name, int atk, int def, MonsterSpecial spec, int uid){
		super(name, uid);
		
		Attack = atk;
		Defense = def;
		
		Special = spec;
	}
	
	public CardMonster(String name, int atk, int def, MonsterSpecial spec, Image front, int uid){
		super(name, uid);
		
		Attack = atk;
		Defense = def;
		
		Special = spec;
		FrontImage = front;
	}

	@Override
	public Card Clone() {
		return new CardMonster(Name, Attack, Defense, Special, FrontImage, SaveUID);
	}
}
