package com.bence.yugioh.cards;

import com.bence.yugioh.YuGiOhGame;
import com.bence.yugioh.slots.CardSlot;

public abstract class MonsterOnPlaceSpecial implements MonsterSpecial {
	public String GetDescription() {
		return "???";
	}

	public abstract void OnActivate(YuGiOhGame game, CardSlot placeSlot);
}
