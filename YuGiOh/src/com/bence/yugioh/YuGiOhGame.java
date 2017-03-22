package com.bence.yugioh;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;

public class YuGiOhGame {
	private Player _playerA; //Top
	private Player _playerB; //Bottom - human player
	
	private ArrayList<CardSlot> _slots;
	
	public YuGiOhGame(int w, int h){
		_slots = new ArrayList<CardSlot>();
		
		_playerA = new Player();
		_playerB = new Player();
		
		//
		int sideHeight = h / 2;
		float cardSize = ((sideHeight / 3) * 0.95f);
		float cardRatio = (float)Art.CardSlot_Hand.getWidth(null) / (float)Art.CardSlot_Hand.getHeight(null);
		
		CardSlot.Width = (int)(cardSize * cardRatio);
		CardSlot.Height = (int)cardSize;
		
		//
		int leftColumn = w / 5;
		float paddingY = sideHeight / 3;
		
		_slots.add(new CardSlotStack(_playerA, GetCardCenterX(leftColumn / 2), (int)(paddingY / 2.0f)));
		_slots.add(new CardSlotStack(_playerB, GetCardCenterX(leftColumn / 2), (int)(h - CardSlot.Height - paddingY / 2.0f)));
		
		_slots.get(0).Card = new Card();
		_slots.get(1).Card = new Card();
		
		//
		int rightColumn = w - leftColumn;
			
		float paddingSize = (CardSlot.Width * 1.05f);
		float fieldStart = rightColumn - paddingSize * 5.0f;

		float rightX = leftColumn + (fieldStart / 2.0f);
		for(int x = 0;x<5;x++){
			_slots.add(new CardSlotHand(_playerA, GetCardCenterX((int)(rightX + paddingSize * x)), 0));
			_slots.add(new CardSlotPlayfield(_playerA, GetCardCenterX((int)(rightX + paddingSize * x)), (int)paddingY, false));
			_slots.add(new CardSlotPlayfield(_playerA, GetCardCenterX((int)(rightX + paddingSize * x)), (int)(paddingY * 2.0f), true));
			
			_slots.add(new CardSlotHand(_playerB, GetCardCenterX((int)(rightX + paddingSize * x)), (int)(h - paddingY)));
			_slots.add(new CardSlotPlayfield(_playerB, GetCardCenterX((int)(rightX + paddingSize * x)), (int)(h - paddingY * 2.0f), false));
			_slots.add(new CardSlotPlayfield(_playerB, GetCardCenterX((int)(rightX + paddingSize * x)), (int)(h - paddingY * 3.0f), true));
		}
	}
	
	private int GetCardCenterX(int x){
		return x - (CardSlot.Width / 2);
	}
	
	public void Draw(Graphics g){
		for(CardSlot s : _slots){
			s.Draw(g, _playerB);
		}
		
		g.setColor(Color.black);;
		g.setFont(new Font("Arial", Font.BOLD, 28));
		g.drawString("HÖPÖ: " + String.valueOf(_playerA.Health), 0, 400);
		g.drawString("HÖPÖ: " + String.valueOf(_playerB.Health), 0, 500);
	}
}
