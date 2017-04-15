package com.bence.yugioh.phases;

import com.bence.yugioh.YuGiOhGame;
import com.bence.yugioh.player.Player;
import com.bence.yugioh.slots.CardSlot;

public abstract class GamePhase {
	public YuGiOhGame Game;
	
	public String Name;
	
	public GamePhase(YuGiOhGame game){
		Game = game;
	}
	
	public abstract void GotoNextPhase();
	
	public boolean CanShowNextPhaseButton(){
		return false;
	}
	
	public void OnPhaseActivated(){
	}
	
	public void OnSlotClick(CardSlot slot, Player byPlayer){
	}
}
