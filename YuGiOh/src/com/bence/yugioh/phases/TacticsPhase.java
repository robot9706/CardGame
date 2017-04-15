package com.bence.yugioh.phases;

import com.bence.yugioh.YuGiOhGame;
import com.bence.yugioh.cards.CardMonster;
import com.bence.yugioh.player.Player;
import com.bence.yugioh.slots.CardSlot;
import com.bence.yugioh.slots.CardSlotHand;
import com.bence.yugioh.slots.CardSlotPlayfield;

public class TacticsPhase extends GamePhase {
	private boolean _isPlacingCard;
	private CardSlot _cardSource;
	
	public TacticsPhase(YuGiOhGame game){
		super(game);
		
		Name = "Taktikai";
	}

	public boolean CanShowNextPhaseButton(){
		return true;
	}
	
	public void GotoNextPhase() {
		Game.SetPhase(Game.PhaseAttack);
	}
	
	public void OnSlotClick(CardSlot slot, Player byPlayer){
		if(slot.Owner == byPlayer){
			if(_isPlacingCard){
				if(slot instanceof CardSlotPlayfield){ 
					if(((CardSlotPlayfield)slot).MonsterOnly == (_cardSource.Card instanceof CardMonster)){
						slot.Card = _cardSource.Card;
					
						byPlayer.RemoveCardFromHand(_cardSource.Card);
					
						_isPlacingCard = false;
					
						Game.ResetSlotHighlight();
						Game.UpdateInspectedSlot();
						Game.RedrawFrame();
					}
				}
			}else{
				if(slot instanceof CardSlotHand){
					if(slot.Card != null){
						_isPlacingCard = true;
						_cardSource = slot;
					
						Game.SetPlayerSlotHighlight(byPlayer, slot, (slot.Card instanceof CardMonster));
					}
				}else if(slot instanceof CardSlotPlayfield){
					CardSlotPlayfield f = (CardSlotPlayfield)slot;
					if(f.MonsterOnly && f.Card != null){
						f.Card.IsRotated = !f.Card.IsRotated;
						
						Game.RedrawFrame();
					}
				}
			}
		}
	}
}
