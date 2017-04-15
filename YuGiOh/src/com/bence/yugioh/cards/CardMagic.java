package com.bence.yugioh.cards;

public class CardMagic extends Card {
	public CardMagic(String name){
		super(name);
	}

	@Override
	public Card Clone() {
		return new CardMagic(Name);
	}
}
