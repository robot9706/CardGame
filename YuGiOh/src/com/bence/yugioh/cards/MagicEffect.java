package com.bence.yugioh.cards;

import com.bence.yugioh.YuGiOhGame;
import com.bence.yugioh.player.Player;

/**
 * Varazskartya kepesseg.
 * @author Bence
 *
 */
public interface MagicEffect {
	/**
	 * Megmondja, hogy kell-e cel kartya.
	 */
	boolean RequiresTarget();
	
	/**
	 * Visszaadja a kartya leirasat.
	 */
	String GetDescription();
	
	/**
	 * Aktivalas cel nelkul.
	 */
	void Activate(YuGiOhGame game, Player by);
	
	/**
	 * Aktivalas celon.
	 */
	void ActivateOnTarget(Card target, YuGiOhGame game);
}
