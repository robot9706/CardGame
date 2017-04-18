package com.bence.yugioh.phases;

import com.bence.yugioh.YuGiOhGame;
import com.bence.yugioh.slots.CardSlot;

/**
 * Egy t�mad�s eredm�ny�t t�rolja.
 * @author Bence
 *
 */
public class AttackResult {
	public YuGiOhGame Game;
	
	public CardSlot Source = null; //A forr�s slot
	public CardSlot Target  = null; //A c�l slot
	
	public CardSlot WinnerSlot = null; //A nyertes slot
	public int LoserDamage = 0; //A vesztes ennyi �letet vesz�t
	
	public boolean SourceDestroyed = false; //A forr�s elpusztul?
	public boolean TargetDestroyed = false; //A c�l elpusztul?
	
	public AttackResult(YuGiOhGame game, CardSlot source, CardSlot target){
		Game = game;
		
		Source = source;
		Target = target;
	}
	
	/**
	 * Elv�gzi a t�mad�s k�vetkezm�nyeit.
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
