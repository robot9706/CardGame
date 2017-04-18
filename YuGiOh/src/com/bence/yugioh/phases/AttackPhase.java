package com.bence.yugioh.phases;

import com.bence.yugioh.YuGiOhGame;
import com.bence.yugioh.cards.Card;
import com.bence.yugioh.cards.CardMonster;
import com.bence.yugioh.slots.CardSlot;
import com.bence.yugioh.slots.CardSlotPlayfield;

/**
 * Támadás fázis.
 * @author Bence
 *
 */
public class AttackPhase extends GamePhase {
	private boolean _isAttacking = false; //Támadás állapot
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
		if(slot == null){ //Ha nem slot-ra kattintok visszavonom az eseményeket
			if(_isAttacking){
				_isAttacking = false;
				Game.ResetSlotHighlight();
				Game.RedrawFrame();
			}
			return;
		}
		
		if(slot instanceof CardSlotPlayfield){ //Ha soltra kattintok megnézem, hogy lehet-e
			if(((CardSlotPlayfield)slot).Used)
				return;
			
			Card card = slot.Card;
			
			if(_isAttacking){ //Ha már támadok, megnézem, hogy ezt a slot-ot lehet-e támadni
				if(slot.Owner == Game.ComputerPlayer && card != null){
					_isAttacking = false;
					Game.ResetSlotHighlight();
					
					DoAttack(_attackSource, slot);
					
					((CardSlotPlayfield)_attackSource).Used = true;
					
					Game.RedrawFrame();
				}
			}else{
				if(slot.Owner == Game.HumanPlayer && card instanceof CardMonster && !card.IsRotated){ //Ha nem támadok, megnézem, hogy ezzel lehet-e támadni
					if(Game.HasMonstersPlaced(Game.ComputerPlayer)){
						_isAttacking = true;
						_attackSource = slot;
						
						Game.SetAttackSlotHighlight(slot);
					}else{ //Ha nincs mit támadni, akkor az ellenfelet támadom közvetlenül
						Game.DamagePlayer(Game.ComputerPlayer, ((CardMonster)card).Attack);
						((CardSlotPlayfield)slot).Used = true;
					}
					
					Game.RedrawFrame();
				}
			}
		}
	}
	
	/**
	 * Végrehajt egy támadást.
	 */
	private void DoAttack(CardSlot source, CardSlot target){
		GetAttackResult(source, target, Game).DoActions();
	}
	
	/**
	 * Kiszámolja egy támadás eredményét-
	 */
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
		
		return res;
	}
}
