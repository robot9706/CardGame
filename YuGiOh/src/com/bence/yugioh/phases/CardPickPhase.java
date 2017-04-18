package com.bence.yugioh.phases;

import com.bence.yugioh.YuGiOhGame;
import com.bence.yugioh.player.Player;
import com.bence.yugioh.slots.CardSlot;
import com.bence.yugioh.slots.CardSlotStack;
import com.bence.yugioh.utils.Texts;

/**
 * Kartyahuzas fazis.
 * @author Bence
 *
 */
public class CardPickPhase extends GamePhase {
	public CardPickPhase(YuGiOhGame game){
		super(game);
		
		Name = Texts.CardPickPhaseText;
	}
	
	public void GotoNextPhase(){
		Game.SetPhase(Game.PhaseTactics, false);
	}
	
	public void OnPhaseActivated(){
		CheckPlayerDeck(Game.PhasePlayer);
	}
	
	/**
	 * Megnezi, hogy egy jatekosnak van-e kartyaja a paklijaba, ha nincs 500 sebzest kap.
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
