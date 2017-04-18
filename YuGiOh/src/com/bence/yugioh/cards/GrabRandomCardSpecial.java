package com.bence.yugioh.cards;

import java.util.Random;

import com.bence.yugioh.YuGiOhGame;
import com.bence.yugioh.player.Player;
import com.bence.yugioh.slots.CardSlot;
import com.bence.yugioh.utils.Texts;

/**
 * Szronykartya kepesseg, mely elvesz egy veletlenszeru kartyat az ellenseg kezebol.
 * @author Bence
 *
 */
public class GrabRandomCardSpecial extends MonsterOnPlaceSpecial {
	public String GetDescription(){
		return Texts.GrabRandomCardText;
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
