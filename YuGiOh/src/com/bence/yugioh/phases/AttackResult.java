package com.bence.yugioh.phases;

import com.bence.yugioh.YuGiOhGame;
import com.bence.yugioh.slots.CardSlot;

/**
 * Egy tamadas eredmenyet tarolja.
 * @author Bence
 *
 */
public class AttackResult {
	public YuGiOhGame Game;
	
	public CardSlot Source = null; //A forras slot
	public CardSlot Target  = null; //A cel slot
	
	public CardSlot WinnerSlot = null; //A nyertes slot
	public int LoserDamage = 0; //A vesztes ennyi eletet veszit
	
	public boolean SourceDestroyed = false; //A forras elpusztul?
	public boolean TargetDestroyed = false; //A cel elpusztul?
	
	public AttackResult(YuGiOhGame game, CardSlot source, CardSlot target){
		Game = game;
		
		Source = source;
		Target = target;
	}
	
	/**
	 * Elvegzi a tamadas kovetkezmenyeit.
	 */
	public void DoActions(){
		if(SourceDestroyed){
			Source.Card = null;
		}
		
		if(TargetDestroyed){
			Target.Card = null;
		}
		
		if(LoserDamage > 0 && WinnerSlot != null){
			Game.DamagePlayer(Game.GetOtherPlayer(WinnerSlot.Owner), LoserDamage);
		}
	}
}
