package com.bence.yugioh.phases;

import com.bence.yugioh.YuGiOhGame;
import com.bence.yugioh.slots.CardSlot;

public class AttackResult {
	public YuGiOhGame Game;
	
	public CardSlot Source = null;
	public CardSlot Target  = null;
	
	public CardSlot WinnerSlot = null;
	public int LoserDamage = 0;
	
	public boolean SourceDestroyed = false;
	public boolean TargetDestroyed = false;
	
	public AttackResult(YuGiOhGame game, CardSlot source, CardSlot target){
		Game = game;
		
		Source = source;
		Target = target;
	}
	
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
