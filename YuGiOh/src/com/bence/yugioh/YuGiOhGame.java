package com.bence.yugioh;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.io.*;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.bence.yugioh.cards.AddATKAllSpecial;
import com.bence.yugioh.cards.AddDEFAllSpecial;
import com.bence.yugioh.cards.AllCards;
import com.bence.yugioh.cards.Card;
import com.bence.yugioh.cards.CardMagic;
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
	private CardSlot _cardSlotToInspect;
	private Rect _cardInspector;
	private Rect _cardInfoRectangle;
	
	private int _leftColumnCenterX;
	private int _rightColumnCenterX;
	private int _computerPlayerY;
	private int _playerY;
	
	private boolean _skipFirstAttackPhase;
	private GamePhase _phase;	
	public GamePhase PhaseCardPick;
	public GamePhase PhaseTactics;
	public GamePhase PhaseAttack;
	
	private boolean _paused = false;
	private boolean _gameRunning = false;
	
	private ArrayList<UIButton> _buttonList;
	private UIButton _nextPhaseButton;
	private UIButton _saveGameButton;
	private UIButton _exitGameButtonType1;
	private UIButton _exitGameButtonType2;
	
	private boolean _gameOver;
	private String _gameOverText;
	
	private Rect _logoArea;
	
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
		
		_computerPlayerY = (int)(paddingY / 4.0f);
		_playerY = (int)(h - paddingY / 4.0f);
				
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
		_cardInfoRectangle = new Rect(playerBHand.get(0).X - CardSlot.Width / 2, playerBHand.get(0).Y + CardSlot.Height - CardSlot.Height / 2, 
				playerBHand.get(playerBHand.size() - 1).X - playerBHand.get(0).X + CardSlot.Width * 2, CardSlot.Height / 2);
		
		
		_buttonList = new ArrayList<UIButton>();
		_buttonList.add(_nextPhaseButton = new UIButton(this, "Következõ", (int)(right2X + (right2W / 2.0f) - (right2W * 0.75f * 0.5f)), (int)paddingY, (int)(right2W * 0.75f), (int)(CardSlot.Height * 0.25f), ButtonAction.NextPhase));
		_nextPhaseButton.Visible = false;
		
		{
			float logoAspectRatio = ((float)Art.Logo.getHeight(null) / (float)Art.Logo.getWidth(null));
			
			int logoWidth = w / 2;
			int logoHeight = (int)(logoAspectRatio * logoWidth);
			_logoArea = new Rect((w / 2) - (logoWidth / 2), (h / 6) - (logoHeight / 2), logoWidth, logoHeight);
			
			int buttonsWidth = logoWidth / 2;
			int buttonsHeight = 50;
			int buttonsX = ((w / 2) - (buttonsWidth / 2));
			int buttonsY = (h / 10) * 4;
			
			_buttonList.add(new UIButtonMenu(this, "Új játék", buttonsX, buttonsY, buttonsWidth, buttonsHeight, ButtonAction.NewGame));
			_buttonList.add(new UIButtonMenu(this, "Játék betöltése", buttonsX, (int)(buttonsY + buttonsHeight * 1.5f), buttonsWidth, buttonsHeight, ButtonAction.Load));
			_buttonList.add(_exitGameButtonType1 = new UIButtonMenu(this, "Kilépés", buttonsX, buttonsY + buttonsHeight * 3, buttonsWidth, buttonsHeight, ButtonAction.Exit));
			_buttonList.add(_saveGameButton = new UIButtonMenu(this, "Játék mentése", buttonsX, buttonsY + buttonsHeight * 3, buttonsWidth, buttonsHeight, ButtonAction.Save));
			_buttonList.add(_exitGameButtonType2 = new UIButtonMenu(this, "Kilépés", buttonsX, (int)(buttonsY + buttonsHeight * 4.5f), buttonsWidth, buttonsHeight, ButtonAction.Exit));
			
			UpdateMenu();
		}
		
		_paused = true;
		_gameOver = false;
	}
	
	public ArrayList<CardSlot> GetPlayerUsedSlots(Player player){
		ArrayList<CardSlot> slots = new ArrayList<CardSlot>();
		
		for(CardSlot s : _slots){
			if(s.Owner == player && s instanceof CardSlotPlayfield && s.Card != null){
				slots.add(s);
			}
		}
		
		return slots;
	}
	
	public boolean HasMonstersPlaced(Player player){
		for(CardSlot s : _slots){
			if(s.Owner == player && s instanceof CardSlotPlayfield && s.Card != null){
				return true;
			}
		}
		
		return false;
	}
	
	private void StartGame(){		
		for(CardSlot s : _slots){
			s.Card = null;
		}
		
		_skipFirstAttackPhase = true;
		
		ComputerPlayer.Health = 4000;
		HumanPlayer.Health = 4000;
		
		int deckSize = 30;
		HumanPlayer.InitCards(AllCards.CreateDeck(deckSize), true);
		ComputerPlayer.InitCards(AllCards.CreateDeck(deckSize), true);
		
		ComputerPlayer.HandCardManager.ResetOffset();
		HumanPlayer.HandCardManager.ResetOffset();
		
		PhasePlayer = HumanPlayer;
		
		_gameOver = false;
		_paused = false;
		_gameRunning = true;
		
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
	
	private boolean IsAITurn(){
		return (PhasePlayer == ComputerPlayer);
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
		
		g.setColor(Color.white);
		g.setFont(new Font("Arial", Font.BOLD, 24));

		DrawCenteredText(g, "Életerõ: " + String.valueOf(ComputerPlayer.Health), _leftColumnCenterX, _playerAHPY);
		DrawCenteredText(g, "Életerõ: " + String.valueOf(HumanPlayer.Health), _leftColumnCenterX, _playerBHPY);

		DrawCenteredText(g, "Számítógép", _leftColumnCenterX, _computerPlayerY);
		DrawCenteredText(g, "Játékos", _leftColumnCenterX, _playerY);
	
		if(_phase != null){
			DrawCenteredText(g, (PhasePlayer == HumanPlayer) ? "A te köröd" : "Ellenfél köre", _rightColumnCenterX, (CardSlot.Height / 4) * 1);
			DrawCenteredText(g, "Fázis:", _rightColumnCenterX, CardSlot.Height / 2);
			DrawCenteredText(g, _phase.Name, _rightColumnCenterX, (CardSlot.Height / 4) * 3);
		}
		
		if(_doInspectCard){
			Card inspectedCard = _cardSlotToInspect.Card;
			if(inspectedCard == null){
				_doInspectCard = false;
			}
			else{
				g.setFont(new Font("Arial", Font.BOLD, 20));
				FontMetrics font = g.getFontMetrics();
				
				g.drawImage(inspectedCard.FrontImage, _cardInspector.X, _cardInspector.Y, _cardInspector.Width, _cardInspector.Height, null);
	
				int baseTextY = (int)(_cardInspector.Y + _cardInspector.Height * 1.1f);
				DrawCenteredText(g, inspectedCard.Name, _cardInspector.X + (_cardInspector.Width / 2), baseTextY);
				
				g.setFont(new Font("Arial", Font.BOLD, 16));
				
				if( inspectedCard instanceof CardMonster){
					CardMonster monster = (CardMonster)inspectedCard;
					
					int addATK = GetAdditionalATK(_cardSlotToInspect);
					int addDEF = GetAdditionalDEF(_cardSlotToInspect);
					
					String monsterData = "ATK: " + String.valueOf(monster.Attack + addATK);
					if(addATK != 0)
						monsterData += "*";
					monsterData += " / DEF: " + String.valueOf(monster.Defense + addDEF);
					if(addDEF != 0)
						monsterData += "*";
					
					DrawCenteredText(g, monsterData, _cardInspector.X + (_cardInspector.Width / 2), baseTextY + font.getAscent());
					
					if(_isInspectedCardPlaced){
						DrawCenteredText(g, inspectedCard.IsRotated ? "Védekezõ" : "Támadó", _cardInspector.X + (_cardInspector.Width / 2), 
								baseTextY + font.getAscent() * 2);
						
					}
					
					if(monster.Special != null){
						DrawInfoBox(g, "Speciális képesség: " + monster.Special.GetDescription());
					}
				}else if(inspectedCard instanceof CardMagic){
					DrawInfoBox(g, ((CardMagic)inspectedCard).Effect.GetDescription());
				}
			}
		}
		
		for(UIButton b : _buttonList){
			if(!(b instanceof UIButtonMenu)){
				b.Draw(g);
			}
		}
		
		if(_paused){
			g.setColor(new Color(0, 0, 0, 150));	
			g.fillRect(0, 0, _frame.getWidth(), _frame.getHeight());
					
			g.drawImage(Art.Logo, _logoArea.X, _logoArea.Y, _logoArea.Width, _logoArea.Height, null);
		
			for(UIButton b : _buttonList){
				if (b instanceof UIButtonMenu) {
					b.Draw(g);
				}
			}
			
			if(_gameOver){
				g.setFont(new Font("Arial", Font.BOLD, 48));
				g.setColor(Color.white);
				DrawCenteredText(g, _gameOverText, _backgroundSize.X / 2, (_backgroundSize.Y / 6) * 2);
			}
		}
	}
	
	private void DrawInfoBox(Graphics g, String text){
		g.setColor(new Color(0,0,0,175));
		g.fillRect(_cardInfoRectangle.X, _cardInfoRectangle.Y, _cardInfoRectangle.Width, _cardInfoRectangle.Height);
		
		g.setColor(Color.white);
		DrawCenteredTextIntoArea(g, text, new Rect(
				_cardInfoRectangle.X + 10, _cardInfoRectangle.Y + 10, _cardInfoRectangle.Width - 20, _cardInfoRectangle.Height - 20));
	}
	
	public static void DrawCenteredTextIntoArea(Graphics g, String text, Rect area){
		FontMetrics m = g.getFontMetrics();
		
		ArrayList<String> lines = new ArrayList<String>();
		
		String line = "";
		int lineW = 0;
		
		String[] parts = text.split(" ");
		for(int x = 0;x<parts.length;x++){
			int partW = m.stringWidth(parts[x] + " ");
			if(lineW + partW < area.Width){
				line += (line.length() == 0 ? "" : " ") + parts[x];
				lineW += partW;
			}else{
				lines.add(line);
				line = parts[x];
				lineW = 0;
			}
		}
		
		if(line.length() > 0){
			lines.add(line);
		}
		
		int drawY = area.Y + area.Height / 2 - ((lines.size() / 2) * (m.getAscent()));
		
		for(int x = 0;x<lines.size();x++){
			DrawCenteredText(g, lines.get(x), area.X + area.Width / 2, drawY + m.getAscent() * x);
		}
	}
	
	public static void DrawCenteredText(Graphics g, String text, float centerX, float centerY){
		if(text == null)
			return;
		
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
			if(s.IsInBounds(_lastMousePosition) && (s.Owner == HumanPlayer || s instanceof CardSlotPlayfield)){
				inspectedSlot = s;
				break;
			}
		}
		
		if(_doInspectCard){
			if(inspectedSlot == null){
				_doInspectCard = false;
				_cardSlotToInspect = null;
				_frame.Redraw();
			}else{
				_cardSlotToInspect = inspectedSlot;
				_doInspectCard = (inspectedSlot.Card != null);
				_isInspectedCardPlaced = (inspectedSlot instanceof CardSlotPlayfield);
				_frame.Redraw();	
			}
		}else{
			if(inspectedSlot != null){
				_cardSlotToInspect = inspectedSlot;
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
		
			if(!_gameOver && !_paused){
				UpdateInspectedSlot();
			}
			
			for(UIButton b : _buttonList){
				if((b instanceof UIButtonMenu && _paused) || (!(b instanceof UIButtonMenu) && !_paused)){
					if(b.OnMouseMove(x, y)){
						_frame.Redraw();
					}	
				}
			}
		}
	}
	
	public void OnMouseClick(int x, int y){
		boolean change = false;

		if(!_gameOver && !_paused){
			if(PhasePlayer == HumanPlayer){
				for(CardSlot s : _slots){
					if(s.IsInBounds(x, y)){
						_phase.OnSlotClick(s);
					
						change = true;
						break;
					}
				}
				if(!change){
					_phase.OnSlotClick(null);
				}
			}
			
			if(HumanPlayer.HandCardManager.OnClick(x, y)){
				change = true;
			}
		}
		
		if(change){
			_frame.Redraw();
		}else{
			for(UIButton b : _buttonList){
				if((b instanceof UIButtonMenu && _paused) || (!(b instanceof UIButtonMenu) && !_paused)){
					b.OnMouseClick(x, y);
				}
			}
		}
	}
	
	public void OnUIButtonClick(ButtonAction act){
		switch(act){
		case NextPhase:
			_phase.GotoNextPhase();
			break;
		case NewGame:
			StartGame();
			break;
		case Load:
			LoadGame();
			break;
		case Save:
			SaveGame();
			break;
		case Exit:
			System.exit(0);
			break;
		}
	}
	
	private void SaveGame() {
		JFileChooser saveDialog = new JFileChooser();
		saveDialog.setFileFilter(new FileNameExtensionFilter("YuGiOh save files", "sav"));
		saveDialog.setCurrentDirectory(new File(System.getProperty("user.home")));
		if(saveDialog.showSaveDialog(_frame) == JFileChooser.APPROVE_OPTION){
			File file = saveDialog.getSelectedFile();
			if(!file.toString().toLowerCase().endsWith("sav")){
				file = new File(file.toString() + ".sav");
			}
			
			DataOutputStream data = null;
			try{
				data = new DataOutputStream(new FileOutputStream(file));
				
				data.writeUTF("YuGiOh");
				
				data.writeInt(_slots.size());
				for(int x = 0;x<_slots.size();x++){
					CardSlot slot = _slots.get(x);
					data.writeInt(x);
					if(slot instanceof CardSlotPlayfield){
						data.writeBoolean(((CardSlotPlayfield)slot).Used);
						data.writeBoolean(slot.Card != null);
						if(slot.Card != null){
							data.writeInt(slot.Card.SaveUID);
						}
					}
				}
				
				WriteCardArray(data, ComputerPlayer.Hand);
				WriteCardArray(data, ComputerPlayer.Deck);
				
				WriteCardArray(data, HumanPlayer.Hand);
				WriteCardArray(data, HumanPlayer.Deck);
				
				data.writeInt(HumanPlayer.Health);
				data.writeInt(ComputerPlayer.Health);
				
				data.writeBoolean(PhasePlayer == HumanPlayer);
				
				if(_phase == PhaseCardPick){
					data.writeByte(1);
				}else if(_phase == PhaseTactics){
					data.writeByte(2);
				}if(_phase == PhaseAttack){
					data.writeByte(3);
				}
				
				data.writeBoolean(_skipFirstAttackPhase);
				
				data.close();
			}catch(Exception ex){
				ex.printStackTrace();
			}finally{
				if(data != null){
					try {
						data.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	private void WriteCardArray(DataOutputStream data, ArrayList<Card> cards) throws IOException{
		data.writeInt(cards.size());
		for(int x = 0;x<cards.size();x++){
			data.writeInt(cards.get(x).SaveUID);
		}
	}
	
	private void LoadGame(){
		JFileChooser openDialog = new JFileChooser();
		openDialog.setFileFilter(new FileNameExtensionFilter("YuGiOh save files", "sav"));
		openDialog.setCurrentDirectory(new File(System.getProperty("user.home")));
		if(openDialog.showOpenDialog(_frame) == JFileChooser.APPROVE_OPTION){
			DataInputStream data = null;
			try{
				data = new DataInputStream(new FileInputStream(openDialog.getSelectedFile()));
				
				String header = data.readUTF();
				if(!header.equalsIgnoreCase("YuGiOh")){
					throw new Exception("Hibás fejléc!");
				}
				
				int size = data.readInt();
				boolean[] usedStates = new boolean[size];
				for(int i = 0;i<size;i++){
					int x = data.readInt();
					CardSlot slot = _slots.get(x);
					if(slot instanceof CardSlotPlayfield){
						usedStates[x] = data.readBoolean();
						if(data.readBoolean()){
							slot.Card = AllCards.GetCardBySaveUID(data.readInt());
						}else{
							slot.Card = null;
						}
					}
				}
				
				ArrayList<Card> tempList = new ArrayList<Card>();
				ReadCardList(data, tempList);
				ComputerPlayer.AddArrayOfCards(tempList);
				ReadCardList(data, tempList);
				ComputerPlayer.InitCards(tempList, false);
								
				ReadCardList(data, tempList);
				HumanPlayer.AddArrayOfCards(tempList);
				ReadCardList(data, tempList);
				HumanPlayer.InitCards(tempList, false);
				
				ComputerPlayer.HandCardManager.ResetOffset();
				HumanPlayer.HandCardManager.ResetOffset();
				
				HumanPlayer.Health = data.readInt();
				ComputerPlayer.Health = data.readInt();
				
				if(data.readBoolean()){
					PhasePlayer = HumanPlayer;
				}else{
					PhasePlayer = ComputerPlayer;
				}
				
				switch(data.readByte()){
				case 1:
					SetPhase(PhaseCardPick, false);
					break;
				case 2:
					SetPhase(PhaseTactics, false);
					break;
				case 3:
					SetPhase(PhaseAttack, false);
					
					for(int x = 0;x<usedStates.length;x++){
						CardSlot s = _slots.get(x);
						if(s instanceof CardSlotPlayfield){
							((CardSlotPlayfield)s).Used = usedStates[x];
						}
					}
					break;
				}
				
				_skipFirstAttackPhase = data.readBoolean();
				
				data.close();
				
				_gameOver = false;
				_paused = false;
				_gameRunning = true;
				
				UpdateMenu();
			}catch(Exception ex){
				ex.printStackTrace();
			}finally{
				if(data != null){
					try {
						data.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	private void ReadCardList(DataInputStream data, ArrayList<Card> target) throws IOException{
		target.clear();
		
		int count = data.readInt();
		for(int x = 0;x<count;x++){
			Card c = AllCards.GetCardBySaveUID(data.readInt());
			if(c != null){
				target.add(c);
			}
		}
	}
	
	public void ResetSlotHighlight(){
		for(int x = 0;x<_slots.size();x++){
			_slots.get(x).IsHighlighted = true;
		}
	}
	
	public void SetPlacementSlotHighlight(CardSlot handSlot, boolean monsterPlacement){
		Player player = handSlot.Owner;
		for(CardSlot s : _slots){
			if(s.Owner == player){
				s.IsHighlighted = (s == handSlot || (s instanceof CardSlotPlayfield && ((CardSlotPlayfield)s).MonsterOnly == monsterPlacement));
			}else{
				s.IsHighlighted = false;
			}
		}
	}
	
	public void SetAttackSlotHighlight(CardSlot sourceSlot){
		Player player = sourceSlot.Owner;
		for(CardSlot s : _slots){
			s.IsHighlighted = (s == sourceSlot || (s.Owner != player && s instanceof CardSlotPlayfield && ((CardSlotPlayfield)s).MonsterOnly && s.Card != null));
		}
	}
	
	public void SetMagicActivateSlotHighlight(CardSlot sourceSlot){
		Player player = sourceSlot.Owner;
		for(CardSlot s : _slots){
			s.IsHighlighted = (s.Owner == player && s instanceof CardSlotPlayfield && ((CardSlotPlayfield)s).MonsterOnly && s.Card != null);
		}
	}
	
	public void SetBotSlotHighlight(CardSlot a, CardSlot b){
		for(CardSlot s : _slots){
			s.IsHighlighted = (s == a || s == b);
		}
	}
	
	public void ResetSlotUsedStates(){
		for(CardSlot s : _slots){
			if(s instanceof CardSlotPlayfield){
				((CardSlotPlayfield)s).Used = false;
			}
		}
	}
	
	public int GetAdditionalATK(CardSlot sourceSlot){
		int add = 0;
		
		Player player = sourceSlot.Owner;
		
		for(CardSlot s : _slots){
			if(s.Owner == player && s != sourceSlot && s instanceof CardSlotPlayfield){
				CardSlotPlayfield pf = (CardSlotPlayfield)s;
				if(pf.MonsterOnly && pf.Card != null){
					CardMonster cm = (CardMonster)pf.Card;
					if(cm.Special != null && cm.Special instanceof AddATKAllSpecial){
						add += ((AddATKAllSpecial)cm.Special).ATK;
					}
				}
			}
		}
		
		return add;
	}
	
	public int GetAdditionalDEF(CardSlot sourceSlot){
		int add = 0;
		
		Player player = sourceSlot.Owner;
		
		for(CardSlot s : _slots){
			if(s.Owner == player && s != sourceSlot && s instanceof CardSlotPlayfield){
				CardSlotPlayfield pf = (CardSlotPlayfield)s;
				if(pf.MonsterOnly && pf.Card != null){
					CardMonster cm = (CardMonster)pf.Card;
					if(cm.Special != null && cm.Special instanceof AddDEFAllSpecial){
						add += ((AddDEFAllSpecial)cm.Special).DEF;
					}
				}
			}
		}
		
		return add;
	}
	
	public Player GetOtherPlayer(Player player){
		if (player == ComputerPlayer)
			return HumanPlayer;
		
		return ComputerPlayer;
	}
	
	public void DamagePlayer(Player p, int damage){
		p.Health -= damage;
		if(p.Health <= 0){
			p.Health = 0;
			
			if(!IsAITurn()){
				OnGameOver();	
			}
		}
	}
	
	public void OnGameOver(){
		_gameOverText = (ComputerPlayer.Health > 0 ? "Vesztettél!" : "Nyertél!");
		
		_gameOver = true;
		_paused = true;
		_gameRunning = false;
		
		_nextPhaseButton.Visible = false;
		
		UpdateMenu();
	}
	
	public void TogglePauseMenu(){
		if(!_gameOver && _gameRunning && !IsAITurn()){
			_paused = !_paused;
		
			UpdateMenu();
			
			RedrawFrame();
		}
	}
	
	private void UpdateMenu() {
		_exitGameButtonType1.Visible = !_gameRunning;
		
		_exitGameButtonType2.Visible = _saveGameButton.Visible = _gameRunning;
	}
}
