package com.bence.yugioh;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Random;

import com.bence.yugioh.cards.AllCards;
import com.bence.yugioh.cards.Card;
import com.bence.yugioh.cards.CardMonster;
import com.bence.yugioh.phases.*;
import com.bence.yugioh.player.*;
import com.bence.yugioh.slots.*;
import com.bence.yugioh.utils.*;

public class YuGiOhGame {
	public Player ComputerPlayer; //Top
	public Player HumanPlayer; //Bottom - human player
	
	private ArrayList<CardSlot> _slots;
	
	private GameFrame _frame;
	
	private int _playerAHPY;
	private int _playerBHPY;
	
	private Point2 _lastMousePosition;
	private Point2 _backgroundSize;
	
	private boolean _doInspectCard;
	private boolean _isInspectedCardPlaced;
	private Card _cardToInspect;
	private Rect _cardInspector;
	
	private int _leftColumnCenterX;
	private int _rightColumnCenterX;
	
	private GamePhase _phase;
	
	private boolean _paused = false;
	
	public YuGiOhGame(int w, int h, GameFrame f){
		_frame = f;
		_backgroundSize = new Point2(w,h);
		
		_slots = new ArrayList<CardSlot>();
		
		_lastMousePosition = new Point2(0, 0);
		
		ComputerPlayer = new Player();
		HumanPlayer = new Player();
		
		
		int sideHeight = h / 2;
		float cardSize = ((sideHeight / 3) * 0.90f);
		float cardRatio = (float)Art.CardSlot_Hand.getWidth(null) / (float)Art.CardSlot_Hand.getHeight(null);
		
		CardSlot.Width = (int)(cardSize * cardRatio);
		CardSlot.Height = (int)(cardSize);
		
		
		int leftColumn = (w / 5);
		float paddingY = sideHeight / 3;
		
		_leftColumnCenterX = (int)(leftColumn / 2.0);
		
		_slots.add(new CardSlotStack(ComputerPlayer, GetCardCenterX(leftColumn / 2), (int)(paddingY / 2.0f)));
		_slots.add(new CardSlotStack(HumanPlayer, GetCardCenterX(leftColumn / 2), (int)(h - CardSlot.Height - paddingY / 2.0f)));
		
		_playerAHPY = (int)(paddingY * 2.0f);
		_playerBHPY = (int)(h - paddingY * 2.0f);
		

		
		float paddingSize = CardSlot.Height;//rightColumn / 5.0f; 

		ArrayList<CardSlotHand> playerAHand = new ArrayList<CardSlotHand>();
		ArrayList<CardSlotHand> playerBHand = new ArrayList<CardSlotHand>();
		
		CardSlotHand temp = null;
		
		float rightX = leftColumn + (CardSlot.Width / 2.0f);
		for(int x = 0;x<5;x++){
			temp = new CardSlotHand(ComputerPlayer, GetCardCenterX((int)(rightX + paddingSize * x)), 0);
			playerAHand.add(temp);
			
			_slots.add(temp);
			_slots.add(new CardSlotPlayfield(ComputerPlayer, GetCardCenterX((int)(rightX + paddingSize * x)), (int)paddingY, false));
			_slots.add(new CardSlotPlayfield(ComputerPlayer, GetCardCenterX((int)(rightX + paddingSize * x)), (int)(paddingY * 1.915f), true));
			
			temp = new CardSlotHand(HumanPlayer, GetCardCenterX((int)(rightX + paddingSize * x)), (int)(h - paddingY));
			playerBHand.add(temp);
			
			_slots.add(temp);
			_slots.add(new CardSlotPlayfield(HumanPlayer, GetCardCenterX((int)(rightX + paddingSize * x)), (int)(h - paddingY * 2.0f), false));
			_slots.add(new CardSlotPlayfield(HumanPlayer, GetCardCenterX((int)(rightX + paddingSize * x)), (int)(h - paddingY * 2.915f), true));
		}
		
		ComputerPlayer.HandCardManager = new HandCardManager(ComputerPlayer, playerAHand);
		HumanPlayer.HandCardManager = new HandCardManager(HumanPlayer, playerBHand);
		
		
		float right2X = w - (w / 5);
		float right2W = w / 5;
		
		_rightColumnCenterX = (int)(right2X + right2W / 2.0);
		
		float iwidth = right2W * 0.75f;
		float iheight = (((float)CardSlot.Height / (float)CardSlot.Width) * iwidth);
		_cardInspector = new Rect((int)(right2X + (right2W / 2.0f) - (iwidth / 2.0f)), (int)(h - iheight * 1.5f), (int)iwidth, (int)iheight);
		_doInspectCard = false;
		
		
		
		StartGame();
	}
	
	private void StartGame(){		
		int deckSize = 30;
		HumanPlayer.InitCards(AllCards.CreateDeck(deckSize));
		ComputerPlayer.InitCards(AllCards.CreateDeck(deckSize));
		
		SetPhase(new CardPickPhase(this));
	}
	
	public void SetPhase(GamePhase phase){
		_phase = phase;
		_phase.OnPhaseActivated();
		
		_frame.Redraw();
	}
	
	private int GetCardCenterX(int x){
		return x - (CardSlot.Width / 2);
	}
	
	public void Draw(Graphics g){
		g.drawImage(Art.EgyptBackground, 0, 0, _backgroundSize.X, _backgroundSize.Y, null);
		
		g.setColor(Color.black);
		g.setFont(new Font("Arial", Font.BOLD, 16));
		
		for(CardSlot s : _slots){
			s.Draw(g, HumanPlayer);
		}
		
		HumanPlayer.HandCardManager.Draw(g);
		
		g.setColor(Color.black);
		g.setFont(new Font("Arial", Font.BOLD, 24));

		DrawCenteredText(g, "Életerõ: " + String.valueOf(ComputerPlayer.Health), _leftColumnCenterX, _playerAHPY);
		DrawCenteredText(g, "Életerõ: " + String.valueOf(HumanPlayer.Health), _leftColumnCenterX, _playerBHPY);

		g.setColor(Color.white);
		DrawCenteredText(g, "Fázis:", _rightColumnCenterX, CardSlot.Height / 2);
		DrawCenteredText(g, _phase.Name, _rightColumnCenterX, (CardSlot.Height / 4) * 3);
		
		if(_doInspectCard){
			g.drawImage(Art.CardFront_Temp, _cardInspector.X, _cardInspector.Y, _cardInspector.Width, _cardInspector.Height, null);

			DrawCenteredText(g, _cardToInspect.Name, _cardInspector.X + (_cardInspector.Width / 2), (int)(_cardInspector.Y + _cardInspector.Height * 1.1f));
			
			if(_isInspectedCardPlaced && _cardToInspect instanceof CardMonster){
				DrawCenteredText(g, _cardToInspect.IsRotated ? "Védekezõ" : "Támadó", _cardInspector.X + (_cardInspector.Width / 2), (int)(_cardInspector.Y + _cardInspector.Height * 1.2f));
			}
		}
		
		if(_paused){
			g.setColor(new Color(0, 0, 0, 192));	
			g.fillRect(0, 0, _frame.getWidth(), _frame.getHeight());
		}
	}
	
	private void DrawCenteredText(Graphics g, String text, int centerX, int centerY){
		FontMetrics m = g.getFontMetrics();
		int txtWidth = m.stringWidth(text);
		int txtHeight = m.getHeight();
		
		//g.drawString(text, (int)(centerX - (txtWidth / 2.0)), (int)(centerY - (txtHeight / 2.0)));
		g.drawString(text, (int)(centerX - (txtWidth / 2.0)), centerY);
	}
	
	public void RedrawFrame(){
		_frame.Redraw();
	}
	
	public void OnMouseMove(int x, int y){
		_lastMousePosition.X = x;
		_lastMousePosition.Y = y;
		
		CardSlot inspectedSlot = null;
		for(CardSlot s : _slots){	
			if(s.IsInBounds(_lastMousePosition) && s.Owner == HumanPlayer){
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
				_isInspectedCardPlaced = (inspectedSlot instanceof CardSlotPlayfield);
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
					_phase.OnSlotClick(s, HumanPlayer);
					
					change = true;
					break;
				}
			}
			
			if(HumanPlayer.HandCardManager.OnClick(x, y)){
				change = true;
			}
			
			if(change){
				_frame.Redraw();
			}
		}
	}
	
	public void ResetSlotHighlight(){
		for(int x = 0;x<_slots.size();x++){
			_slots.get(x).IsHighlighted = true;
		}
	}
	
	public void SetPlayerSlotHighlight(Player player, CardSlot handSlot, boolean monsterPlacement){
		for(int x = 0;x<_slots.size();x++){
			CardSlot s = _slots.get(x);
			if(s.Owner == player){
				s.IsHighlighted = (s == handSlot || (s instanceof CardSlotPlayfield && ((CardSlotPlayfield)s).MonsterOnly == monsterPlacement));
			}else{
				s.IsHighlighted = false;
			}
		}
	}
	
	public void DamagePlayer(Player p, int damage){
		p.Health -= damage;
		if(p.Health <= 0){
			p.Health = 0;
			
			//TODO: Vége
		}
	}
}
