package com.bence.yugioh.player;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import com.bence.yugioh.Art;
import com.bence.yugioh.cards.Card;
import com.bence.yugioh.slots.CardSlot;
import com.bence.yugioh.slots.CardSlotHand;
import com.bence.yugioh.utils.Rect;

/**
 * Kezeli a j�t�kos kez�ben l�v� k�rty�kat �s a hozz�juk tartoz� slot-okat.
 * @author Bence
 *
 */
public class HandCardManager {
	private Player _player;
	private ArrayList<CardSlotHand> _handSlots;
	
	//Jobbra, balra ny�l helye.
	private Rect _leftArrow;
	private Rect _rightArrow;
	
	//Slot elcs�sztat�s.
	private int _slotOffset = 0;
	
	public HandCardManager(Player p, ArrayList<CardSlotHand> slots){
		_player = p;
		_handSlots = slots;
		
		_rightArrow = new Rect(slots.get(slots.size() - 1).X  + CardSlot.Width, slots.get(slots.size() - 1).Y + CardSlot.Height / 2 - CardSlot.Height / 4, CardSlot.Width / 2, CardSlot.Height / 2);
		_leftArrow = new Rect(slots.get(0).X - CardSlot.Width / 2, slots.get(0).Y + CardSlot.Height / 2 - CardSlot.Height / 4, CardSlot.Width / 2, CardSlot.Height / 2);
	}
	
	/**
	 * Vissza�ll�tja az elcs�sztat�st.
	 */
	public void ResetOffset(){
		_slotOffset = 0;
	}
	
	/*
	 * Plusz inform�ci�kat jelen�t meg a j�t�kos kez�ben l�v� k�rty�khoz.
	 */
	public void Draw(Graphics g){
		if(_player.Hand.size() > 5) { //Ha t�bb mint 5 k�rtya van
			if(CanScrollRight()){ //Lehet jobbra cs�sztatni?
				g.drawImage(Art.ArrowRight, _rightArrow.X, _rightArrow.Y, _rightArrow.Width, _rightArrow.Height, null);
			}
			
			if(CanScrollLeft()){ //Lehet balra cs�sztatni?
				g.drawImage(Art.ArrowLeft, _leftArrow.X, _leftArrow.Y, _leftArrow.Width, _leftArrow.Height, null);
			}
			
			//Megjelen�tek egy kis cs�kot mely mutatja, hogy mennyi k�rtya van �s abb�l melyik 5 j�tszik.
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
	
	/**
	 * Visszaadja, hogy lehet-e jobbra cs�sztatni.
	 */
	private boolean CanScrollRight(){
		return (_slotOffset + 1 <= _player.Hand.size() - _handSlots.size());
	}
	
	/**
	 * Visszaadja, hogy lehet-e balra cs�sztatni.
	 */
	private boolean CanScrollLeft(){
		return (_slotOffset > 0);
	}
	
	/**
	 * Kattint�s esem�ny.
	 */
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
	
	/**
	 * Az elcs�sztat�s alapj�n a k�z slot-okba rak k�rty�kat.
	 */
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
	
	/**
	 * �j k�rtya ker�lt a j�t�kos kez�be.
	 */
	public void OnCardAdded(){
		Card newCard = _player.Hand.get(_player.Hand.size() - 1); //Az �j k�rtya
		
		int emptySlot = -1;
		for(int x = 0;x<_handSlots.size();x++) { //Keresek egy �res helyet
			if(_handSlots.get(x).Card == null) {
				emptySlot = x;
				break;
			}
		}

		if(emptySlot == -1) { //Ha nem volt, akkor mindent balra cs�sztatok �s a v�g�re rakom az �j k�rty�t
			for(int x = 1;x<_handSlots.size();x++){
				_handSlots.get(x-1).Card = _handSlots.get(x).Card;
			}
			_handSlots.get(_handSlots.size() - 1).Card = newCard;
			
			_slotOffset = _player.Hand.size() - _handSlots.size();
			if(_slotOffset < 0)
				_slotOffset = 0;
		} else { //Ha volt akkor az �res helyre rakom az �j k�rty�t
			_handSlots.get(emptySlot).Card = newCard;
		}
	}
	
	/**
	 * Egy k�rtya elt�vol�t�sra ker�lt a j�t�kos kez�b�l.
	 */
	public void OnCardRemoved(){		
		ReassignCards();
	}
}
