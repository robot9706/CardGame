package com.bence.yugioh.cards;

public class CardMonster extends Card {
	public int Attack;
	public int Defense;
	
	public MonsterSpecial Special;
	
	public CardMonster(String name, int atk, int def){
		super(name);
		
		Attack = atk;
		Defense = def;
		
		Special = null;
	}
	
	public CardMonster(String name, int atk, int def, MonsterSpecial spec){
		super(name);
		
		Attack = atk;
		Defense = def;
		
		Special = spec;
	}

	@Override
	public Card Clone() {
		return new CardMonster(Name, Attack, Defense, Special);
	}
}
