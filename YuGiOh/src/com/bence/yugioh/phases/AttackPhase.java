package com.bence.yugioh.phases;

import com.bence.yugioh.YuGiOhGame;
import com.bence.yugioh.cards.Card;
import com.bence.yugioh.cards.CardMonster;
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
	
	public void OnPhaseActivated(){
		Game.ResetSlotUsedStates();
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
			if(((CardSlotPlayfield)slot).Used)
				return;
			
			Card card = slot.Card;
			
			if(_isAttacking){
				if(slot.Owner == Game.ComputerPlayer && card != null){
					_isAttacking = false;
					Game.ResetSlotHighlight();
					
					DoAttack(_attackSource, slot);
					
					((CardSlotPlayfield)_attackSource).Used = true;
					
					Game.RedrawFrame();
				}
			}else{
				if(slot.Owner == Game.HumanPlayer && card instanceof CardMonster && !card.IsRotated){
					if(Game.HasMonstersPlaced(Game.ComputerPlayer)){
						_isAttacking = true;
						_attackSource = slot;
						
						Game.SetAttackSlotHighlight(slot);
					}else{
						Game.DamagePlayer(Game.ComputerPlayer, ((CardMonster)card).Attack);
						slot.Card = null;
						((CardSlotPlayfield)slot).Used = true;
					}
					
					Game.RedrawFrame();
				}
			}
		}
	}
	
	private void DoAttack(CardSlot source, CardSlot target){
		CardMonster from = (CardMonster)source.Card;
		CardMonster to = (CardMonster)target.Card;
		
		int fromATK = from.Attack + Game.GetAdditionalATK(source);
		int fromDEF = from.Defense + Game.GetAdditionalDEF(source);
		int toATK = to.Attack + Game.GetAdditionalATK(target); 
		int toDEF = to.Defense + Game.GetAdditionalDEF(target); 
		
		if(target.Card.IsRotated){ //Attack vs Defense
			if(fromATK > toDEF){
				target.Card = null;
			}else if(fromATK < toDEF){
				int dif = toDEF - fromATK;
				Game.DamagePlayer(source.Owner, dif);
			}
		}else{ //Attack vs Attack
			if(fromATK > toATK){
				int dif = fromATK - toATK;
				target.Card = null;
				
				Game.DamagePlayer(target.Owner, dif);
			}else if(fromATK == toATK){
				source.Card = null;
				target.Card = null;
			}else{
				source.Card = null;
			}
		}
	}
}
