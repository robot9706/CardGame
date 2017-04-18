package com.bence.yugioh.player;

import java.util.ArrayList;

import com.bence.yugioh.YuGiOhGame;
import com.bence.yugioh.cards.Card;

/**
 * Egy j�t�kos.
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
	 * T�mbnyi k�rty�t helyez a j�t�kos kez�be.
	 */
	public void AddArrayOfCards(ArrayList<Card> cards){
		for(int x = 0;x<cards.size();x++){
			AddCardToHand(cards.get(x));
		}
	}
	
	/**
	 * Egy k�rty�t helyez a j�t�kos kez�be. 
	 */
	public void AddCardToHand(Card c){
		Hand.add(c);
		
		HandCardManager.OnCardAdded();
	}
	
	/**
	 * Elt�vol�t egy k�rty�t a j�t�kos kez�b�l.
	 */
	public void RemoveCardFromHand(Card c){
		Hand.remove(c);
		
		HandCardManager.OnCardRemoved();
	}
	
	/**
	 * El�k�sz�ti a paklit, ha ez egy kezd�pakli, akkor kiveszi bel�le az els� 3 k�rty�t.
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
	 * Kivesz egy k�rty�t a paklib�l (ha van).
	 */
	public void GrabCardFromDeck(){
		if(Deck.size() > 0){
			Card c = Deck.get(Deck.size() - 1);
			Deck.remove(c);
			AddCardToHand(c);
		}
	}
}
