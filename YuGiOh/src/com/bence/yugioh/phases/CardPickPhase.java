package com.bence.yugioh.phases;

import com.bence.yugioh.YuGiOhGame;
import com.bence.yugioh.player.Player;
import com.bence.yugioh.slots.CardSlot;
import com.bence.yugioh.slots.CardSlotStack;

/**
 * K�rtyah�z�s f�zis.
 * @author Bence
 *
 */
public class CardPickPhase extends GamePhase {
	public CardPickPhase(YuGiOhGame game){
		super(game);
		
		Name = "K�rtya h�z�s";
	}
	
	public void GotoNextPhase(){
		Game.SetPhase(Game.PhaseTactics, false);
	}
	
	public void OnPhaseActivated(){
		CheckPlayerDeck(Game.PhasePlayer);
	}
	
	/**
	 * Megn�zi, hogy egy j�t�kosnak van-e k�rty�ja a paklij�ba, ha nincs 500 sebz�st kap.
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
