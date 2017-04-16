package com.bence.yugioh;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.ArrayList;

import com.bence.yugioh.cards.AllCards;
import com.bence.yugioh.cards.Card;
import com.bence.yugioh.cards.CardMonster;
import com.bence.yugioh.phases.*;
import com.bence.yugioh.player.*;
import com.bence.yugioh.slots.*;
import com.bence.yugioh.utils.*;

public class YuGiOhGame {
	public ComputerPlayer ComputerPlayer; //Top
	public Player HumanPlayer; //Bottom - human player
	public Player PhasePlayer;
	
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
	
	private boolean _skipFirstAttackPhase;
	private GamePhase _phase;	
	public GamePhase PhaseCardPick;
	public GamePhase PhaseTactics;
	public GamePhase PhaseAttack;
	
	private boolean _paused = false;
	
	private ArrayList<UIButton> _buttonList;
	private UIButton _nextPhaseButton;
	
	public YuGiOhGame(int w, int h, GameFrame f){
		_frame = f;
		_backgroundSize = new Point2(w,h);
		
		_slots = new ArrayList<CardSlot>();
		
		_lastMousePosition = new Point2(0, 0);
		
		PhaseCardPick = new CardPickPhase(this);
		PhaseTactics = new TacticsPhase(this);
		PhaseAttack = new AttackPhase(this);
		
		ComputerPlayer = new ComputerPlayer(this);
		HumanPlayer = new Player(this);
		
		
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
		
		ArrayList<CardSlot> cpuSlots = new ArrayList<CardSlot>();
		for(CardSlot s : _slots){
			if(s.Owner == ComputerPlayer){
				cpuSlots.add(s);
			}
		}
		ComputerPlayer.InitSlots(cpuSlots);
		
		float right2X = w - (w / 5);
		float right2W = w / 5;
		
		_rightColumnCenterX = (int)(right2X + right2W / 2.0);
		
		float iwidth = right2W * 0.75f;
		float iheight = (((float)CardSlot.Height / (float)CardSlot.Width) * iwidth);
		_cardInspector = new Rect((int)(right2X + (right2W / 2.0f) - (iwidth / 2.0f)), (int)(h - iheight * 1.5f), (int)iwidth, (int)iheight);
		_doInspectCard = false;
		
		
		_buttonList = new ArrayList<UIButton>();
		_buttonList.add(_nextPhaseButton = new UIButton(this, "Következõ", (int)(right2X + (right2W / 2.0f) - (right2W * 0.75f * 0.5f)), (int)paddingY, (int)(right2W * 0.75f), (int)(CardSlot.Height * 0.25f)));
		
		
		StartGame();
	}
	
	private void StartGame(){		
		_skipFirstAttackPhase = true;
		
		ComputerPlayer.Health = 4000;
		HumanPlayer.Health = 4000;
		
		int deckSize = 30;
		HumanPlayer.InitCards(AllCards.CreateDeck(deckSize));
		ComputerPlayer.InitCards(AllCards.CreateDeck(deckSize));
		
		PhasePlayer = HumanPlayer;
		
		SetPhase(PhaseCardPick, false);
	}
	
	public void SetPhase(GamePhase phase, boolean swapPlayers){
		if(phase instanceof AttackPhase && _skipFirstAttackPhase){
			_skipFirstAttackPhase = false;
			phase.GotoNextPhase();
			return;
		}
		
		if(swapPlayers){
			if(PhasePlayer == HumanPlayer){
				PhasePlayer = ComputerPlayer;
			}else{
				PhasePlayer = HumanPlayer;
			}
		}
		
		_phase = phase;
		_phase.OnPhaseActivated();
		
		_nextPhaseButton.Visible = (_phase.CanShowNextPhaseButton() && PhasePlayer == HumanPlayer);
		
		_frame.Redraw();
		
		if(PhasePlayer == ComputerPlayer){
			ComputerPlayer.DoPhase(_phase);
		}
	}
	
	private int GetCardCenterX(int x){
		return x - (CardSlot.Width / 2);
	}
	
	public void Draw(Graphics g){
		g.drawImage(Art.Background, 0, 0, _backgroundSize.X, _backgroundSize.Y, null);
		
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
		
		DrawCenteredText(g, (PhasePlayer == HumanPlayer) ? "A te köröd" : "Ellenfél köre", _rightColumnCenterX, (CardSlot.Height / 4) * 1);
		DrawCenteredText(g, "Fázis:", _rightColumnCenterX, CardSlot.Height / 2);
		DrawCenteredText(g, _phase.Name, _rightColumnCenterX, (CardSlot.Height / 4) * 3);
		
		if(_doInspectCard){
			g.setFont(new Font("Arial", Font.BOLD, 20));
			FontMetrics font = g.getFontMetrics();
			
			g.drawImage(Art.CardFront_Temp, _cardInspector.X, _cardInspector.Y, _cardInspector.Width, _cardInspector.Height, null);

			int baseTextY = (int)(_cardInspector.Y + _cardInspector.Height * 1.1f);
			DrawCenteredText(g, _cardToInspect.Name, _cardInspector.X + (_cardInspector.Width / 2), baseTextY);
			
			if( _cardToInspect instanceof CardMonster){
				CardMonster monster = (CardMonster)_cardToInspect;
				DrawCenteredText(g, "ATK: " + String.valueOf(monster.Attack) + " / DEF: " + String.valueOf(monster.Defense), _cardInspector.X + (_cardInspector.Width / 2), 
						baseTextY + font.getAscent());
				
				if(_isInspectedCardPlaced){
					DrawCenteredText(g, _cardToInspect.IsRotated ? "Védekezõ" : "Támadó", _cardInspector.X + (_cardInspector.Width / 2), 
							baseTextY + font.getAscent() * 2);
				}
			}
		}
		
		for(UIButton b : _buttonList){
			b.Draw(g);
		}
		
		if(_paused){
			g.setColor(new Color(0, 0, 0, 192));	
			g.fillRect(0, 0, _frame.getWidth(), _frame.getHeight());
		}
	}
	
	public static void DrawCenteredText(Graphics g, String text, float centerX, float centerY){
		FontMetrics m = g.getFontMetrics();
		int txtWidth = m.stringWidth(text);
		int txtHeight = m.getAscent();
		
		g.drawString(text, (int)(centerX - (txtWidth / 2.0f)), (int)(centerY + (txtHeight / 2.0f)));
	}
	
	public void RedrawFrame(){
		_frame.Redraw();
	}
	
	public void UpdateInspectedSlot(){
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
	
	public void OnMouseMove(int x, int y){
		if(_lastMousePosition.X != x || _lastMousePosition.Y != y){
			_lastMousePosition.X = x;
			_lastMousePosition.Y = y;
		
			UpdateInspectedSlot();
			
			for(UIButton b : _buttonList){
				if(b.OnMouseMove(x, y)){
					_frame.Redraw();
				}
			}
		}
	}
	
	public void OnMouseClick(int x, int y){
		boolean change = false;
			
		if(PhasePlayer == HumanPlayer){
			for(CardSlot s : _slots){
				if(s.IsInBounds(x, y)){
					_phase.OnSlotClick(s, HumanPlayer);
				
					change = true;
					break;
				}
			}
			if(!change){
				_phase.OnSlotClick(null, HumanPlayer);
			}
		}
		
		if(HumanPlayer.HandCardManager.OnClick(x, y)){
			change = true;
		}
		
		if(change){
			_frame.Redraw();
		}else{
			for(UIButton b : _buttonList){
				b.OnMouseClick(x, y);
			}
		}
	}
	
	public void OnUIButtonClick(UIButton btn){
		if(btn == _nextPhaseButton){
			_phase.GotoNextPhase();
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
