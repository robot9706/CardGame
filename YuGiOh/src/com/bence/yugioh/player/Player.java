package com.bence.yugioh.player;

import java.util.ArrayList;

import com.bence.yugioh.YuGiOhGame;
import com.bence.yugioh.cards.Card;

/**
 * Egy játékos.
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
	 * Tömbnyi kártyát helyez a játékos kezébe.
	 */
	public void AddArrayOfCards(ArrayList<Card> cards){
		for(int x = 0;x<cards.size();x++){
			AddCardToHand(cards.get(x));
		}
	}
	
	/**
	 * Egy kártyát helyez a játékos kezébe. 
	 */
	public void AddCardToHand(Card c){
		Hand.add(c);
		
		HandCardManager.OnCardAdded();
	}
	
	/**
	 * Eltávolít egy kártyát a játékos kezébõl.
	 */
	public void RemoveCardFromHand(Card c){
		Hand.remove(c);
		
		HandCardManager.OnCardRemoved();
	}
	
	/**
	 * Elõkészíti a paklit, ha ez egy kezdõpakli, akkor kiveszi belõle az elsõ 3 kártyát.
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
	 * Kivesz egy kártyát a pakliból (ha van).
	 */
	public void GrabCardFromDeck(){
		if(Deck.size() > 0){
			Card c = Deck.get(Deck.size() - 1);
			Deck.remove(c);
			AddCardToHand(c);
		}
	}
}
