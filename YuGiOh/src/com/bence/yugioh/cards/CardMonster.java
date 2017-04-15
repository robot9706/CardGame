package com.bence.yugioh.cards;

public class CardMonster extends Card {
	public int Attack;
	public int Defense;
	
	public CardMonster(String name, int atk, int def){
		super(name);
		
		Attack = atk;
		Defense = def;
	}

	@Override
	public Card Clone() {
		return new CardMonster(Name, Attack, Defense);
	}
}
