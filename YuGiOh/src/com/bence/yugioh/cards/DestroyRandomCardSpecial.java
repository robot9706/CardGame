package com.bence.yugioh.cards;

import java.util.ArrayList;
import java.util.Random;

import com.bence.yugioh.YuGiOhGame;
import com.bence.yugioh.slots.CardSlot;

/**
 * Szörnykártya képesség, mely elpusztít egy véletlenszerû ellenséges kártyát.
 * @author Bence
 *
 */
public class DestroyRandomCardSpecial extends MonsterOnPlaceSpecial {
	public String GetDescription(){
		return "Az ellenfél egy véletlenszerû kártyája megsemmisül. A kártya lehelyezésekor aktiválódik";
	}
	
	public void OnActivate(YuGiOhGame game, CardSlot placeSlot) {
		ArrayList<CardSlot> slots = game.GetPlayerUsedSlots(game.GetOtherPlayer(placeSlot.Owner));
		if(slots.size() > 0){
			Random rnd = new Random();
			slots.get(rnd.nextInt(slots.size())).Card = null;
		}
	}
}
