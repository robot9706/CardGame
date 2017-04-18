package com.bence.yugioh.cards;

import com.bence.yugioh.YuGiOhGame;
import com.bence.yugioh.player.Player;

/**
 * Varázskártya képesség.
 * @author Bence
 *
 */
public interface MagicEffect {
	/**
	 * Megmondja, hogy kell-e cél kártya.
	 */
	boolean RequiresTarget();
	
	/**
	 * Visszaadja a kártya leírását.
	 */
	String GetDescription();
	
	/**
	 * Aktiválás cél nélkül.
	 */
	void Activate(YuGiOhGame game, Player by);
	
	/**
	 * Aktiválás célon.
	 */
	void ActivateOnTarget(Card target, YuGiOhGame game);
}
