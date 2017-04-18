package com.bence.yugioh.cards;

import com.bence.yugioh.YuGiOhGame;
import com.bence.yugioh.player.Player;

/**
 * Var�zsk�rtya k�pess�g.
 * @author Bence
 *
 */
public interface MagicEffect {
	/**
	 * Megmondja, hogy kell-e c�l k�rtya.
	 */
	boolean RequiresTarget();
	
	/**
	 * Visszaadja a k�rtya le�r�s�t.
	 */
	String GetDescription();
	
	/**
	 * Aktiv�l�s c�l n�lk�l.
	 */
	void Activate(YuGiOhGame game, Player by);
	
	/**
	 * Aktiv�l�s c�lon.
	 */
	void ActivateOnTarget(Card target, YuGiOhGame game);
}
