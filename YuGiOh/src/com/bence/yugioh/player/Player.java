package com.bence.yugioh.player;

import java.util.ArrayList;

import com.bence.yugioh.YuGiOhGame;
import com.bence.yugioh.cards.Card;

/**
 * Egy jatekos.
 * @author Bence
 *
 */
public class Player {
	public int Health;
	
	public ArrayList<Card> Deck;
	public ArrayList<Card> Hand;
	
	public HandCardManager HandCardManager;
		
	public YuGiOhGame Game;
	
	public Player(YuGiOhGame owner){
		Game = owner;
		
		Health = 4000;
		
		Hand = new ArrayList<Card>();
	}
	
	/**
	 * Tombnyi kartyat helyez a jatekos kezebe.
	 */
	public void SetHandArrayOfCards(ArrayList<Card> cards){
		Hand.clear();
		
		for(int x = 0;x<cards.size();x++){
			AddCardToHand(cards.get(x));
		}
	}
	
	/**
	 * Egy kartyat helyez a jatekos kezebe. 
	 */
	public void AddCardToHand(Card c){
		Hand.add(c);
		
		HandCardManager.OnCardAdded();
	}
	
	/**
	 * Eltavolit egy kartyat a jatekos kezebol.
	 */
	public void RemoveCardFromHand(Card c){
		Hand.remove(c);
		
		HandCardManager.OnCardRemoved();
	}
	
	/**
	 * Elokesziti a paklit, ha ez egy kezdopakli, akkor kiveszi belole az elso 3 kartyat.
	 */
	public void InitCards(ArrayList<Card> deck, boolean isStartingDeck){
		Deck = deck;
		
		if(isStartingDeck){
			Hand.clear();
			
			for(int x = 0;x<3;x++){
				AddCardToHand(Deck.get(Deck.size() - 1));
				Deck.remove(Deck.size() - 1);
			}
		}
	}
	
	/**
	 * Kivesz egy kartyat a paklibol (ha van).
	 */
	public void GrabCardFromDeck(){
		if(Deck.size() > 0){
			Card c = Deck.get(Deck.size() - 1);
			Deck.remove(c);
			AddCardToHand(c);
		}
	}
}
