package com.bence.yugioh.player;

import java.util.ArrayList;

import com.bence.yugioh.YuGiOhGame;
import com.bence.yugioh.cards.Card;

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
	
	public void AddCardToHand(Card c){
		Hand.add(c);
		
		HandCardManager.OnCardAdded();
	}
	
	public void RemoveCardFromHand(Card c){
		Hand.remove(c);
		
		HandCardManager.OnCardRemoved();
	}
	
	public void InitCards(ArrayList<Card> deck){
		Deck = deck;
		Hand.clear();
		
		for(int x = 0;x<3;x++){
			AddCardToHand(Deck.get(Deck.size() - 1));
			Deck.remove(Deck.size() - 1);
		}
	}
	
	public void GrabCardFromDeck(){
		if(Deck.size() > 0){
			Card c = Deck.get(Deck.size() - 1);
			Deck.remove(c);
			AddCardToHand(c);
		}
	}
}
