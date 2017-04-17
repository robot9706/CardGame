package com.bence.yugioh.cards;

public class CardMagic extends Card {
	public MagicEffect Effect;
	
	public CardMagic(String name, MagicEffect fx){
		super(name);
		
		Effect = fx;
	}

	@Override
	public Card Clone() {
		return new CardMagic(Name, Effect);
	}
}
