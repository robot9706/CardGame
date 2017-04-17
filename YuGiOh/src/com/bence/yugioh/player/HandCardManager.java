package com.bence.yugioh.player;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import com.bence.yugioh.Art;
import com.bence.yugioh.cards.Card;
import com.bence.yugioh.slots.CardSlot;
import com.bence.yugioh.slots.CardSlotHand;
import com.bence.yugioh.utils.Rect;

public class HandCardManager {
	private Player _player;
	private ArrayList<CardSlotHand> _handSlots;
	
	private Rect _leftArrow;
	private Rect _rightArrow;
	
	private int _slotOffset = 0;
	
	public HandCardManager(Player p, ArrayList<CardSlotHand> slots){
		_player = p;
		_handSlots = slots;
		
		_rightArrow = new Rect(slots.get(slots.size() - 1).X  + CardSlot.Width, slots.get(slots.size() - 1).Y + CardSlot.Height / 2 - CardSlot.Height / 4, CardSlot.Width / 2, CardSlot.Height / 2);
		_leftArrow = new Rect(slots.get(0).X - CardSlot.Width / 2, slots.get(0).Y + CardSlot.Height / 2 - CardSlot.Height / 4, CardSlot.Width / 2, CardSlot.Height / 2);
	}
	
	public void ResetOffset(){
		_slotOffset = 0;
	}
	
	public void Draw(Graphics g){
		if(_player.Hand.size() > 5) {
			if(CanScrollRight()){
				g.drawImage(Art.ArrowRight, _rightArrow.X, _rightArrow.Y, _rightArrow.Width, _rightArrow.Height, null);
			}
			
			if(CanScrollLeft()){ 
				g.drawImage(Art.ArrowLeft, _leftArrow.X, _leftArrow.Y, _leftArrow.Width, _leftArrow.Height, null);
			}
			
			int barX = _handSlots.get(0).X;
			int barY = _handSlots.get(0).Y + CardSlot.Height + 4;
			int barWidth = (_handSlots.get(_handSlots.size() - 1).X + CardSlot.Width) - _handSlots.get(0).X;
			int barHeight = 5;
			
			g.setColor(new Color(0, 0, 0, 65));
			g.fillRoundRect(barX, barY, barWidth, barHeight, 2, 2);
			
			int scrollWidth = (int)(barWidth * (5.0f / _player.Hand.size()));
			
			g.setColor(new Color(255, 255, 255, 100));
			g.fillRoundRect((int)(barX + (barWidth - scrollWidth) * ((float)_slotOffset / (_player.Hand.size() - _handSlots.size()))), barY, scrollWidth, barHeight, 2, 2);
		}
	}
	
	private boolean CanScrollRight(){
		return (_slotOffset + 1 <= _player.Hand.size() - _handSlots.size());
	}
	
	private boolean CanScrollLeft(){
		return (_slotOffset > 0);
	}
	
	public boolean OnClick(int x, int y){
		if(_rightArrow.IsPointInRect(x, y)){
			if(CanScrollRight()){
				_slotOffset++;
				
				ReassignCards();
				
				return true;
			}
		}
		
		if(_leftArrow.IsPointInRect(x, y)){
			if(CanScrollLeft()){
				_slotOffset--;
			
				ReassignCards();
				
				return true;
			}
		}
		
		return false;
	}
	
	private void ReassignCards(){
		for(int x = 0;x<_handSlots.size();x++){
			int cardIndex = x + _slotOffset;
			
			if(cardIndex < _player.Hand.size()){
				_handSlots.get(x).Card  = _player.Hand.get(cardIndex);
			}else{
				_handSlots.get(x).Card = null;
			}
		}
	}
	
	public void OnCardAdded(){
		Card newCard = _player.Hand.get(_player.Hand.size() - 1);
		
		int emptySlot = -1;
		for(int x = 0;x<_handSlots.size();x++) {
			if(_handSlots.get(x).Card == null) {
				emptySlot = x;
				break;
			}
		}

		if(emptySlot == -1) {
			for(int x = 1;x<_handSlots.size();x++){
				_handSlots.get(x-1).Card = _handSlots.get(x).Card;
			}
			_handSlots.get(_handSlots.size() - 1).Card = newCard;
			
			_slotOffset = _player.Hand.size() - _handSlots.size();
			if(_slotOffset < 0)
				_slotOffset = 0;
		} else {
			_handSlots.get(emptySlot).Card = newCard;
		}
	}
	
	public void OnCardRemoved(){		
		ReassignCards();
	}
}
