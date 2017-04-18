package com.bence.yugioh.phases;

import com.bence.yugioh.YuGiOhGame;
import com.bence.yugioh.slots.CardSlot;

/**
 * Egy játék fázis.
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
	 * A következõ fázist hívja meg.
	 */
	public abstract void GotoNextPhase();
	
	/**
	 * Slot kattintás esemény.
	 */
	public abstract void OnSlotClick(CardSlot slot);
	
	/**
	 * Visszaadja, hogy a következõ fázis gombot meg lehet-e jeleníteni.
	 */
	public boolean CanShowNextPhaseButton(){
		return false;
	}
	
	/**
	 * A fázis kezdetekor hívodik meg.
	 */
	public void OnPhaseActivated(){
	}
}
