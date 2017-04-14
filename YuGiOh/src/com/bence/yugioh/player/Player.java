package com.bence.yugioh.player;

import java.util.ArrayList;

import com.bence.yugioh.Card;

public class Player {
	public int Health;
	
	public ArrayList<Card> Hand;
	
	public HandCardManager HandCardManager;
		
	public Player(){
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
}
