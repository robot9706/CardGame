package com.bence.yugioh;

import java.util.ArrayList;

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
		
		HandCardManager.OnChange();
	}
}
