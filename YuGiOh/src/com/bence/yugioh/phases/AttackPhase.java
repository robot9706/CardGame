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
						((CardSlotPlayfield)slot).Used = true;
					}
					
					Game.RedrawFrame();
				}
			}
		}
	}
	
	private void DoAttack(CardSlot source, CardSlot target){
		GetAttackResult(source, target, Game).DoActions();
	}
	
	public static AttackResult GetAttackResult(CardSlot source, CardSlot target, YuGiOhGame game){
		CardMonster from = (CardMonster)source.Card;
		CardMonster to = (CardMonster)target.Card;
		
		int fromATK = from.Attack + game.GetAdditionalATK(source);
		int toATK = to.Attack + game.GetAdditionalATK(target); 
		int toDEF = to.Defense + game.GetAdditionalDEF(target); 
		
		AttackResult res = new AttackResult(game, source, target);
		
		if(target.Card.IsRotated) { //Attack vs Defense
			if(toDEF < fromATK){
				res.WinnerSlot = source;
				res.TargetDestroyed = true;
				res.LoserDamage = 0;
			}else if(toDEF > fromATK){
				res.WinnerSlot = target;
				res.LoserDamage = toDEF - fromATK;
				res.SourceDestroyed = false;
				res.TargetDestroyed = false;
			}else if(toDEF == fromATK){
				res.WinnerSlot = null;
				res.LoserDamage = 0;
				res.TargetDestroyed = false;
				res.SourceDestroyed = false;
			}
		}else{ //Attack vs Attack
			if(fromATK > toATK){
				res.WinnerSlot = source;
				res.TargetDestroyed = true;
				res.LoserDamage = fromATK - toATK;
			}else if(fromATK == toATK){
				res.WinnerSlot = null;
				res.TargetDestroyed = true;
				res.SourceDestroyed = true;
			}else if(fromATK < toATK){
				res.WinnerSlot = target;
				res.LoserDamage = toATK - fromATK;
				res.SourceDestroyed = true;
			}
		}
		
		/*if(target.Card.IsRotated){ //Attack vs Defense
			if(fromATK > toDEF){
				//target.Card = null;
				res.WinnerSlot = source;
				res.TargetDestroyed = true;
			}else if(fromATK < toDEF){
				int dif = toDEF - fromATK;
				//game.DamagePlayer(source.Owner, dif);
				
				res.WinnerSlot = target;
				res.LoserDamage = dif;
			}
		}else{ //Attack vs Attack
			if(fromATK > toATK){
				int dif = fromATK - toATK;
				//target.Card = null;
				
				//game.DamagePlayer(target.Owner, dif);
				
				res.WinnerSlot = source;
				res.LoserDamage = dif;
				res.TargetDestroyed = true;
			}else if(fromATK == toATK){
				//source.Card = null;
				//target.Card = null;
				res.SourceDestroyed = true;
				res.TargetDestroyed = true;
			}else{
				//source.Card = null;
				res.WinnerSlot = target;
				res.SourceDestroyed = true;
			}
		}*/
		
		return res;
	}
}
