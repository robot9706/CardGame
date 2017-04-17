package com.bence.yugioh.cards;

import java.util.Random;

import com.bence.yugioh.YuGiOhGame;
import com.bence.yugioh.player.Player;
import com.bence.yugioh.slots.CardSlot;

public class GrabRandomCardSpecial extends MonsterOnPlaceSpecial {
	public String GetDescription(){
		return "Az ellenf�l kez�b�l kapsz egy v�letlenszer� k�rty�t. A k�rtya lehelyez�sekor aktiv�l�dik";
	}
	
	public void OnActivate(YuGiOhGame game, CardSlot placeSlot) {
		Player other = game.GetOtherPlayer(placeSlot.Owner);
		if (other.Hand.size() > 0){
			Random rnd = new Random();
			Card rm = other.Hand.get(rnd.nextInt(other.Hand.size()));
			other.RemoveCardFromHand(rm);
			placeSlot.Owner.AddCardToHand(rm);
		}
	}
}
