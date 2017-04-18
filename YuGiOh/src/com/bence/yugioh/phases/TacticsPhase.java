package com.bence.yugioh.phases;

import com.bence.yugioh.YuGiOhGame;
import com.bence.yugioh.cards.CardMagic;
import com.bence.yugioh.cards.CardMonster;
import com.bence.yugioh.cards.MonsterOnPlaceSpecial;
import com.bence.yugioh.slots.CardSlot;
import com.bence.yugioh.slots.CardSlotHand;
import com.bence.yugioh.slots.CardSlotPlayfield;

/**
 * Taktikai fázis.
 * @author Bence
 *
 */
public class TacticsPhase extends GamePhase {
	//A fázis állapotai.
	private boolean _isActivatingMagic; //Varázskártyát akarok aktiválni.
	private boolean _isPlacingCard; //Kártyát akarok a pályára helyezni.
	private CardSlot _cardSource; //Tárolja, hogy honnan akarok kártyát helyezni a pályára.
	
	public TacticsPhase(YuGiOhGame game){
		super(game);
		
		Name = "Taktikai";
	}

	public boolean CanShowNextPhaseButton(){
		return true;
	}
	
	public void GotoNextPhase() {
		Game.SetPhase(Game.PhaseAttack, false); //A következõ fázis a támadó fázis, nincs játékos csere
	}
	
	public void OnSlotClick(CardSlot slot){
		if(slot == null){ //Ha nem slotra történt kattintás, akkor visszavonom az aktuális eseményt
			if(_isPlacingCard || _isActivatingMagic){
				_isPlacingCard = false;
				_isActivatingMagic = false;
				Game.ResetSlotHighlight();
				Game.RedrawFrame();
			}
			return;
		}
		
		if(slot.Owner != Game.HumanPlayer) //Ha nem a saját slotra akar kattintani a játékos akkor nem csinálok semmit
			return;
		
		if(_isPlacingCard){ //Ha kártyát akarok a pályára helyezni, megnézem, hogy abba a slot-ba lehet,e
			if(slot instanceof CardSlotPlayfield && slot.Card == null){ 
				if(((CardSlotPlayfield)slot).MonsterOnly == (_cardSource.Card instanceof CardMonster)){
					slot.Card = _cardSource.Card;
					
					if(slot.Card instanceof CardMonster){
						CardMonster m = (CardMonster)slot.Card;
						if(m.Special != null && m.Special instanceof MonsterOnPlaceSpecial){ //Ha a szörnynek van képessége aktiválom
							((MonsterOnPlaceSpecial)m.Special).OnActivate(Game, slot);
						}
					}
					
					Game.HumanPlayer.RemoveCardFromHand(_cardSource.Card);
					
					_isPlacingCard = false;
					
					Game.ResetSlotHighlight();
					Game.UpdateInspectedSlot();
					Game.RedrawFrame();
				}
			}
		}else if(_isActivatingMagic){ //Ha varázskártyát akarok aktiválni, megnézem hogy ezen a slot-on lehet-e
			if(slot instanceof CardSlotPlayfield && slot.Card != null && slot.Card instanceof CardMonster){
				((CardMagic)_cardSource.Card).Effect.ActivateOnTarget(slot.Card, Game);
				
				_cardSource.Card = null;
				
				_isActivatingMagic = false;
				Game.ResetSlotHighlight();
				Game.RedrawFrame();
			}
		}else{
			if(slot instanceof CardSlotHand){ //Ha kéz slotra kattintok akkor kártyát akarok a pályára helyezni
				if(slot.Card != null){
					_isPlacingCard = true;
					_isActivatingMagic = false;
					_cardSource = slot;
					
					Game.SetPlacementSlotHighlight(slot, (slot.Card instanceof CardMonster));
					Game.RedrawFrame();
				}
			}else if(slot instanceof CardSlotPlayfield){ //Ha a pályára kattnintok akkor vagy varázslatot aktiválok, vagy szörny kártyát forgatok.
				CardSlotPlayfield f = (CardSlotPlayfield)slot;
				if(f.Card != null){
					if(f.MonsterOnly){
						f.Card.IsRotated = !f.Card.IsRotated;
						
						Game.RedrawFrame();
					}else{
						CardMagic m = (CardMagic)slot.Card;
						if(m.Effect.RequiresTarget()){
							_isPlacingCard = false;
							_isActivatingMagic = true;
							_cardSource = slot;
							
							Game.SetMagicActivateSlotHighlight(slot);
							Game.RedrawFrame();
						}else{
							m.Effect.Activate(Game, Game.HumanPlayer);
							slot.Card = null;
							Game.RedrawFrame();
						}
					}
				}
			}
		}
	}
}
