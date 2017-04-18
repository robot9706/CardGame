package com.bence.yugioh.phases;

import com.bence.yugioh.YuGiOhGame;
import com.bence.yugioh.slots.CardSlot;

/**
 * Egy jatek fazis.
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
	 * A kovetkezo fazist hivja meg.
	 */
	public abstract void GotoNextPhase();
	
	/**
	 * Slot kattintas esemeny.
	 */
	public abstract void OnSlotClick(CardSlot slot);
	
	/**
	 * Visszaadja, hogy a kovetkezo fazis gombot meg lehet-e jeleniteni.
	 */
	public boolean CanShowNextPhaseButton(){
		return false;
	}
	
	/**
	 * A fazis kezdetekor hivodik meg.
	 */
	public void OnPhaseActivated(){
	}
}
