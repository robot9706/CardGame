package com.bence.yugioh.cards;

import com.bence.yugioh.YuGiOhGame;
import com.bence.yugioh.slots.CardSlot;

/**
 * Szröny képesség, mely a szörny pályára helyezésekor aktiválódik.
 * @author Bence
 *
 */
public abstract class MonsterOnPlaceSpecial implements MonsterSpecial {
	/**
	 * Visszaadja a képesség leírását.
	 */
	public String GetDescription() {
		return "???";
	}

	/**
	 * A kártya lehelyezésekor hívodik meg.
	 */
	public abstract void OnActivate(YuGiOhGame game, CardSlot placeSlot);
}
