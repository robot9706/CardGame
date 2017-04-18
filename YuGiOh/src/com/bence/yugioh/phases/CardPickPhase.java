package com.bence.yugioh.phases;

import com.bence.yugioh.YuGiOhGame;
import com.bence.yugioh.player.Player;
import com.bence.yugioh.slots.CardSlot;
import com.bence.yugioh.slots.CardSlotStack;

/**
 * Kártyahúzás fázis.
 * @author Bence
 *
 */
public class CardPickPhase extends GamePhase {
	public CardPickPhase(YuGiOhGame game){
		super(game);
		
		Name = "Kártya húzás";
	}
	
	public void GotoNextPhase(){
		Game.SetPhase(Game.PhaseTactics, false);
	}
	
	public void OnPhaseActivated(){
		CheckPlayerDeck(Game.PhasePlayer);
	}
	
	/**
	 * Megnézi, hogy egy játékosnak van-e kártyája a paklijába, ha nincs 500 sebzést kap.
	 */
	private void CheckPlayerDeck(Player p){
		if(p.Deck.size() == 0){
			Game.DamagePlayer(p, 500);
			
			GotoNextPhase();
		}
	}
	
	public void OnSlotClick(CardSlot slot){
		if(slot instanceof CardSlotStack)
		{
			slot.OnClick(Game.HumanPlayer);
		
			GotoNextPhase();
		}
	}
}
