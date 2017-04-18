package com.bence.yugioh;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.io.*;
import java.util.ArrayList;

import javax.swing.JFileChooser;
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
	public ComputerPlayer ComputerPlayer; //A fels�, sz�m�t�g�p j�t�kos
	public Player HumanPlayer; //Az als�, emberi j�t�kos
	public Player PhasePlayer; //Az akt�v j�t�kos aki �ppen a k�r�t j�tsza
	
	private ArrayList<CardSlot> _slots; //A p�ly�n l�v� �sszes k�rtya hely
	
	private GameFrame _frame; //A j�t�kot megjelen�t� komponens
	
	private Point2 _lastMousePosition; //Az utols� ismert eg�r poz�ci�
	private Point2 _backgroundSize; //A h�tt�r (k�perny�) m�rete
	
	//K�rtya n�zeget� be�ll�t�sai
	private boolean _doInspectCard; //Kell n�zni k�rty�t?
	private boolean _isInspectedCardPlaced; //A vizsg�lt k�rtya a p�ly�n van?
	private CardSlot _cardSlotToInspect; //A vizsg�lt k�rtya
	private Rect _cardInspector; //A k�rtya vizsg�l� helye a k�perny�n
	private Rect _cardInfoRectangle; //A speci�lis k�rtya inform�ci� helye
	
	//Grafika
	private int _playerAHPY; //A fels� j�t�kos �leterej�nek Y helye
	private int _playerBHPY; //Az als� j�t�kos �leterej�nek Y helye
	
	private int _leftColumnCenterX; //A bal oszlop X k�zepe
	private int _rightColumnCenterX; //A jobb oszlop X k�zepe
	private int _computerPlayerY; //A sz�m�t�g�pes j�t�kos felirat helye
	private int _playerY; //Az emberi j�t�kos felirat helye
	
	private Rect _logoArea; //A men�ben a YuGiOh log� helye
	
	//J�t�k f�zisok
	private boolean _skipFirstAttackPhase; //Az els� t�mad� f�zist �t kell ugrani?
	private GamePhase _phase; //Az akt�v f�zis
	public GamePhase PhaseCardPick; //K�rtya felv�tel f�zisa
	public GamePhase PhaseTactics; //Taktikai f�zis
	public GamePhase PhaseAttack; //T�mad� f�tis
	
	//J�t�k �llapot
	private boolean _paused; //A j�t�k sz�netel?
	private boolean _gameRunning; //Fut a j�t�k?
	
	private boolean _gameOver; //A j�t�knak v�ge?
	private String _gameOverText; //A nyertes sz�veg
	
	//UI
	private ArrayList<UIButton> _buttonList; //Lista a Ui gombokr�l
	private UIButton _nextPhaseButton;
	private UIButton _saveGameButton;
	private UIButton _exitGameButtonType1;
	private UIButton _exitGameButtonType2;
	
	/**
	 * L�trehoz egy YuGiOh j�t�kot
	 * @param w A k�perny� sz�less�ge.
	 * @param h A k�perny� magass�ga.
	 * @param f A j�t�kot tartalmaz� UI komponens.
	 */
	public YuGiOhGame(int w, int h, GameFrame f){
		_frame = f;
		_backgroundSize = new Point2(w,h);
		_lastMousePosition = new Point2(0, 0);
		
		//F�zisok elk�sz�t�se
		PhaseCardPick = new CardPickPhase(this);
		PhaseTactics = new TacticsPhase(this);
		PhaseAttack = new AttackPhase(this);
		
		//J�t�kosok elk�sz�t�se
		ComputerPlayer = new ComputerPlayer(this);
		HumanPlayer = new Player(this);
		
		//Kisz�molom a k�rtya m�reteket
		int sideHeight = h / 2; //T�rf�l m�rete
		float paddingY = sideHeight / 3; //Egy k�rtya sor m�rete
		float cardSize = (paddingY * 0.90f); //A t�rf�len 3 sor k�rtya van (minden sor 90% magass�g�, �gy van hely k�zt�k)
		float cardRatio = (float)Art.CardSlot_Hand.getWidth(null) / (float)Art.CardSlot_Hand.getHeight(null); //Kisz�molom egy k�rtya m�reteinek ar�ny�t
		
		CardSlot.Width = (int)(cardSize * cardRatio); //Kisz�molom a k�rtya sz�less�g�t
		CardSlot.Height = (int)(cardSize);
		
		//Elkezdem elk�sz�teni a p�ly�t
		_slots = new ArrayList<CardSlot>();
		
		int leftColumn = (w / 5); //A bal oldali oszlop m�rete
		
		_leftColumnCenterX = (int)(leftColumn / 2.0); //A bal oldali oszlop k�zep�nek sz�m�t�sa
		
		//Elhelyezem a k�t paklit a p�ly�n, ez egyik fel�lre ker�l, a m�sik lentre
		_slots.add(new CardSlotStack(ComputerPlayer, GetCardCenterX(leftColumn / 2), (int)(paddingY / 2.0f)));
		_slots.add(new CardSlotStack(HumanPlayer, GetCardCenterX(leftColumn / 2), (int)(h - CardSlot.Height - paddingY / 2.0f)));
		
		//Kisz�molom a j�t�kos megnevez�sek hely�t
		_computerPlayerY = (int)(paddingY / 4.0f);
		_playerY = (int)(h - paddingY / 4.0f);
				
		//Kisz�molom a j�t�kos �letek hely�t
		_playerAHPY = (int)(paddingY * 2.0f);
		_playerBHPY = (int)(h - paddingY * 2.0f);
		
		//A k�z�ps�, f� oszlop el�k�sz��tse
		float paddingSize = CardSlot.Height; //Eltol�s m�rete 

		//K�t t�mb mely azokat a k�rtya helyeket tartalmazza ahonann k�rty�t lehet majd helyezni a p�ly�ra 
		ArrayList<CardSlotHand> playerAHand = new ArrayList<CardSlotHand>();
		ArrayList<CardSlotHand> playerBHand = new ArrayList<CardSlotHand>();
		
		CardSlotHand temp = null; //�tmeneti v�ltoz�
		
		float rightX = leftColumn + (CardSlot.Width / 2.0f); //Az els� k�rtya helye, ez a bal olszlop m�rete + egy f�l k�rtya sz�less�g
		for (int x = 0; x < 5; x++){ //5db k�rtya lehet egy sorban
			//Sz�m�t�g�pes j�t�kos:
			temp = new CardSlotHand(ComputerPlayer, GetCardCenterX((int)(rightX + paddingSize * x)), 0); //Els� sor x. k�rtya
			
			//Ezt elrakom az els� j�t�kos �tmeneti t�rol�j�ba �s a k�rtya helyek k�z� is
			playerAHand.add(temp);
			_slots.add(temp);
			
			_slots.add(new CardSlotPlayfield(ComputerPlayer, GetCardCenterX((int)(rightX + paddingSize * x)), (int)paddingY, false)); //2. sor x. k�rtya, ide csak var�zsk�rty�kat lehet rakni
			_slots.add(new CardSlotPlayfield(ComputerPlayer, GetCardCenterX((int)(rightX + paddingSize * x)), (int)(paddingY * 1.915f), true)); //3. sor x. k�rtya, csak sz�rny k�rty�k
			
			//Emberi j�t�kos:
			temp = new CardSlotHand(HumanPlayer, GetCardCenterX((int)(rightX + paddingSize * x)), (int)(h - paddingY)); //6. sor x. k�rtya, emberi j�t�kos "keze"
			
			playerBHand.add(temp);
			_slots.add(temp);
			
			_slots.add(new CardSlotPlayfield(HumanPlayer, GetCardCenterX((int)(rightX + paddingSize * x)), (int)(h - paddingY * 2.0f), false)); //5. sor x. k�rtya, csak var�zsk�rty�k
			_slots.add(new CardSlotPlayfield(HumanPlayer, GetCardCenterX((int)(rightX + paddingSize * x)), (int)(h - paddingY * 2.915f), true)); //4. sor x. k�rtya, csak sz�rnyk�rty�k
		}
		
		//Az el�z�leg �tmenetileg elt�rolt "k�z" k�rtya helyek alapj�n elk�sz�tem a j�t�kosok "k�z" kezel�j�t
		ComputerPlayer.HandCardManager = new HandCardManager(ComputerPlayer, playerAHand);
		HumanPlayer.HandCardManager = new HandCardManager(HumanPlayer, playerBHand);
		
		//Az eddigi �sszes helyet v�gig n�zve megkeresem azokat, amelyek a sz�m�t�g�pes j�t�koshoz tartoznak �s �tadom az AI-nak
		ArrayList<CardSlot> cpuSlots = new ArrayList<CardSlot>();
		for(CardSlot s : _slots){
			if(s.Owner == ComputerPlayer){
				cpuSlots.add(s);
			}
		}
		ComputerPlayer.InitSlots(cpuSlots);
		
		//A jobb oldali oszlop m�rete �s helye
		float right2X = w - (w / 5);
		float right2W = w / 5;
		
		_rightColumnCenterX = (int)(right2X + right2W / 2.0);
		
		//A k�rtya n�zeget� el�k�sz�t�se
		float iwidth = right2W * 0.75f; //A n�zeget� sz�less�ge az oszlop 75%-a
		float iheight = (((float)CardSlot.Height / (float)CardSlot.Width) * iwidth); //A magass�g�t egy k�rtya ford�tott m�ret ar�ny�b�l �s a n�z�ke magass�g�b�l sz�molom
		_cardInspector = new Rect((int)(right2X + (right2W / 2.0f) - (iwidth / 2.0f)), (int)(h - iheight * 1.5f), (int)iwidth, (int)iheight); //A kisz�molt m�reteket egy t�glalapba rendezem
		_doInspectCard = false; //Nem n�zek k�rty�n
		_cardInfoRectangle = new Rect(playerBHand.get(0).X - CardSlot.Width / 2, playerBHand.get(0).Y + CardSlot.Height / 2, 
				playerBHand.get(playerBHand.size() - 1).X - playerBHand.get(0).X + CardSlot.Width * 2, CardSlot.Height / 2); //Az emberi j�t�kos k�z helye alapj�n sz�molok egy t�glalapot mely a k�rtya le�r�st tartalmazza
		
		//UI gombok el�k�sz�t�se
		_buttonList = new ArrayList<UIButton>();
		
		//A k�vetkez� f�zis gomb a jobb oldali oszlop k�zep�be rakom �gy, hogy az az oszlop sz�less�g�nek 75%-a
		_buttonList.add(_nextPhaseButton = new UIButton(this, "K�vetkez�", (int)(right2X + (right2W / 2.0f) - (right2W * 0.75f * 0.5f)), (int)paddingY, (int)(right2W * 0.75f), (int)(CardSlot.Height * 0.25f), ButtonAction.NextPhase));
		_nextPhaseButton.Visible = false; //A gomb alapb�l nem kell, hogy l�tsz�djon
		
		//A j�t�knak nincs v�ge, nem fut �s meg van �ll�tva (�gy van men�, ezek kellenek az UpdateMenu-h�z)
		_paused = true;
		_gameOver = false;
		_gameRunning = false;
		
		//A men� gombjainak el�k�sz��tse
		{
			float logoAspectRatio = ((float)Art.Logo.getHeight(null) / (float)Art.Logo.getWidth(null)); //A log� m�retar�nya
			
			int logoWidth = w / 2; //A log� sz�less�ge a k�p sz�less�g�nek fele legyen
			int logoHeight = (int)(logoAspectRatio * logoWidth);
			_logoArea = new Rect((w / 2) - (logoWidth / 2), (h / 6) - (logoHeight / 2), logoWidth, logoHeight);
			
			//Gombok m�rete �s kezd� X,Y helye
			int buttonsWidth = logoWidth / 2;
			int buttonsHeight = 50;
			int buttonsX = ((w / 2) - (buttonsWidth / 2));
			int buttonsY = (h / 10) * 4;
			
			//Egym�s al� rakom a gombokat
			_buttonList.add(new UIButtonMenu(this, "�j j�t�k", buttonsX, buttonsY, buttonsWidth, buttonsHeight, ButtonAction.NewGame));
			_buttonList.add(new UIButtonMenu(this, "J�t�k bet�lt�se", buttonsX, (int)(buttonsY + buttonsHeight * 1.5f), buttonsWidth, buttonsHeight, ButtonAction.Load));
			
			//Ez a h�rom gomb tr�kk�s, mert a j�t�k �llapot�t�l f�gg�en kell megjelenni�k, �gy k�t gomb egym�son van
			_buttonList.add(_exitGameButtonType1 = new UIButtonMenu(this, "Kil�p�s", buttonsX, buttonsY + buttonsHeight * 3, buttonsWidth, buttonsHeight, ButtonAction.Exit));
			_buttonList.add(_saveGameButton = new UIButtonMenu(this, "J�t�k ment�se", buttonsX, buttonsY + buttonsHeight * 3, buttonsWidth, buttonsHeight, ButtonAction.Save));
			_buttonList.add(_exitGameButtonType2 = new UIButtonMenu(this, "Kil�p�s", buttonsX, (int)(buttonsY + buttonsHeight * 4.5f), buttonsWidth, buttonsHeight, ButtonAction.Exit));
			
			//Megh�vom az UpdateMenu f�ggv�nyt, hogy a j� gombok jelenjenek meg
			UpdateMenu();
		}
	}
	
	/**
	 * Elind�t egy �j j�t�kot
	 */
	private void StartGame(){		
		for(CardSlot s : _slots){ //T�rl�m a k�rty�kat a p�ly�r�l
			s.Card = null;
		}
		
		_skipFirstAttackPhase = true; //Az els� t�mad� f�zist ugrani kell
		
		ComputerPlayer.Health = 4000;
		HumanPlayer.Health = 4000;
		
		int deckSize = 30; //Hard Code-olt pakli m�ret
		//K�sz�tek paklit a 2 j�t�kosnak �s �tadom azt is, hogy ez egy kezd� pakli
		HumanPlayer.InitCards(AllCards.CreateDeck(deckSize), true);
		ComputerPlayer.InitCards(AllCards.CreateDeck(deckSize), true);
		
		//Alaphelyzetbe �ll�tom a k�z k�rtya manager-t
		ComputerPlayer.HandCardManager.ResetOffset();
		HumanPlayer.HandCardManager.ResetOffset();
		
		//Az emberi j�t�kos kezd
		PhasePlayer = HumanPlayer;
		
		//Nincs v�ge a j�t�knak, nincs meg�ll�tva �s fut
		_gameOver = false;
		_paused = false;
		_gameRunning = true;
		
		SetPhase(PhaseCardPick, false); //K�rtya h�z�ssal kezd�dik a j�t�k �s nincs sz�ks�g j�t�kos cser�re
	}
	
	/**
	 * J�t�k v�ge.
	 */
	public void OnGameOver(){
		_gameOverText = (ComputerPlayer.Health > 0 ? "Vesztett�l!" : "Nyert�l!"); //Megn�zem, hogy ki nyert
		
		//Be�ll�tom az �llapotokat a men�h�z
		_gameOver = true;
		_paused = true;
		_gameRunning = false;
		
		_nextPhaseButton.Visible = false;
		
		UpdateMenu();
	}
	
	/**
	 * J�t�k f�zist cser�l.
	 */
	public void SetPhase(GamePhase phase, boolean swapPlayers){
		if(phase instanceof AttackPhase && _skipFirstAttackPhase){ //Ha t�mad� f�zis van �s ugrani kell a f�zist...
			_skipFirstAttackPhase = false; //Akkor a k�vetkez�t nem ugrom
			phase.GotoNextPhase();
			return;
		}
		
		if(swapPlayers){ //Ha j�t�kosokat kell cser�lni..
			if(PhasePlayer == HumanPlayer){
				PhasePlayer = ComputerPlayer;
			}else{
				PhasePlayer = HumanPlayer;
			}
		}
		
		_phase = phase;
		_phase.OnPhaseActivated(); //Sz�lok a f�zisnak, hogy kezd�dik
		
		_nextPhaseButton.Visible = (_phase.CanShowNextPhaseButton() && PhasePlayer == HumanPlayer); //Kell, hogy l�tsz�djon a k�vetkez� f�zis gomb?
		
		_frame.Redraw(); //�jra rajzoltatom a k�pet
		
		if(PhasePlayer == ComputerPlayer){ //Ha a sz�m�t�g�p j�tszik akkor sz�lok neki, hogy tegye a dolg�t az "AI"
			ComputerPlayer.DoPhase(_phase);
		}
	}
	
	/**
	 * A sz�m�t�g�p j�tszik?
	 */
	private boolean IsAITurn(){
		return (PhasePlayer == ComputerPlayer);
	}
	
	/**
	 * Visszaadja a m�sik j�t�kost.
	 */
	public Player GetOtherPlayer(Player player){
		if (player == ComputerPlayer)
			return HumanPlayer;
		
		return ComputerPlayer;
	}
	
	/**
	 * Sebz�st sz�mol a j�t�kosra.
	 */
	public void DamagePlayer(Player p, int damage){
		p.Health -= damage;
		if(p.Health <= 0){ //Ha a j�t�kos "meghalt"
			p.Health = 0;
			
			if(!IsAITurn()){ //�s nem a sz�m�t�g�p j�tszik, akkor v�ge a j�t�knak
				OnGameOver();	
			}
		}
	}
	
	/**
	 * Vissaadja egy j�t�kos azon k�rtya helyeit, melyek a p�lya r�sze �s tartalmaz k�rty�t
	 */
	public ArrayList<CardSlot> GetPlayerUsedSlots(Player player){
		ArrayList<CardSlot> slots = new ArrayList<CardSlot>();
		
		for(CardSlot s : _slots){
			if(s.Owner == player && s instanceof CardSlotPlayfield && s.Card != null){
				slots.add(s);
			}
		}
		
		return slots;
	}
	
	/**
	 * Megmondja, hogy egy j�t�kosnak van-e sz�rny k�rty�ja lehelyezve
	 */
	public boolean HasMonstersPlaced(Player player){
		for(CardSlot s : _slots){
			if(s.Owner == player && s instanceof CardSlotPlayfield && s.Card != null){
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Megadja, hogy h�ny plusz ATK pontot kell sz�molni.
	 */
	public int GetAdditionalATK(CardSlot sourceSlot){
		int add = 0;
		
		Player player = sourceSlot.Owner; //Melyik j�t�kost kell sz�molni?
		
		for(CardSlot s : _slots){
			if(s.Owner == player && s != sourceSlot && s instanceof CardSlotPlayfield){ //Ha a slot a j�t�kos� �s nem a forr�s slot �s a slot a p�lya r�sze
				CardSlotPlayfield pf = (CardSlotPlayfield)s;
				if(pf.MonsterOnly && pf.Card != null){ //ATK csak sz�rnynek lehet, van-e k�rtya?
					CardMonster cm = (CardMonster)pf.Card;
					if(cm.Special != null && cm.Special instanceof AddATKAllSpecial){ //Ha van k�rtya, annak van special-ja �s az a special +ATK
						add += ((AddATKAllSpecial)cm.Special).ATK;
					}
				}
			}
		}
		
		return add;
	}
	
	/**
	 * Megadja, hogy h�ny plusz DEF pontot kell sz�molni.
	 */
	public int GetAdditionalDEF(CardSlot sourceSlot){
		int add = 0;
		
		Player player = sourceSlot.Owner; //Melyik j�t�kost kell sz�molni?
		
		for(CardSlot s : _slots){
			if(s.Owner == player && s != sourceSlot && s instanceof CardSlotPlayfield){ //Ha a slot a j�t�kos� �s nem a forr�s slot �s a slot a p�lya r�sze
				CardSlotPlayfield pf = (CardSlotPlayfield)s;
				if(pf.MonsterOnly && pf.Card != null){ //DEF csak sz�rnynek lehet, van-e k�rtya?
					CardMonster cm = (CardMonster)pf.Card;
					if(cm.Special != null && cm.Special instanceof AddDEFAllSpecial){ //Ha van k�rtya, annak van special-ja �s az a special +DEF
						add += ((AddDEFAllSpecial)cm.Special).DEF;
					}
				}
			}
		}
		
		return add;
	}
	
	/**
	 * Kirajzolja a k�perny�re a j�t�kot.
	 */
	public void Draw(Graphics g){
		g.drawImage(Art.Background, 0, 0, _backgroundSize.X, _backgroundSize.Y, null); //A h�tt�r

		for(CardSlot s : _slots){ //Kirajzolok minden slot-ot
			s.Draw(g, HumanPlayer);
		}
		
		HumanPlayer.HandCardManager.Draw(g); //Az emberi j�t�kos kez�ben l�v� k�rty�k plusz inform�ci�i
		
		g.setColor(Color.white); //Feh�r sz�n
		g.setFont(new Font("Arial", Font.BOLD, 24)); //24-es f�lk�v�r Arial

		//J�t�kos �leter�k �s j�t�kos megnevez�sek
		DrawCenteredText(g, "�leter�: " + String.valueOf(ComputerPlayer.Health), _leftColumnCenterX, _playerAHPY);
		DrawCenteredText(g, "�leter�: " + String.valueOf(HumanPlayer.Health), _leftColumnCenterX, _playerBHPY);

		DrawCenteredText(g, "Sz�m�t�g�p", _leftColumnCenterX, _computerPlayerY);
		DrawCenteredText(g, "J�t�kos", _leftColumnCenterX, _playerY);
	
		//Ha van akt�v f�zis (men�ben nincs)
		if(_phase != null){
			//F�zissal kapcsolatos inform�ci�k
			DrawCenteredText(g, (PhasePlayer == HumanPlayer) ? "A te k�r�d" : "Ellenf�l k�re", _rightColumnCenterX, (CardSlot.Height / 4) * 1);
			DrawCenteredText(g, "F�zis:", _rightColumnCenterX, CardSlot.Height / 2);
			DrawCenteredText(g, _phase.Name, _rightColumnCenterX, (CardSlot.Height / 4) * 3);
		}
		
		if(_doInspectCard){ //K�rtya n�zeget� kell?
			Card inspectedCard = _cardSlotToInspect.Card;
			if(inspectedCard == null){ //Ha nincs mit n�zni (megsz�nt a n�zeget�s k�zbe)
				_doInspectCard = false;
			}
			else{
				g.setFont(new Font("Arial", Font.BOLD, 20)); //20-as f�lk�v�r Arial
				FontMetrics font = g.getFontMetrics(); //A bet�tipus m�retei
				
				//Kirajzolom a megfigyelt k�rty�t
				g.drawImage(inspectedCard.FrontImage, _cardInspector.X, _cardInspector.Y, _cardInspector.Width, _cardInspector.Height, null);
	
				int baseTextY = (int)(_cardInspector.Y + _cardInspector.Height * 1.1f); //A sz�veg kezd� Y-ja
				DrawCenteredText(g, inspectedCard.Name, _cardInspector.X + (_cardInspector.Width / 2), baseTextY); //A k�rtya neve k�z�pre igaz�tva
				
				g.setFont(new Font("Arial", Font.BOLD, 16)); //16-os f�lk�v�r Arial
				
				if( inspectedCard instanceof CardMonster){ //Ha sz�rnyk�rty�t n�zek
					CardMonster monster = (CardMonster)inspectedCard;
					
					//Elt�rolom a plusz ATK �s DEF �rt�keket
					int addATK = GetAdditionalATK(_cardSlotToInspect);
					int addDEF = GetAdditionalDEF(_cardSlotToInspect);
					
					//ATK �s DEF inform�ci�
					String monsterData = "ATK: " + String.valueOf(monster.Attack + addATK);
					if(addATK != 0) //Csillaggal jelzem, ha az �rt�k n� m�s k�rtya miatt (�tmenetileg)
						monsterData += "*";
					monsterData += " / DEF: " + String.valueOf(monster.Defense + addDEF);
					if(addDEF != 0)
						monsterData += "*";
					
					DrawCenteredText(g, monsterData, _cardInspector.X + (_cardInspector.Width / 2), baseTextY + font.getAscent()); //1 sorral lejjebb
					
					if(_isInspectedCardPlaced){ //Ha a k�rtya a p�ly�n van, ki�rom, hogy milyen �ll�potban van
						DrawCenteredText(g, inspectedCard.IsRotated ? "V�dekez�" : "T�mad�", _cardInspector.X + (_cardInspector.Width / 2), 
								baseTextY + font.getAscent() * 2); //2 sorral lejjebb
						
					}
					
					if(monster.Special != null){ //Ha van speci�lis k�pess�g megjelen�tem azt is
						DrawInfoBox(g, "Speci�lis k�pess�g: " + monster.Special.GetDescription());
					}
				}else if(inspectedCard instanceof CardMagic){ //Ha var�zsk�rty�r�l van sz�
					DrawInfoBox(g, ((CardMagic)inspectedCard).Effect.GetDescription()); //Megjelen�tem a k�rtya k�pess�g�nek le�r�s�t
				}
			}
		}
		
		_nextPhaseButton.Draw(g); //Kirajzolom a k�vetkez� f�zis gombot (ez k�l�n van a t�bbit�l)
		
		if(_paused){ //Ha van men�
			//Rajzolok egy f�lig �tl�tsz� fekete t�glalapot a j�t�kra
			g.setColor(new Color(0, 0, 0, 150));	
			g.fillRect(0, 0, _frame.getWidth(), _frame.getHeight());
					
			//Kirajzolom a log�t
			g.drawImage(Art.Logo, _logoArea.X, _logoArea.Y, _logoArea.Width, _logoArea.Height, null);
		
			for(UIButton b : _buttonList){ //Kirajzolok minden gombot ami men� gomb
				if (b instanceof UIButtonMenu) {
					b.Draw(g);
				}
			}
			
			if(_gameOver){ //Ha a j�t�k v�get �rt
				g.setFont(new Font("Arial", Font.BOLD, 48)); //48-as f�lk�v�r Arial
				g.setColor(Color.white); //Feh�r
				DrawCenteredText(g, _gameOverText, _backgroundSize.X / 2, (_backgroundSize.Y / 6) * 2); //K�z�pre igaz�tott sz�veg
			}
		}
	}
	
	/**
	 * Kisz�molja az x. k�rtya hely�t a sorban.
	 */
	private int GetCardCenterX(int x){
		return x - (CardSlot.Width / 2);
	}
	
	/**
	 * Kirajzolja a speci�lis k�rtya inf�t.
	 */
	private void DrawInfoBox(Graphics g, String text){
		g.setColor(new Color(0,0,0,175)); //F�lig �tl�tsz� fekete t�glalap
		g.fillRect(_cardInfoRectangle.X, _cardInfoRectangle.Y, _cardInfoRectangle.Width, _cardInfoRectangle.Height);
		
		g.setColor(Color.white);
		DrawCenteredTextIntoArea(g, text, new Rect(
				_cardInfoRectangle.X + 10, _cardInfoRectangle.Y + 10, _cardInfoRectangle.Width - 20, _cardInfoRectangle.Height - 20));
	}
	
	/**
	 * Kit�lt k�z�pre igaz�tott sz�veggel egy t�glalapot
	 */
	public static void DrawCenteredTextIntoArea(Graphics g, String text, Rect area){
		FontMetrics m = g.getFontMetrics(); //Bet�tipus inf�k
		
		//Sz�tv�lasztom a sz�veget sorokra, minden sor pont belef�r a t�glalap sz�less�g�be
		ArrayList<String> lines = new ArrayList<String>();
		
		String line = ""; //Egy �tmeneti sor
		int lineW = 0; //A sor sz�less�ge
		
		String[] parts = text.split(" "); //Sz�tv�lasztom a sz�veget szavakra.
		for(int x = 0; x < parts.length; x++){
			int partW = m.stringWidth(parts[x] + " "); //Megn�zem egy sz� + egy sz�k�z sz�less�g�t
			if(lineW + partW < area.Width){ //Ha ezt hozz�rakom a sorhoz �s belef�r a sorba..
				line += (line.length() == 0 ? "" : " ") + parts[x]; //Hozz�adom a sz�veget a sorhoz, ha a sorba m�r van sz�veg rakok el� egy sz�k�zt is
				lineW += partW;
			}else{ //Ha nem f�r bele a sorba �j sort kezdek
				lines.add(line);
				line = parts[x];
				lineW = 0;
			}
		}
		
		if(line.length() > 0){ //Ha maradt valami az csak �j sorba mehet
			lines.add(line);
		}
		
		int drawY = area.Y + area.Height / 2 - ((lines.size() / 2) * (m.getAscent())); //A sorok sz�ma �s egy sor magass�ga alapj�n kisz�molom, hogy honann kezdjem a rajzol�st Y-ban
		
		for(int x = 0;x<lines.size();x++){
			DrawCenteredText(g, lines.get(x), area.X + area.Width / 2, drawY + m.getAscent() * x); //K�z�pre igaz�tott sor rajzol�s
		}
	}
	
	/**
	 * K�z�pre igaz�tva rajzol egy sor sz�veget.
	 */
	public static void DrawCenteredText(Graphics g, String text, float centerX, float centerY){
		if(text == null)
			return;
		
		FontMetrics m = g.getFontMetrics();
		int txtWidth = m.stringWidth(text);
		int txtHeight = m.getAscent();
		
		g.drawString(text, (int)(centerX - (txtWidth / 2.0f)), (int)(centerY + (txtHeight / 2.0f)));
	}
	
	/**
	 * Jelzi a UI panelnak hogy �jra kell rajzodnia, mert valami megv�ltozott
	 */
	public void RedrawFrame(){
		_frame.Redraw();
	}
	
	/**
	 * A k�rtya n�zeget� friss�t�se.
	 */
	public void UpdateInspectedSlot(){
		//Megkeresem, hogy melyik slot-on van az eg�r amit az emberi j�t�kos megn�zhet
		CardSlot inspectedSlot = null;
		for(CardSlot s : _slots){	
			if(s.IsInBounds(_lastMousePosition) && (s.Owner == HumanPlayer || s instanceof CardSlotPlayfield)){
				inspectedSlot = s;
				break;
			}
		}
		
		if(_doInspectCard){ //Ha m�r n�zek k�rty�t
			if(inspectedSlot == null){ //Az eg�r nincs m�r k�rty�n
				_doInspectCard = false;
				_cardSlotToInspect = null;
				_frame.Redraw();
			}else{ //Az eg�r m�sik k�rty�n van
				_cardSlotToInspect = inspectedSlot;
				_doInspectCard = (inspectedSlot.Card != null);
				_isInspectedCardPlaced = (inspectedSlot instanceof CardSlotPlayfield);
				_frame.Redraw();	
			}
		}else{ //Ha nem n�zek k�rty�n
			if(inspectedSlot != null){ //De az eg�r k�rty�n van
				_cardSlotToInspect = inspectedSlot;
				_doInspectCard = (inspectedSlot.Card != null);
				_isInspectedCardPlaced = (inspectedSlot instanceof CardSlotPlayfield);
				_frame.Redraw();
			}
		}
	}
	
	/**
	 * Eg�r mozg�si esem�ny.
	 */
	public void OnMouseMove(int x, int y){
		if(_lastMousePosition.X != x || _lastMousePosition.Y != y){
			_lastMousePosition.X = x;
			_lastMousePosition.Y = y;
		
			if(!_gameOver && !_paused){ //Ha nincs v�ge a j�t�knak �s nem vagyok men�be
				UpdateInspectedSlot(); //Friss�teni kell a k�rtya n�zeget�t
			}
			
			for(UIButton b : _buttonList){ //V�gig megyek minden gombon amire kattitani lehet �s friss�tem a kin�zet�t
				if((b instanceof UIButtonMenu && _paused) || (!(b instanceof UIButtonMenu) && !_paused)){
					if(b.OnMouseMove(x, y)){
						_frame.Redraw();
					}	
				}
			}
		}
	}
	
	/**
	 * Eg�r kattint�si esem�ny.
	 */
	public void OnMouseClick(int x, int y){
		boolean change = false; //V�ltozott valami?

		if(!_gameOver && !_paused){
			if(PhasePlayer == HumanPlayer){ //Ha az emberi j�t�kos k�re van akkor megn�zem, hogy slot-ra kattintott-e
				for(CardSlot s : _slots){
					if(s.IsInBounds(x, y)){
						_phase.OnSlotClick(s); //Ha igen akkor sz�lok a f�zisnak, hogy kezdjen vele valamit
					
						change = true;
						break;
					}
				}
				if(!change){ //Kattint�s volt, de nem slot-ra
					_phase.OnSlotClick(null);
				}
			}
			
			if(HumanPlayer.HandCardManager.OnClick(x, y)){ //K�zre kattintott?
				change = true;
			}
		}
		
		if(change){
			_frame.Redraw();
		}else{
			for(UIButton b : _buttonList){ //Ha nem slot-ra volt kattint�s, lehet, hogy gombra volt
				if((b instanceof UIButtonMenu && _paused) || (!(b instanceof UIButtonMenu) && !_paused)){
					b.OnMouseClick(x, y);
				}
			}
		}
	}
	
	/**
	 * UI gomb kattint�si esem�ny.
	 */
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
	
	/**
	 * Elmenti a j�t�kot egy f�jlba.
	 */
	private void SaveGame() {
		//Nyitok egy f�jl ment�s ablakot ami sav f�jolkat akar menteni a felhaszn�l� mapp�j�ba
		JFileChooser saveDialog = new JFileChooser();
		saveDialog.setFileFilter(new FileNameExtensionFilter("YuGiOh save files", "sav"));
		saveDialog.setCurrentDirectory(new File(System.getProperty("user.home")));
		if(saveDialog.showSaveDialog(_frame) == JFileChooser.APPROVE_OPTION){
			File file = saveDialog.getSelectedFile();
			if(!file.toString().toLowerCase().endsWith("sav")){ //Ha hi�nyzik a kiterjeszt�s
				file = new File(file.toString() + ".sav");
			}
			
			DataOutputStream data = null;
			try{
				data = new DataOutputStream(new FileOutputStream(file));
				
				data.writeUTF("YuGiOh"); //Ment�s fejl�ce
				
				//Elmentem a k�rtya slot-okat, darabsz�m�t, index�t, tartalm�t, �llapot�t
				data.writeInt(_slots.size());
				for(int x = 0;x<_slots.size();x++){
					CardSlot slot = _slots.get(x);
					data.writeInt(x);
					if(slot instanceof CardSlotPlayfield){ //Csak a p�lya k�rty�it mentem el
						data.writeBoolean(((CardSlotPlayfield)slot).Used);
						data.writeBoolean(slot.Card != null);
						if(slot.Card != null){
							data.writeInt(slot.Card.SaveUID);
						}
					}
				}
				
				//Elmentem a paklikat
				WriteCardArray(data, ComputerPlayer.Hand);
				WriteCardArray(data, ComputerPlayer.Deck);
				
				WriteCardArray(data, HumanPlayer.Hand);
				WriteCardArray(data, HumanPlayer.Deck);
				
				//A j�t�kosok �let�t
				data.writeInt(HumanPlayer.Health);
				data.writeInt(ComputerPlayer.Health);
				
				//Azt, hogy ki j�tszik
				data.writeBoolean(PhasePlayer == HumanPlayer);
				
				//A jelenlegi f�zist is
				if(_phase == PhaseCardPick){
					data.writeByte(1);
				}else if(_phase == PhaseTactics){
					data.writeByte(2);
				}if(_phase == PhaseAttack){
					data.writeByte(3);
				}
				
				//�s azt hogy az els� t�mad� f�zison t�l jutott-e a j�t�k
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
	
	/**
	 * Elment egy t�mbnyi k�ry�t.
	 */
	private void WriteCardArray(DataOutputStream data, ArrayList<Card> cards) throws IOException{
		data.writeInt(cards.size());
		for(int x = 0;x<cards.size();x++){
			data.writeInt(cards.get(x).SaveUID);
		}
	}
	
	/**
	 * Bet�lt egy ment�st �s folytatja onnan a j�t�kot.
	 */
	private void LoadGame(){
		//Nyitok egy f�jl bet�lt�s ablakot.
		JFileChooser openDialog = new JFileChooser();
		openDialog.setFileFilter(new FileNameExtensionFilter("YuGiOh save files", "sav"));
		openDialog.setCurrentDirectory(new File(System.getProperty("user.home")));
		if(openDialog.showOpenDialog(_frame) == JFileChooser.APPROVE_OPTION){
			DataInputStream data = null;
			try{
				data = new DataInputStream(new FileInputStream(openDialog.getSelectedFile()));
				
				String header = data.readUTF();
				if(!header.equalsIgnoreCase("YuGiOh")){ //Ellen�rz�m a fejl�c helyess�g�t
					throw new Exception("Hib�s fejl�c!");
				}
				
				//Elkezdem bet�lteni a k�rtya slot inform�ci�kat
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
				
				//Bet�lt�m a paklikat is
				ArrayList<Card> tempList = new ArrayList<Card>();
				ReadCardList(data, tempList);
				ComputerPlayer.AddArrayOfCards(tempList);
				ReadCardList(data, tempList);
				ComputerPlayer.InitCards(tempList, false);
								
				ReadCardList(data, tempList);
				HumanPlayer.AddArrayOfCards(tempList);
				ReadCardList(data, tempList);
				HumanPlayer.InitCards(tempList, false);
				
				//A k�z manager-t �jra kell ind�tani
				ComputerPlayer.HandCardManager.ResetOffset();
				HumanPlayer.HandCardManager.ResetOffset();
				
				//Bet�lt�m a j�t�kosok �let�t
				HumanPlayer.Health = data.readInt();
				ComputerPlayer.Health = data.readInt();
				
				if(data.readBoolean()){ //Azt, hogy ki j�tszik �ppen
					PhasePlayer = HumanPlayer;
				}else{
					PhasePlayer = ComputerPlayer;
				}
				
				switch(data.readByte()){ //A f�zist
				case 1:
					SetPhase(PhaseCardPick, false);
					break;
				case 2:
					SetPhase(PhaseTactics, false);
					break;
				case 3:
					SetPhase(PhaseAttack, false);
					
					for(int x = 0;x<usedStates.length;x++){ //Ha t�mad� f�zis van akkor kell az is hogy melyik sz�rny volt m�r haszn�lva
						CardSlot s = _slots.get(x);
						if(s instanceof CardSlotPlayfield){
							((CardSlotPlayfield)s).Used = usedStates[x];
						}
					}
					break;
				}
				
				_skipFirstAttackPhase = data.readBoolean();
				
				data.close();
				
				//Be�ll�tom a j�t�k �llapotokat
				_gameOver = false;
				_paused = false;
				_gameRunning = true;
				_doInspectCard = false;
				
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
	
	/**
	 * Bet�lt egy tombnyi k�rty�t.
	 */
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
	
	/**
	 * A slot kijel�l�seket alaphelyzetbe �ll�tja.
	 */
	public void ResetSlotHighlight(){
		for(int x = 0;x<_slots.size();x++){
			_slots.get(x).IsHighlighted = true;
		}
	}
	
	/**
	 * A slot kijel�l�seket k�rtya lehelyez�se m�dba �ll�tja.
	 */
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
	
	/**
	 * A k�rtya slot kijel�l�seket lehets�ges t�mad�s c�lpontjaira �ll�tja.
	 */
	public void SetAttackSlotHighlight(CardSlot sourceSlot){
		Player player = sourceSlot.Owner;
		for(CardSlot s : _slots){
			s.IsHighlighted = (s == sourceSlot || (s.Owner != player && s instanceof CardSlotPlayfield && ((CardSlotPlayfield)s).MonsterOnly && s.Card != null));
		}
	}
	
	/**
	 * A slot kijel�l�seket sz�rnyekre �ll�tja.
	 */
	public void SetMagicActivateSlotHighlight(CardSlot sourceSlot){
		Player player = sourceSlot.Owner;
		for(CardSlot s : _slots){
			s.IsHighlighted = (s.Owner == player && s instanceof CardSlotPlayfield && ((CardSlotPlayfield)s).MonsterOnly && s.Card != null);
		}
	}
	
	/**
	 * A slot kijel�l�seket 2db slot alapj�n �ll�tja be.
	 */
	public void SetBotSlotHighlight(CardSlot a, CardSlot b){
		for(CardSlot s : _slots){
			s.IsHighlighted = (s == a || s == b);
		}
	}
	
	/**
	 * A haszn�lt slotokat �jra aktiv�lja.
	 */
	public void ResetSlotUsedStates(){
		for(CardSlot s : _slots){
			if(s instanceof CardSlotPlayfield){
				((CardSlotPlayfield)s).Used = false;
			}
		}
	}
	
	/**
	 * Ki �s be kapcsolja a men�t.
	 */
	public void TogglePauseMenu(){
		if(!_gameOver && _gameRunning && !IsAITurn()){
			_paused = !_paused;
		
			UpdateMenu();
			
			RedrawFrame();
		}
	}
	
	/**
	 * A men� gombok �llapot�t �ll�tja be a j�t�k �llapota alapj�n.
	 */
	private void UpdateMenu() {
		_exitGameButtonType1.Visible = !_gameRunning;
		
		_exitGameButtonType2.Visible = _saveGameButton.Visible = _gameRunning;
	}
}
