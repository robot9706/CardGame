package com.bence.yugioh.cards;

import java.util.ArrayList;
import java.util.Random;

import com.bence.yugioh.YuGiOhGame;
import com.bence.yugioh.slots.CardSlot;

/**
 * Sz�rnyk�rtya k�pess�g, mely elpuszt�t egy v�letlenszer� ellens�ges k�rty�t.
 * @author Bence
 *
 */
public class DestroyRandomCardSpecial extends MonsterOnPlaceSpecial {
	public String GetDescription(){
		return "Az ellenf�l egy v�letlenszer� k�rty�ja megsemmis�l. A k�rtya lehelyez�sekor aktiv�l�dik";
	}
	
	public void OnActivate(YuGiOhGame game, CardSlot placeSlot) {
		ArrayList<CardSlot> slots = game.GetPlayerUsedSlots(game.GetOtherPlayer(placeSlot.Owner));
		if(slots.size() > 0){
			Random rnd = new Random();
			slots.get(rnd.nextInt(slots.size())).Card = null;
		}
	}
}
