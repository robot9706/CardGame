package com.bence.yugioh.phases;

import com.bence.yugioh.YuGiOhGame;
import com.bence.yugioh.cards.CardMagic;
import com.bence.yugioh.cards.CardMonster;
import com.bence.yugioh.cards.MonsterOnPlaceSpecial;
import com.bence.yugioh.slots.CardSlot;
import com.bence.yugioh.slots.CardSlotHand;
import com.bence.yugioh.slots.CardSlotPlayfield;

/**
 * Taktikai f�zis.
 * @author Bence
 *
 */
public class TacticsPhase extends GamePhase {
	//A f�zis �llapotai.
	private boolean _isActivatingMagic; //Var�zsk�rty�t akarok aktiv�lni.
	private boolean _isPlacingCard; //K�rty�t akarok a p�ly�ra helyezni.
	private CardSlot _cardSource; //T�rolja, hogy honnan akarok k�rty�t helyezni a p�ly�ra.
	
	public TacticsPhase(YuGiOhGame game){
		super(game);
		
		Name = "Taktikai";
	}

	public boolean CanShowNextPhaseButton(){
		return true;
	}
	
	public void GotoNextPhase() {
		Game.SetPhase(Game.PhaseAttack, false); //A k�vetkez� f�zis a t�mad� f�zis, nincs j�t�kos csere
	}
	
	public void OnSlotClick(CardSlot slot){
		if(slot == null){ //Ha nem slotra t�rt�nt kattint�s, akkor visszavonom az aktu�lis esem�nyt
			if(_isPlacingCard || _isActivatingMagic){
				_isPlacingCard = false;
				_isActivatingMagic = false;
				Game.ResetSlotHighlight();
				Game.RedrawFrame();
			}
			return;
		}
		
		if(slot.Owner != Game.HumanPlayer) //Ha nem a saj�t slotra akar kattintani a j�t�kos akkor nem csin�lok semmit
			return;
		
		if(_isPlacingCard){ //Ha k�rty�t akarok a p�ly�ra helyezni, megn�zem, hogy abba a slot-ba lehet,e
			if(slot instanceof CardSlotPlayfield && slot.Card == null){ 
				if(((CardSlotPlayfield)slot).MonsterOnly == (_cardSource.Card instanceof CardMonster)){
					slot.Card = _cardSource.Card;
					
					if(slot.Card instanceof CardMonster){
						CardMonster m = (CardMonster)slot.Card;
						if(m.Special != null && m.Special instanceof MonsterOnPlaceSpecial){ //Ha a sz�rnynek van k�pess�ge aktiv�lom
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
		}else if(_isActivatingMagic){ //Ha var�zsk�rty�t akarok aktiv�lni, megn�zem hogy ezen a slot-on lehet-e
			if(slot instanceof CardSlotPlayfield && slot.Card != null && slot.Card instanceof CardMonster){
				((CardMagic)_cardSource.Card).Effect.ActivateOnTarget(slot.Card, Game);
				
				_cardSource.Card = null;
				
				_isActivatingMagic = false;
				Game.ResetSlotHighlight();
				Game.RedrawFrame();
			}
		}else{
			if(slot instanceof CardSlotHand){ //Ha k�z slotra kattintok akkor k�rty�t akarok a p�ly�ra helyezni
				if(slot.Card != null){
					_isPlacingCard = true;
					_isActivatingMagic = false;
					_cardSource = slot;
					
					Game.SetPlacementSlotHighlight(slot, (slot.Card instanceof CardMonster));
					Game.RedrawFrame();
				}
			}else if(slot instanceof CardSlotPlayfield){ //Ha a p�ly�ra kattnintok akkor vagy var�zslatot aktiv�lok, vagy sz�rny k�rty�t forgatok.
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
