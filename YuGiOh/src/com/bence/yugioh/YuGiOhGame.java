package com.bence.yugioh;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;

public class YuGiOhGame {
	private Player _playerA; //Top
	private Player _playerB; //Bottom - human player
	
	private ArrayList<CardSlot> _slots;
	
	private GameFrame _frame;
	
	private Point2 _playerAHP;
	private Point2 _playerBHP;
	
	private boolean _paused = false;
	
	public YuGiOhGame(int w, int h, GameFrame f){
		_frame = f;

		_slots = new ArrayList<CardSlot>();
		
		_playerA = new Player();
		_playerB = new Player();
		
		
		int sideHeight = h / 2;
		float cardSize = ((sideHeight / 3) * 0.90f);
		float cardRatio = (float)Art.CardSlot_Hand.getWidth(null) / (float)Art.CardSlot_Hand.getHeight(null);
		
		CardSlot.Width = (int)(cardSize * cardRatio);
		CardSlot.Height = (int)(cardSize);
		
		
		int leftColumn = (w / 4);
		float paddingY = sideHeight / 3;
		
		_slots.add(new CardSlotStack(_playerA, GetCardCenterX(leftColumn / 2), (int)(paddingY / 2.0f)));
		_slots.add(new CardSlotStack(_playerB, GetCardCenterX(leftColumn / 2), (int)(h - CardSlot.Height - paddingY / 2.0f)));
		
		_playerAHP = new Point2(30, (int)(paddingY * 2.0f));
		_playerBHP = new Point2(30, (int)(h - paddingY * 2.0f));
		
		
		int rightColumn = (w / 4) * 2;
			
		float paddingSize = rightColumn / 5.0f; 

		ArrayList<CardSlotHand> playerAHand = new ArrayList<CardSlotHand>();
		ArrayList<CardSlotHand> playerBHand = new ArrayList<CardSlotHand>();
		
		CardSlotHand temp = null;
		
		float rightX = leftColumn + (CardSlot.Width / 2.0f);
		for(int x = 0;x<5;x++){
			temp = new CardSlotHand(_playerA, GetCardCenterX((int)(rightX + paddingSize * x)), 0);
			playerAHand.add(temp);
			
			_slots.add(temp);
			_slots.add(new CardSlotPlayfield(_playerA, GetCardCenterX((int)(rightX + paddingSize * x)), (int)paddingY, false));
			_slots.add(new CardSlotPlayfield(_playerA, GetCardCenterX((int)(rightX + paddingSize * x)), (int)(paddingY * 1.915f), true));
			
			temp = new CardSlotHand(_playerB, GetCardCenterX((int)(rightX + paddingSize * x)), (int)(h - paddingY));
			playerBHand.add(temp);
			
			_slots.add(temp);
			_slots.add(new CardSlotPlayfield(_playerB, GetCardCenterX((int)(rightX + paddingSize * x)), (int)(h - paddingY * 2.0f), false));
			_slots.add(new CardSlotPlayfield(_playerB, GetCardCenterX((int)(rightX + paddingSize * x)), (int)(h - paddingY * 2.915f), true));
		}
		
		_playerA.HandCardManager = new HandCardManager(_playerA, playerAHand);
		_playerB.HandCardManager = new HandCardManager(_playerB, playerBHand);
		
		_playerA.AddCardToHand(new Card());
		_playerA.AddCardToHand(new Card());
	}
	
	private int GetCardCenterX(int x){
		return x - (CardSlot.Width / 2);
	}
	
	public void Draw(Graphics g){
		for(CardSlot s : _slots){
			s.Draw(g, _playerB);
		}
		
		_playerB.HandCardManager.Draw(g);
		
		g.setColor(Color.black);
		g.setFont(new Font("Arial", Font.BOLD, 28));
		
		g.drawString("Életerõ: " + String.valueOf(_playerA.Health), _playerAHP.X, _playerAHP.Y);
		g.drawString("Életerõ: " + String.valueOf(_playerB.Health), _playerBHP.X, _playerBHP.Y);
		
		if(_paused){
			g.setColor(new Color(0, 0, 0, 192));	
			g.fillRect(0, 0, _frame.getWidth(), _frame.getHeight());
		}
	}
	
	public void OnMouseClick(int x, int y){
		if(_paused){
			
		}else{
			boolean change = false;
			
			for(CardSlot s : _slots){
				if(s.IsInBounds(x, y)){
					s.OnClick(_playerB);
					
					change = true;
					break;
				}
			}
			
			if(change){
				_frame.Redraw();
			}
		}
	}
}
