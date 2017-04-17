package com.bence.yugioh.phases;

import com.bence.yugioh.YuGiOhGame;
import com.bence.yugioh.slots.CardSlot;

public abstract class GamePhase {
	public YuGiOhGame Game;
	
	public String Name;
	
	public GamePhase(YuGiOhGame game){
		Game = game;
	}
	
	public abstract void GotoNextPhase();
	public abstract void OnSlotClick(CardSlot slot);
	
	public boolean CanShowNextPhaseButton(){
		return false;
	}
	
	public void OnPhaseActivated(){
	}
}
