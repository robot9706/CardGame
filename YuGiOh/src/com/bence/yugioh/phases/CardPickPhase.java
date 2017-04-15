package com.bence.yugioh.phases;

import com.bence.yugioh.YuGiOhGame;
import com.bence.yugioh.player.Player;
import com.bence.yugioh.slots.CardSlot;
import com.bence.yugioh.slots.CardSlotStack;

public class CardPickPhase extends GamePhase {
	public CardPickPhase(YuGiOhGame game){
		super(game);
		
		Name = "Kártya húzás";
	}
	
	public void GotoNextPhase(){
		Game.SetPhase(new TacticsPhase(Game));
	}
	
	public void OnPhaseActivated(){
		if(Game.ComputerPlayer.Deck.size() == 0 && Game.HumanPlayer.Deck.size() == 0){
			GotoNextPhase();
			//TODO: STUFF
		}
	}
	
	public void OnSlotClick(CardSlot slot, Player byPlayer){
		if(slot instanceof CardSlotStack && slot.Owner == Game.HumanPlayer)
		{
			slot.OnClick(byPlayer);
		
			GotoNextPhase();
		}
	}
}
