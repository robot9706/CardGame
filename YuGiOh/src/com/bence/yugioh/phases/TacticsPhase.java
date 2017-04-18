package com.bence.yugioh.phases;

import com.bence.yugioh.YuGiOhGame;
import com.bence.yugioh.cards.CardMagic;
import com.bence.yugioh.cards.CardMonster;
import com.bence.yugioh.cards.MonsterOnPlaceSpecial;
import com.bence.yugioh.slots.CardSlot;
import com.bence.yugioh.slots.CardSlotHand;
import com.bence.yugioh.slots.CardSlotPlayfield;
import com.bence.yugioh.utils.Texts;

/**
 * Taktikai fazis.
 * @author Bence
 *
 */
public class TacticsPhase extends GamePhase {
	//A fazis allapotai.
	private boolean _isActivatingMagic; //Varazskartyat akarok aktivalni.
	private boolean _isPlacingCard; //Kartyat akarok a palyara helyezni.
	private CardSlot _cardSource; //Tarolja, hogy honnan akarok kartyat helyezni a palyara.
	
	public TacticsPhase(YuGiOhGame game){
		super(game);
		
		Name = Texts.TacticsPhaseText;
	}

	public boolean CanShowNextPhaseButton(){
		return true;
	}
	
	public void GotoNextPhase() {
		Game.SetPhase(Game.PhaseAttack, false); //A kovetkezo fazis a tamado fazis, nincs jatekos csere
	}
	
	public void OnSlotClick(CardSlot slot){
		if(slot == null){ //Ha nem slotra tortent kattintas, akkor visszavonom az aktualis esemenyt
			if(_isPlacingCard || _isActivatingMagic){
				_isPlacingCard = false;
				_isActivatingMagic = false;
				Game.ResetSlotHighlight();
				Game.RedrawFrame();
			}
			return;
		}
		
		if(slot.Owner != Game.HumanPlayer) //Ha nem a sajat slotra akar kattintani a jatekos akkor nem csinalok semmit
			return;
		
		if(_isPlacingCard){ //Ha kartyat akarok a palyara helyezni, megnezem, hogy abba a slot-ba lehet,e
			if(slot instanceof CardSlotPlayfield && slot.Card == null){ 
				if(((CardSlotPlayfield)slot).MonsterOnly == (_cardSource.Card instanceof CardMonster)){
					slot.Card = _cardSource.Card;
					
					if(slot.Card instanceof CardMonster){
						CardMonster m = (CardMonster)slot.Card;
						if(m.Special != null && m.Special instanceof MonsterOnPlaceSpecial){ //Ha a szornynek van kepessege aktivalom
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
		}else if(_isActivatingMagic){ //Ha varazskartyat akarok aktivalni, megnezem hogy ezen a slot-on lehet-e
			if(slot instanceof CardSlotPlayfield && slot.Card != null && slot.Card instanceof CardMonster){
				((CardMagic)_cardSource.Card).Effect.ActivateOnTarget(slot.Card, Game);
				
				_cardSource.Card = null;
				
				_isActivatingMagic = false;
				Game.ResetSlotHighlight();
				Game.RedrawFrame();
			}
		}else{
			if(slot instanceof CardSlotHand){ //Ha kez slotra kattintok akkor kartyat akarok a palyara helyezni
				if(slot.Card != null){
					_isPlacingCard = true;
					_isActivatingMagic = false;
					_cardSource = slot;
					
					Game.SetPlacementSlotHighlight(slot, (slot.Card instanceof CardMonster));
					Game.RedrawFrame();
				}
			}else if(slot instanceof CardSlotPlayfield){ //Ha a palyara kattnintok akkor vagy varazslatot aktivalok, vagy szorny kartyat forgatok.
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
