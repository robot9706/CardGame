package com.bence.yugioh.cards;

import com.bence.yugioh.YuGiOhGame;
import com.bence.yugioh.slots.CardSlot;

/**
 * Szr�ny k�pess�g, mely a sz�rny p�ly�ra helyez�sekor aktiv�l�dik.
 * @author Bence
 *
 */
public abstract class MonsterOnPlaceSpecial implements MonsterSpecial {
	/**
	 * Visszaadja a k�pess�g le�r�s�t.
	 */
	public String GetDescription() {
		return "???";
	}

	/**
	 * A k�rtya lehelyez�sekor h�vodik meg.
	 */
	public abstract void OnActivate(YuGiOhGame game, CardSlot placeSlot);
}
