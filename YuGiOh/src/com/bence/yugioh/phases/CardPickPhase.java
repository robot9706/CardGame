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
	
	public void OnSlotClick(CardSlot slot, Player byPlayer){
		if(slot instanceof CardSlotStack && slot.Owner == Game.HumanPlayer)
		{
			slot.OnClick(byPlayer);
		
			GotoNextPhase();
		}
	}
}
