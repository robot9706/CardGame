package com.bence.yugioh.cards;

import java.awt.Image;

/**
 * Varázskártya osztály.
 * @author Bence
 *
 */
public class CardMagic extends Card {
	public MagicEffect Effect;
	
	public CardMagic(String name, MagicEffect fx, int s){
		super(name, s);
		
		Effect = fx;
	}
	
	public CardMagic(String name, MagicEffect fx, Image front, int s){
		super(name, s);
		
		Effect = fx;
		FrontImage = front;
	}

	@Override
	public Card Clone() {
		return new CardMagic(Name, Effect, FrontImage, SaveUID);
	}
}
