package com.bence.yugioh;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.ArrayList;

public class YuGiOhGame {
	private Player _playerA; //Top
	private Player _playerB; //Bottom - human player
	
	private ArrayList<CardSlot> _slots;
	
	private GameFrame _frame;
	
	private Point2 _playerAHP;
	private Point2 _playerBHP;
	
	private Point2 _lastMousePosition;
	
	private boolean _doInspectCard;
	private Card _cardToInspect;
	private Rect _cardInspector;
	
	private boolean _paused = false;
	
	public YuGiOhGame(int w, int h, GameFrame f){
		_frame = f;

		_slots = new ArrayList<CardSlot>();
		
		_lastMousePosition = new Point2(0, 0);
		
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
		
		
		float right2X = w - (w / 4);
		float right2W = w / 4;
		
		float iwidth = right2W * 0.6f;
		float iheight = (((float)CardSlot.Height / (float)CardSlot.Width) * iwidth);
		_cardInspector = new Rect((int)(right2X + (right2W / 2.0f) - (iwidth / 2.0f)), (int)(h - iheight * 1.5f), (int)iwidth, (int)iheight);
		_doInspectCard = false;
	}
	
	private int GetCardCenterX(int x){
		return x - (CardSlot.Width / 2);
	}
	
	public void Draw(Graphics g){
		g.setColor(Color.black);
		g.setFont(new Font("Arial", Font.BOLD, 16));
		
		for(CardSlot s : _slots){
			s.Draw(g, _playerB);
		}
		
		_playerB.HandCardManager.Draw(g);
		
		g.setColor(Color.black);
		g.setFont(new Font("Arial", Font.BOLD, 28));
		
		g.drawString("Életerõ: " + String.valueOf(_playerA.Health), _playerAHP.X, _playerAHP.Y);
		g.drawString("Életerõ: " + String.valueOf(_playerB.Health), _playerBHP.X, _playerBHP.Y);
		
		if(_doInspectCard){
			g.drawImage(Art.CardFront_Temp, _cardInspector.X, _cardInspector.Y, _cardInspector.Width, _cardInspector.Height, null);
			
			FontMetrics m = g.getFontMetrics();
			int w = m.stringWidth(_cardToInspect.Name);
			
			g.drawString(_cardToInspect.Name, _cardInspector.X + (_cardInspector.Width / 2) - (w / 2), _cardInspector.Y + _cardInspector.Height + (int)(m.getHeight() * 1.05f));
		}
		
		if(_paused){
			g.setColor(new Color(0, 0, 0, 192));	
			g.fillRect(0, 0, _frame.getWidth(), _frame.getHeight());
		}
	}
	
	public void OnMouseMove(int x, int y){
		if(_lastMousePosition.X == x && _lastMousePosition.Y == y){
			return;
		}
		
		_lastMousePosition.X = x;
		_lastMousePosition.Y = y;
		
		CardSlot inspectedSlot = null;
		for(CardSlot s : _slots){	
			if(s.IsInBounds(_lastMousePosition)){
				inspectedSlot = s;
				break;
			}
		}
		
		if(_doInspectCard){
			if(inspectedSlot == null){
				_doInspectCard = false;
				_cardToInspect = null;
				_frame.Redraw();
			}
		}else{
			if(inspectedSlot != null){
				_cardToInspect = inspectedSlot.Card;
				_doInspectCard = (inspectedSlot.Card != null);
				_frame.Redraw();
			}
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
			
			if(_playerB.HandCardManager.OnClick(x, y)){
				change = true;
			}
			
			if(change){
				_frame.Redraw();
			}
		}
	}
}
