package com.bence.yugioh;

import java.awt.Graphics;
import java.util.ArrayList;

public class HandCardManager {
	private Player _player;
	private ArrayList<CardSlotHand> _handSlots;
	
	private Rect _leftArrow;
	private Rect _rightArrow;
	
	public HandCardManager(Player p, ArrayList<CardSlotHand> slots){
		_player = p;
		_handSlots = slots;
		
		_rightArrow = new Rect(slots.get(slots.size() - 1).X  + CardSlot.Width, slots.get(slots.size() - 1).Y + CardSlot.Height / 2 - CardSlot.Height / 4, CardSlot.Width / 2, CardSlot.Height / 2);
		_leftArrow = new Rect(slots.get(0).X - CardSlot.Width / 2, slots.get(0).Y + CardSlot.Height / 2 - CardSlot.Height / 4, CardSlot.Width / 2, CardSlot.Height / 2);
	}
	
	public void Draw(Graphics g){
		if(_player.Hand.size() > 5){
			g.drawImage(Art.ArrowRight, _rightArrow.X, _rightArrow.Y, _rightArrow.Width, _rightArrow.Height, null);
			g.drawImage(Art.ArrowLeft, _leftArrow.X, _leftArrow.Y, _leftArrow.Width, _leftArrow.Height, null);
		}
	}
	
	public void OnChange(){
		Card newCard = _player.Hand.get(_player.Hand.size() - 1);
		
		int emptySlot = -1;
		for(int x = 0;x<_handSlots.size();x++){
			if(_handSlots.get(x).Card == null){
				emptySlot = x;
				break;
			}
		}

		if(emptySlot == -1){
			for(int x = 1;x<_handSlots.size();x++){
				_handSlots.get(x-1).Card = _handSlots.get(x).Card;
			}
			_handSlots.get(_handSlots.size() - 1).Card = newCard;
		}else{
			_handSlots.get(emptySlot).Card = newCard;
		}
	}
}
