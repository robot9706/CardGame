package com.bence.yugioh.phases;

import com.bence.yugioh.YuGiOhGame;
import com.bence.yugioh.cards.Card;
import com.bence.yugioh.cards.CardMonster;
import com.bence.yugioh.player.Player;
import com.bence.yugioh.slots.CardSlot;
import com.bence.yugioh.slots.CardSlotPlayfield;

public class AttackPhase extends GamePhase {
	private boolean _isAttacking = false;
	private CardSlot _attackSource;
	
	public AttackPhase(YuGiOhGame game){
		super(game);
		
		Name = "Támadó fázis";
	}
	
	public boolean CanShowNextPhaseButton(){
		return true;
	}
	
	public void GotoNextPhase() {
		Game.SetPhase(Game.PhaseCardPick, true);
	}
	
	public void OnSlotClick(CardSlot slot){
		if(slot == null){
			if(_isAttacking){
				_isAttacking = false;
				Game.ResetSlotHighlight();
				Game.RedrawFrame();
			}
			return;
		}
		
		if(slot instanceof CardSlotPlayfield){
			Card card = slot.Card;
			
			if(_isAttacking){
				if(slot.Owner == Game.ComputerPlayer && card != null){
					_isAttacking = false;
					Game.ResetSlotHighlight();
					
					DoAttack(_attackSource, slot);
					
					Game.RedrawFrame();
				}
			}else{
				if(slot.Owner == Game.HumanPlayer && card instanceof CardMonster && !card.IsRotated){
					Game.SetAttackSlotHighlight(slot);
					Game.RedrawFrame();
					_isAttacking = true;
					_attackSource = slot;
				}
			}
		}
	}
	
	private void DoAttack(CardSlot source, CardSlot target){
		CardMonster from = (CardMonster)source.Card;
		CardMonster to = (CardMonster)target.Card;
		
		if(target.Card.IsRotated){ //Attack vs Defense
			if(from.Attack > to.Defense){
				target.Card = null;
			}else if(from.Attack < to.Defense){
				int dif = to.Defense - from.Attack;
				Game.DamagePlayer(source.Owner, dif);
			}
		}else{ //Attack vs Attack
			if(from.Attack > to.Attack){
				int dif = from.Attack - to.Attack;
				target.Card = null;
				
				Game.DamagePlayer(target.Owner, dif);
			}else if(from.Attack == to.Attack){
				source.Card = null;
				target.Card = null;
			}
		}
	}
}
