package com.bence.yugioh.phases;

import com.bence.yugioh.YuGiOhGame;
import com.bence.yugioh.slots.CardSlot;

/**
 * Egy j�t�k f�zis.
 * @author Bence
 *
 */
public abstract class GamePhase {
	public YuGiOhGame Game;
	
	public String Name;
	
	public GamePhase(YuGiOhGame game){
		Game = game;
	}
	
	/**
	 * A k�vetkez� f�zist h�vja meg.
	 */
	public abstract void GotoNextPhase();
	
	/**
	 * Slot kattint�s esem�ny.
	 */
	public abstract void OnSlotClick(CardSlot slot);
	
	/**
	 * Visszaadja, hogy a k�vetkez� f�zis gombot meg lehet-e jelen�teni.
	 */
	public boolean CanShowNextPhaseButton(){
		return false;
	}
	
	/**
	 * A f�zis kezdetekor h�vodik meg.
	 */
	public void OnPhaseActivated(){
	}
}
