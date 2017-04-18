package com.bence.yugioh.cards;

import com.bence.yugioh.YuGiOhGame;
import com.bence.yugioh.slots.CardSlot;

/**
 * Szrony kepesseg, mely a szorny palyara helyezesekor aktivalodik.
 * @author Bence
 *
 */
public abstract class MonsterOnPlaceSpecial implements MonsterSpecial {
	/**
	 * Visszaadja a kepesseg leirasat.
	 */
	public String GetDescription() {
		return "???";
	}

	/**
	 * A kartya lehelyezesekor hivodik meg.
	 */
	public abstract void OnActivate(YuGiOhGame game, CardSlot placeSlot);
}
