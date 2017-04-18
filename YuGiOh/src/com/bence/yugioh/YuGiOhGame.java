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
	public ComputerPlayer ComputerPlayer; //A felsõ, számítógép játékos
	public Player HumanPlayer; //Az alsó, emberi játékos
	public Player PhasePlayer; //Az aktív játékos aki éppen a körét játsza
	
	private ArrayList<CardSlot> _slots; //A pályán lévõ összes kártya hely
	
	private GameFrame _frame; //A játékot megjelenítõ komponens
	
	private Point2 _lastMousePosition; //Az utolsó ismert egér pozíció
	private Point2 _backgroundSize; //A háttér (képernyõ) mérete
	
	//Kártya nézegetõ beállításai
	private boolean _doInspectCard; //Kell nézni kártyát?
	private boolean _isInspectedCardPlaced; //A vizsgált kártya a pályán van?
	private CardSlot _cardSlotToInspect; //A vizsgált kártya
	private Rect _cardInspector; //A kártya vizsgáló helye a képernyõn
	private Rect _cardInfoRectangle; //A speciális kártya információ helye
	
	//Grafika
	private int _playerAHPY; //A felsõ játékos életerejének Y helye
	private int _playerBHPY; //Az alsõ játékos életerejének Y helye
	
	private int _leftColumnCenterX; //A bal oszlop X közepe
	private int _rightColumnCenterX; //A jobb oszlop X közepe
	private int _computerPlayerY; //A számítógépes játékos felirat helye
	private int _playerY; //Az emberi játékos felirat helye
	
	private Rect _logoArea; //A menüben a YuGiOh logó helye
	
	//Játék fázisok
	private boolean _skipFirstAttackPhase; //Az elsõ támadó fázist át kell ugrani?
	private GamePhase _phase; //Az aktív fázis
	public GamePhase PhaseCardPick; //Kártya felvétel fázisa
	public GamePhase PhaseTactics; //Taktikai fázis
	public GamePhase PhaseAttack; //Támadó fátis
	
	//Játék állapot
	private boolean _paused; //A játék szünetel?
	private boolean _gameRunning; //Fut a játék?
	
	private boolean _gameOver; //A játéknak vége?
	private String _gameOverText; //A nyertes szöveg
	
	//UI
	private ArrayList<UIButton> _buttonList; //Lista a Ui gombokról
	private UIButton _nextPhaseButton;
	private UIButton _saveGameButton;
	private UIButton _exitGameButtonType1;
	private UIButton _exitGameButtonType2;
	
	/**
	 * Létrehoz egy YuGiOh játékot
	 * @param w A képernyõ szélessége.
	 * @param h A képernyõ magassága.
	 * @param f A játékot tartalmazó UI komponens.
	 */
	public YuGiOhGame(int w, int h, GameFrame f){
		_frame = f;
		_backgroundSize = new Point2(w,h);
		_lastMousePosition = new Point2(0, 0);
		
		//Fázisok elkészítése
		PhaseCardPick = new CardPickPhase(this);
		PhaseTactics = new TacticsPhase(this);
		PhaseAttack = new AttackPhase(this);
		
		//Játékosok elkészítése
		ComputerPlayer = new ComputerPlayer(this);
		HumanPlayer = new Player(this);
		
		//Kiszámolom a kártya méreteket
		int sideHeight = h / 2; //Térfél mérete
		float paddingY = sideHeight / 3; //Egy kártya sor mérete
		float cardSize = (paddingY * 0.90f); //A térfélen 3 sor kártya van (minden sor 90% magasságú, így van hely köztük)
		float cardRatio = (float)Art.CardSlot_Hand.getWidth(null) / (float)Art.CardSlot_Hand.getHeight(null); //Kiszámolom egy kártya méreteinek arányát
		
		CardSlot.Width = (int)(cardSize * cardRatio); //Kiszámolom a kártya szélességét
		CardSlot.Height = (int)(cardSize);
		
		//Elkezdem elkészíteni a pályát
		_slots = new ArrayList<CardSlot>();
		
		int leftColumn = (w / 5); //A bal oldali oszlop mérete
		
		_leftColumnCenterX = (int)(leftColumn / 2.0); //A bal oldali oszlop közepének számítása
		
		//Elhelyezem a két paklit a pályán, ez egyik felülre kerül, a másik lentre
		_slots.add(new CardSlotStack(ComputerPlayer, GetCardCenterX(leftColumn / 2), (int)(paddingY / 2.0f)));
		_slots.add(new CardSlotStack(HumanPlayer, GetCardCenterX(leftColumn / 2), (int)(h - CardSlot.Height - paddingY / 2.0f)));
		
		//Kiszámolom a játékos megnevezések helyét
		_computerPlayerY = (int)(paddingY / 4.0f);
		_playerY = (int)(h - paddingY / 4.0f);
				
		//Kiszámolom a játékos életek helyét
		_playerAHPY = (int)(paddingY * 2.0f);
		_playerBHPY = (int)(h - paddingY * 2.0f);
		
		//A középsõ, fõ oszlop elõkészíétse
		float paddingSize = CardSlot.Height; //Eltolás mérete 

		//Két tömb mely azokat a kártya helyeket tartalmazza ahonann kártyát lehet majd helyezni a pályára 
		ArrayList<CardSlotHand> playerAHand = new ArrayList<CardSlotHand>();
		ArrayList<CardSlotHand> playerBHand = new ArrayList<CardSlotHand>();
		
		CardSlotHand temp = null; //Átmeneti változó
		
		float rightX = leftColumn + (CardSlot.Width / 2.0f); //Az elsõ kártya helye, ez a bal olszlop mérete + egy fél kártya szélesség
		for (int x = 0; x < 5; x++){ //5db kártya lehet egy sorban
			//Számítógépes játékos:
			temp = new CardSlotHand(ComputerPlayer, GetCardCenterX((int)(rightX + paddingSize * x)), 0); //Elsõ sor x. kártya
			
			//Ezt elrakom az elsõ játékos átmeneti tárolójába és a kártya helyek közé is
			playerAHand.add(temp);
			_slots.add(temp);
			
			_slots.add(new CardSlotPlayfield(ComputerPlayer, GetCardCenterX((int)(rightX + paddingSize * x)), (int)paddingY, false)); //2. sor x. kártya, ide csak varázskártyákat lehet rakni
			_slots.add(new CardSlotPlayfield(ComputerPlayer, GetCardCenterX((int)(rightX + paddingSize * x)), (int)(paddingY * 1.915f), true)); //3. sor x. kártya, csak szörny kártyák
			
			//Emberi játékos:
			temp = new CardSlotHand(HumanPlayer, GetCardCenterX((int)(rightX + paddingSize * x)), (int)(h - paddingY)); //6. sor x. kártya, emberi játékos "keze"
			
			playerBHand.add(temp);
			_slots.add(temp);
			
			_slots.add(new CardSlotPlayfield(HumanPlayer, GetCardCenterX((int)(rightX + paddingSize * x)), (int)(h - paddingY * 2.0f), false)); //5. sor x. kártya, csak varázskártyák
			_slots.add(new CardSlotPlayfield(HumanPlayer, GetCardCenterX((int)(rightX + paddingSize * x)), (int)(h - paddingY * 2.915f), true)); //4. sor x. kártya, csak szörnykártyák
		}
		
		//Az elõzõleg átmenetileg eltárolt "kéz" kártya helyek alapján elkészítem a játékosok "kéz" kezelõjét
		ComputerPlayer.HandCardManager = new HandCardManager(ComputerPlayer, playerAHand);
		HumanPlayer.HandCardManager = new HandCardManager(HumanPlayer, playerBHand);
		
		//Az eddigi összes helyet végig nézve megkeresem azokat, amelyek a számítógépes játékoshoz tartoznak és átadom az AI-nak
		ArrayList<CardSlot> cpuSlots = new ArrayList<CardSlot>();
		for(CardSlot s : _slots){
			if(s.Owner == ComputerPlayer){
				cpuSlots.add(s);
			}
		}
		ComputerPlayer.InitSlots(cpuSlots);
		
		//A jobb oldali oszlop mérete és helye
		float right2X = w - (w / 5);
		float right2W = w / 5;
		
		_rightColumnCenterX = (int)(right2X + right2W / 2.0);
		
		//A kártya nézegetõ elõkészítése
		float iwidth = right2W * 0.75f; //A nézegetõ szélessége az oszlop 75%-a
		float iheight = (((float)CardSlot.Height / (float)CardSlot.Width) * iwidth); //A magasságát egy kártya fordított méret arányából és a nézõke magasságából számolom
		_cardInspector = new Rect((int)(right2X + (right2W / 2.0f) - (iwidth / 2.0f)), (int)(h - iheight * 1.5f), (int)iwidth, (int)iheight); //A kiszámolt méreteket egy téglalapba rendezem
		_doInspectCard = false; //Nem nézek kártyán
		_cardInfoRectangle = new Rect(playerBHand.get(0).X - CardSlot.Width / 2, playerBHand.get(0).Y + CardSlot.Height / 2, 
				playerBHand.get(playerBHand.size() - 1).X - playerBHand.get(0).X + CardSlot.Width * 2, CardSlot.Height / 2); //Az emberi játékos kéz helye alapján számolok egy téglalapot mely a kártya leírást tartalmazza
		
		//UI gombok elõkészítése
		_buttonList = new ArrayList<UIButton>();
		
		//A következõ fázis gomb a jobb oldali oszlop közepébe rakom úgy, hogy az az oszlop szélességének 75%-a
		_buttonList.add(_nextPhaseButton = new UIButton(this, "Következõ", (int)(right2X + (right2W / 2.0f) - (right2W * 0.75f * 0.5f)), (int)paddingY, (int)(right2W * 0.75f), (int)(CardSlot.Height * 0.25f), ButtonAction.NextPhase));
		_nextPhaseButton.Visible = false; //A gomb alapból nem kell, hogy látszódjon
		
		//A játéknak nincs vége, nem fut és meg van állítva (így van menü, ezek kellenek az UpdateMenu-höz)
		_paused = true;
		_gameOver = false;
		_gameRunning = false;
		
		//A menü gombjainak elõkészíétse
		{
			float logoAspectRatio = ((float)Art.Logo.getHeight(null) / (float)Art.Logo.getWidth(null)); //A logó méretaránya
			
			int logoWidth = w / 2; //A logó szélessége a kép szélességének fele legyen
			int logoHeight = (int)(logoAspectRatio * logoWidth);
			_logoArea = new Rect((w / 2) - (logoWidth / 2), (h / 6) - (logoHeight / 2), logoWidth, logoHeight);
			
			//Gombok mérete és kezdõ X,Y helye
			int buttonsWidth = logoWidth / 2;
			int buttonsHeight = 50;
			int buttonsX = ((w / 2) - (buttonsWidth / 2));
			int buttonsY = (h / 10) * 4;
			
			//Egymás alá rakom a gombokat
			_buttonList.add(new UIButtonMenu(this, "Új játék", buttonsX, buttonsY, buttonsWidth, buttonsHeight, ButtonAction.NewGame));
			_buttonList.add(new UIButtonMenu(this, "Játék betöltése", buttonsX, (int)(buttonsY + buttonsHeight * 1.5f), buttonsWidth, buttonsHeight, ButtonAction.Load));
			
			//Ez a három gomb trükkös, mert a játék állapotától függõen kell megjelenniük, így két gomb egymáson van
			_buttonList.add(_exitGameButtonType1 = new UIButtonMenu(this, "Kilépés", buttonsX, buttonsY + buttonsHeight * 3, buttonsWidth, buttonsHeight, ButtonAction.Exit));
			_buttonList.add(_saveGameButton = new UIButtonMenu(this, "Játék mentése", buttonsX, buttonsY + buttonsHeight * 3, buttonsWidth, buttonsHeight, ButtonAction.Save));
			_buttonList.add(_exitGameButtonType2 = new UIButtonMenu(this, "Kilépés", buttonsX, (int)(buttonsY + buttonsHeight * 4.5f), buttonsWidth, buttonsHeight, ButtonAction.Exit));
			
			//Meghívom az UpdateMenu függvényt, hogy a jó gombok jelenjenek meg
			UpdateMenu();
		}
	}
	
	/**
	 * Elindít egy új játékot
	 */
	private void StartGame(){		
		for(CardSlot s : _slots){ //Törlöm a kártyákat a pályáról
			s.Card = null;
		}
		
		_skipFirstAttackPhase = true; //Az elsõ támadó fázist ugrani kell
		
		ComputerPlayer.Health = 4000;
		HumanPlayer.Health = 4000;
		
		int deckSize = 30; //Hard Code-olt pakli méret
		//Készítek paklit a 2 játékosnak és átadom azt is, hogy ez egy kezdõ pakli
		HumanPlayer.InitCards(AllCards.CreateDeck(deckSize), true);
		ComputerPlayer.InitCards(AllCards.CreateDeck(deckSize), true);
		
		//Alaphelyzetbe állítom a kéz kártya manager-t
		ComputerPlayer.HandCardManager.ResetOffset();
		HumanPlayer.HandCardManager.ResetOffset();
		
		//Az emberi játékos kezd
		PhasePlayer = HumanPlayer;
		
		//Nincs vége a játéknak, nincs megállítva és fut
		_gameOver = false;
		_paused = false;
		_gameRunning = true;
		
		SetPhase(PhaseCardPick, false); //Kártya húzással kezdõdik a játék és nincs szükség játékos cserére
	}
	
	/**
	 * Játék vége.
	 */
	public void OnGameOver(){
		_gameOverText = (ComputerPlayer.Health > 0 ? "Vesztettél!" : "Nyertél!"); //Megnézem, hogy ki nyert
		
		//Beállítom az állapotokat a menühöz
		_gameOver = true;
		_paused = true;
		_gameRunning = false;
		
		_nextPhaseButton.Visible = false;
		
		UpdateMenu();
	}
	
	/**
	 * Játék fázist cserél.
	 */
	public void SetPhase(GamePhase phase, boolean swapPlayers){
		if(phase instanceof AttackPhase && _skipFirstAttackPhase){ //Ha támadó fázis van és ugrani kell a fázist...
			_skipFirstAttackPhase = false; //Akkor a következõt nem ugrom
			phase.GotoNextPhase();
			return;
		}
		
		if(swapPlayers){ //Ha játékosokat kell cserélni..
			if(PhasePlayer == HumanPlayer){
				PhasePlayer = ComputerPlayer;
			}else{
				PhasePlayer = HumanPlayer;
			}
		}
		
		_phase = phase;
		_phase.OnPhaseActivated(); //Szólok a fázisnak, hogy kezdõdik
		
		_nextPhaseButton.Visible = (_phase.CanShowNextPhaseButton() && PhasePlayer == HumanPlayer); //Kell, hogy látszódjon a következõ fázis gomb?
		
		_frame.Redraw(); //Újra rajzoltatom a képet
		
		if(PhasePlayer == ComputerPlayer){ //Ha a számítógép játszik akkor szólok neki, hogy tegye a dolgát az "AI"
			ComputerPlayer.DoPhase(_phase);
		}
	}
	
	/**
	 * A számítógép játszik?
	 */
	private boolean IsAITurn(){
		return (PhasePlayer == ComputerPlayer);
	}
	
	/**
	 * Visszaadja a másik játékost.
	 */
	public Player GetOtherPlayer(Player player){
		if (player == ComputerPlayer)
			return HumanPlayer;
		
		return ComputerPlayer;
	}
	
	/**
	 * Sebzést számol a játékosra.
	 */
	public void DamagePlayer(Player p, int damage){
		p.Health -= damage;
		if(p.Health <= 0){ //Ha a játékos "meghalt"
			p.Health = 0;
			
			if(!IsAITurn()){ //És nem a számítógép játszik, akkor vége a játéknak
				OnGameOver();	
			}
		}
	}
	
	/**
	 * Vissaadja egy játékos azon kártya helyeit, melyek a pálya része és tartalmaz kártyát
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
	 * Megmondja, hogy egy játékosnak van-e szörny kártyája lehelyezve
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
	 * Megadja, hogy hány plusz ATK pontot kell számolni.
	 */
	public int GetAdditionalATK(CardSlot sourceSlot){
		int add = 0;
		
		Player player = sourceSlot.Owner; //Melyik játékost kell számolni?
		
		for(CardSlot s : _slots){
			if(s.Owner == player && s != sourceSlot && s instanceof CardSlotPlayfield){ //Ha a slot a játékosé és nem a forrás slot és a slot a pálya része
				CardSlotPlayfield pf = (CardSlotPlayfield)s;
				if(pf.MonsterOnly && pf.Card != null){ //ATK csak szörnynek lehet, van-e kártya?
					CardMonster cm = (CardMonster)pf.Card;
					if(cm.Special != null && cm.Special instanceof AddATKAllSpecial){ //Ha van kártya, annak van special-ja és az a special +ATK
						add += ((AddATKAllSpecial)cm.Special).ATK;
					}
				}
			}
		}
		
		return add;
	}
	
	/**
	 * Megadja, hogy hány plusz DEF pontot kell számolni.
	 */
	public int GetAdditionalDEF(CardSlot sourceSlot){
		int add = 0;
		
		Player player = sourceSlot.Owner; //Melyik játékost kell számolni?
		
		for(CardSlot s : _slots){
			if(s.Owner == player && s != sourceSlot && s instanceof CardSlotPlayfield){ //Ha a slot a játékosé és nem a forrás slot és a slot a pálya része
				CardSlotPlayfield pf = (CardSlotPlayfield)s;
				if(pf.MonsterOnly && pf.Card != null){ //DEF csak szörnynek lehet, van-e kártya?
					CardMonster cm = (CardMonster)pf.Card;
					if(cm.Special != null && cm.Special instanceof AddDEFAllSpecial){ //Ha van kártya, annak van special-ja és az a special +DEF
						add += ((AddDEFAllSpecial)cm.Special).DEF;
					}
				}
			}
		}
		
		return add;
	}
	
	/**
	 * Kirajzolja a képernyõre a játékot.
	 */
	public void Draw(Graphics g){
		g.drawImage(Art.Background, 0, 0, _backgroundSize.X, _backgroundSize.Y, null); //A háttér

		for(CardSlot s : _slots){ //Kirajzolok minden slot-ot
			s.Draw(g, HumanPlayer);
		}
		
		HumanPlayer.HandCardManager.Draw(g); //Az emberi játékos kezében lévõ kártyák plusz információi
		
		g.setColor(Color.white); //Fehér szín
		g.setFont(new Font("Arial", Font.BOLD, 24)); //24-es félkövér Arial

		//Játékos életerõk és játékos megnevezések
		DrawCenteredText(g, "Életerõ: " + String.valueOf(ComputerPlayer.Health), _leftColumnCenterX, _playerAHPY);
		DrawCenteredText(g, "Életerõ: " + String.valueOf(HumanPlayer.Health), _leftColumnCenterX, _playerBHPY);

		DrawCenteredText(g, "Számítógép", _leftColumnCenterX, _computerPlayerY);
		DrawCenteredText(g, "Játékos", _leftColumnCenterX, _playerY);
	
		//Ha van aktív fázis (menüben nincs)
		if(_phase != null){
			//Fázissal kapcsolatos információk
			DrawCenteredText(g, (PhasePlayer == HumanPlayer) ? "A te köröd" : "Ellenfél köre", _rightColumnCenterX, (CardSlot.Height / 4) * 1);
			DrawCenteredText(g, "Fázis:", _rightColumnCenterX, CardSlot.Height / 2);
			DrawCenteredText(g, _phase.Name, _rightColumnCenterX, (CardSlot.Height / 4) * 3);
		}
		
		if(_doInspectCard){ //Kártya nézegetõ kell?
			Card inspectedCard = _cardSlotToInspect.Card;
			if(inspectedCard == null){ //Ha nincs mit nézni (megszünt a nézegetés közbe)
				_doInspectCard = false;
			}
			else{
				g.setFont(new Font("Arial", Font.BOLD, 20)); //20-as félkövér Arial
				FontMetrics font = g.getFontMetrics(); //A betûtipus méretei
				
				//Kirajzolom a megfigyelt kártyát
				g.drawImage(inspectedCard.FrontImage, _cardInspector.X, _cardInspector.Y, _cardInspector.Width, _cardInspector.Height, null);
	
				int baseTextY = (int)(_cardInspector.Y + _cardInspector.Height * 1.1f); //A szöveg kezdõ Y-ja
				DrawCenteredText(g, inspectedCard.Name, _cardInspector.X + (_cardInspector.Width / 2), baseTextY); //A kártya neve középre igazítva
				
				g.setFont(new Font("Arial", Font.BOLD, 16)); //16-os félkövér Arial
				
				if( inspectedCard instanceof CardMonster){ //Ha szörnykártyát nézek
					CardMonster monster = (CardMonster)inspectedCard;
					
					//Eltárolom a plusz ATK és DEF értékeket
					int addATK = GetAdditionalATK(_cardSlotToInspect);
					int addDEF = GetAdditionalDEF(_cardSlotToInspect);
					
					//ATK és DEF információ
					String monsterData = "ATK: " + String.valueOf(monster.Attack + addATK);
					if(addATK != 0) //Csillaggal jelzem, ha az érték nõ más kártya miatt (átmenetileg)
						monsterData += "*";
					monsterData += " / DEF: " + String.valueOf(monster.Defense + addDEF);
					if(addDEF != 0)
						monsterData += "*";
					
					DrawCenteredText(g, monsterData, _cardInspector.X + (_cardInspector.Width / 2), baseTextY + font.getAscent()); //1 sorral lejjebb
					
					if(_isInspectedCardPlaced){ //Ha a kártya a pályán van, kiírom, hogy milyen állípotban van
						DrawCenteredText(g, inspectedCard.IsRotated ? "Védekezõ" : "Támadó", _cardInspector.X + (_cardInspector.Width / 2), 
								baseTextY + font.getAscent() * 2); //2 sorral lejjebb
						
					}
					
					if(monster.Special != null){ //Ha van speciális képesség megjelenítem azt is
						DrawInfoBox(g, "Speciális képesség: " + monster.Special.GetDescription());
					}
				}else if(inspectedCard instanceof CardMagic){ //Ha varázskártyáról van szó
					DrawInfoBox(g, ((CardMagic)inspectedCard).Effect.GetDescription()); //Megjelenítem a kártya képességének leírását
				}
			}
		}
		
		_nextPhaseButton.Draw(g); //Kirajzolom a következõ fázis gombot (ez külön van a többitõl)
		
		if(_paused){ //Ha van menü
			//Rajzolok egy félig átlátszó fekete téglalapot a játékra
			g.setColor(new Color(0, 0, 0, 150));	
			g.fillRect(0, 0, _frame.getWidth(), _frame.getHeight());
					
			//Kirajzolom a logót
			g.drawImage(Art.Logo, _logoArea.X, _logoArea.Y, _logoArea.Width, _logoArea.Height, null);
		
			for(UIButton b : _buttonList){ //Kirajzolok minden gombot ami menü gomb
				if (b instanceof UIButtonMenu) {
					b.Draw(g);
				}
			}
			
			if(_gameOver){ //Ha a játék véget ért
				g.setFont(new Font("Arial", Font.BOLD, 48)); //48-as félkövér Arial
				g.setColor(Color.white); //Fehér
				DrawCenteredText(g, _gameOverText, _backgroundSize.X / 2, (_backgroundSize.Y / 6) * 2); //Középre igazított szöveg
			}
		}
	}
	
	/**
	 * Kiszámolja az x. kártya helyét a sorban.
	 */
	private int GetCardCenterX(int x){
		return x - (CardSlot.Width / 2);
	}
	
	/**
	 * Kirajzolja a speciális kártya infót.
	 */
	private void DrawInfoBox(Graphics g, String text){
		g.setColor(new Color(0,0,0,175)); //Félig átlátszó fekete téglalap
		g.fillRect(_cardInfoRectangle.X, _cardInfoRectangle.Y, _cardInfoRectangle.Width, _cardInfoRectangle.Height);
		
		g.setColor(Color.white);
		DrawCenteredTextIntoArea(g, text, new Rect(
				_cardInfoRectangle.X + 10, _cardInfoRectangle.Y + 10, _cardInfoRectangle.Width - 20, _cardInfoRectangle.Height - 20));
	}
	
	/**
	 * Kitölt középre igazított szöveggel egy téglalapot
	 */
	public static void DrawCenteredTextIntoArea(Graphics g, String text, Rect area){
		FontMetrics m = g.getFontMetrics(); //Betûtipus infók
		
		//Szétválasztom a szöveget sorokra, minden sor pont belefér a téglalap szélességébe
		ArrayList<String> lines = new ArrayList<String>();
		
		String line = ""; //Egy átmeneti sor
		int lineW = 0; //A sor szélessége
		
		String[] parts = text.split(" "); //Szétválasztom a szöveget szavakra.
		for(int x = 0; x < parts.length; x++){
			int partW = m.stringWidth(parts[x] + " "); //Megnézem egy szó + egy szóköz szélességét
			if(lineW + partW < area.Width){ //Ha ezt hozzárakom a sorhoz és belefér a sorba..
				line += (line.length() == 0 ? "" : " ") + parts[x]; //Hozzáadom a szöveget a sorhoz, ha a sorba már van szöveg rakok elé egy szóközt is
				lineW += partW;
			}else{ //Ha nem fér bele a sorba új sort kezdek
				lines.add(line);
				line = parts[x];
				lineW = 0;
			}
		}
		
		if(line.length() > 0){ //Ha maradt valami az csak új sorba mehet
			lines.add(line);
		}
		
		int drawY = area.Y + area.Height / 2 - ((lines.size() / 2) * (m.getAscent())); //A sorok száma és egy sor magassága alapján kiszámolom, hogy honann kezdjem a rajzolást Y-ban
		
		for(int x = 0;x<lines.size();x++){
			DrawCenteredText(g, lines.get(x), area.X + area.Width / 2, drawY + m.getAscent() * x); //Középre igazított sor rajzolás
		}
	}
	
	/**
	 * Középre igazítva rajzol egy sor szöveget.
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
	 * Jelzi a UI panelnak hogy újra kell rajzodnia, mert valami megváltozott
	 */
	public void RedrawFrame(){
		_frame.Redraw();
	}
	
	/**
	 * A kártya nézegetõ frissítése.
	 */
	public void UpdateInspectedSlot(){
		//Megkeresem, hogy melyik slot-on van az egér amit az emberi játékos megnézhet
		CardSlot inspectedSlot = null;
		for(CardSlot s : _slots){	
			if(s.IsInBounds(_lastMousePosition) && (s.Owner == HumanPlayer || s instanceof CardSlotPlayfield)){
				inspectedSlot = s;
				break;
			}
		}
		
		if(_doInspectCard){ //Ha már nézek kártyát
			if(inspectedSlot == null){ //Az egér nincs már kártyán
				_doInspectCard = false;
				_cardSlotToInspect = null;
				_frame.Redraw();
			}else{ //Az egér másik kártyán van
				_cardSlotToInspect = inspectedSlot;
				_doInspectCard = (inspectedSlot.Card != null);
				_isInspectedCardPlaced = (inspectedSlot instanceof CardSlotPlayfield);
				_frame.Redraw();	
			}
		}else{ //Ha nem nézek kártyán
			if(inspectedSlot != null){ //De az egér kártyán van
				_cardSlotToInspect = inspectedSlot;
				_doInspectCard = (inspectedSlot.Card != null);
				_isInspectedCardPlaced = (inspectedSlot instanceof CardSlotPlayfield);
				_frame.Redraw();
			}
		}
	}
	
	/**
	 * Egér mozgási esemény.
	 */
	public void OnMouseMove(int x, int y){
		if(_lastMousePosition.X != x || _lastMousePosition.Y != y){
			_lastMousePosition.X = x;
			_lastMousePosition.Y = y;
		
			if(!_gameOver && !_paused){ //Ha nincs vége a játéknak és nem vagyok menübe
				UpdateInspectedSlot(); //Frissíteni kell a kártya nézegetõt
			}
			
			for(UIButton b : _buttonList){ //Végig megyek minden gombon amire kattitani lehet és frissítem a kinézetét
				if((b instanceof UIButtonMenu && _paused) || (!(b instanceof UIButtonMenu) && !_paused)){
					if(b.OnMouseMove(x, y)){
						_frame.Redraw();
					}	
				}
			}
		}
	}
	
	/**
	 * Egér kattintási esemény.
	 */
	public void OnMouseClick(int x, int y){
		boolean change = false; //Változott valami?

		if(!_gameOver && !_paused){
			if(PhasePlayer == HumanPlayer){ //Ha az emberi játékos köre van akkor megnézem, hogy slot-ra kattintott-e
				for(CardSlot s : _slots){
					if(s.IsInBounds(x, y)){
						_phase.OnSlotClick(s); //Ha igen akkor szólok a fázisnak, hogy kezdjen vele valamit
					
						change = true;
						break;
					}
				}
				if(!change){ //Kattintás volt, de nem slot-ra
					_phase.OnSlotClick(null);
				}
			}
			
			if(HumanPlayer.HandCardManager.OnClick(x, y)){ //Kézre kattintott?
				change = true;
			}
		}
		
		if(change){
			_frame.Redraw();
		}else{
			for(UIButton b : _buttonList){ //Ha nem slot-ra volt kattintás, lehet, hogy gombra volt
				if((b instanceof UIButtonMenu && _paused) || (!(b instanceof UIButtonMenu) && !_paused)){
					b.OnMouseClick(x, y);
				}
			}
		}
	}
	
	/**
	 * UI gomb kattintási esemény.
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
	 * Elmenti a játékot egy fájlba.
	 */
	private void SaveGame() {
		//Nyitok egy fájl mentés ablakot ami sav fájolkat akar menteni a felhasználó mappájába
		JFileChooser saveDialog = new JFileChooser();
		saveDialog.setFileFilter(new FileNameExtensionFilter("YuGiOh save files", "sav"));
		saveDialog.setCurrentDirectory(new File(System.getProperty("user.home")));
		if(saveDialog.showSaveDialog(_frame) == JFileChooser.APPROVE_OPTION){
			File file = saveDialog.getSelectedFile();
			if(!file.toString().toLowerCase().endsWith("sav")){ //Ha hiányzik a kiterjesztés
				file = new File(file.toString() + ".sav");
			}
			
			DataOutputStream data = null;
			try{
				data = new DataOutputStream(new FileOutputStream(file));
				
				data.writeUTF("YuGiOh"); //Mentés fejléce
				
				//Elmentem a kártya slot-okat, darabszámát, indexét, tartalmát, állapotát
				data.writeInt(_slots.size());
				for(int x = 0;x<_slots.size();x++){
					CardSlot slot = _slots.get(x);
					data.writeInt(x);
					if(slot instanceof CardSlotPlayfield){ //Csak a pálya kártyáit mentem el
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
				
				//A játékosok életét
				data.writeInt(HumanPlayer.Health);
				data.writeInt(ComputerPlayer.Health);
				
				//Azt, hogy ki játszik
				data.writeBoolean(PhasePlayer == HumanPlayer);
				
				//A jelenlegi fázist is
				if(_phase == PhaseCardPick){
					data.writeByte(1);
				}else if(_phase == PhaseTactics){
					data.writeByte(2);
				}if(_phase == PhaseAttack){
					data.writeByte(3);
				}
				
				//És azt hogy az elsõ támadó fázison túl jutott-e a játék
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
	 * Elment egy tömbnyi káryát.
	 */
	private void WriteCardArray(DataOutputStream data, ArrayList<Card> cards) throws IOException{
		data.writeInt(cards.size());
		for(int x = 0;x<cards.size();x++){
			data.writeInt(cards.get(x).SaveUID);
		}
	}
	
	/**
	 * Betölt egy mentést és folytatja onnan a játékot.
	 */
	private void LoadGame(){
		//Nyitok egy fájl betöltés ablakot.
		JFileChooser openDialog = new JFileChooser();
		openDialog.setFileFilter(new FileNameExtensionFilter("YuGiOh save files", "sav"));
		openDialog.setCurrentDirectory(new File(System.getProperty("user.home")));
		if(openDialog.showOpenDialog(_frame) == JFileChooser.APPROVE_OPTION){
			DataInputStream data = null;
			try{
				data = new DataInputStream(new FileInputStream(openDialog.getSelectedFile()));
				
				String header = data.readUTF();
				if(!header.equalsIgnoreCase("YuGiOh")){ //Ellenörzöm a fejléc helyességét
					throw new Exception("Hibás fejléc!");
				}
				
				//Elkezdem betölteni a kártya slot információkat
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
				
				//Betöltöm a paklikat is
				ArrayList<Card> tempList = new ArrayList<Card>();
				ReadCardList(data, tempList);
				ComputerPlayer.AddArrayOfCards(tempList);
				ReadCardList(data, tempList);
				ComputerPlayer.InitCards(tempList, false);
								
				ReadCardList(data, tempList);
				HumanPlayer.AddArrayOfCards(tempList);
				ReadCardList(data, tempList);
				HumanPlayer.InitCards(tempList, false);
				
				//A kéz manager-t újra kell indítani
				ComputerPlayer.HandCardManager.ResetOffset();
				HumanPlayer.HandCardManager.ResetOffset();
				
				//Betöltöm a játékosok életét
				HumanPlayer.Health = data.readInt();
				ComputerPlayer.Health = data.readInt();
				
				if(data.readBoolean()){ //Azt, hogy ki játszik éppen
					PhasePlayer = HumanPlayer;
				}else{
					PhasePlayer = ComputerPlayer;
				}
				
				switch(data.readByte()){ //A fázist
				case 1:
					SetPhase(PhaseCardPick, false);
					break;
				case 2:
					SetPhase(PhaseTactics, false);
					break;
				case 3:
					SetPhase(PhaseAttack, false);
					
					for(int x = 0;x<usedStates.length;x++){ //Ha támadó fázis van akkor kell az is hogy melyik szörny volt már használva
						CardSlot s = _slots.get(x);
						if(s instanceof CardSlotPlayfield){
							((CardSlotPlayfield)s).Used = usedStates[x];
						}
					}
					break;
				}
				
				_skipFirstAttackPhase = data.readBoolean();
				
				data.close();
				
				//Beállítom a játék állapotokat
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
	 * Betölt egy tombnyi kártyát.
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
	 * A slot kijelöléseket alaphelyzetbe állítja.
	 */
	public void ResetSlotHighlight(){
		for(int x = 0;x<_slots.size();x++){
			_slots.get(x).IsHighlighted = true;
		}
	}
	
	/**
	 * A slot kijelöléseket kártya lehelyezése módba állítja.
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
	 * A kártya slot kijelöléseket lehetséges támadás célpontjaira állítja.
	 */
	public void SetAttackSlotHighlight(CardSlot sourceSlot){
		Player player = sourceSlot.Owner;
		for(CardSlot s : _slots){
			s.IsHighlighted = (s == sourceSlot || (s.Owner != player && s instanceof CardSlotPlayfield && ((CardSlotPlayfield)s).MonsterOnly && s.Card != null));
		}
	}
	
	/**
	 * A slot kijelöléseket szörnyekre állítja.
	 */
	public void SetMagicActivateSlotHighlight(CardSlot sourceSlot){
		Player player = sourceSlot.Owner;
		for(CardSlot s : _slots){
			s.IsHighlighted = (s.Owner == player && s instanceof CardSlotPlayfield && ((CardSlotPlayfield)s).MonsterOnly && s.Card != null);
		}
	}
	
	/**
	 * A slot kijelöléseket 2db slot alapján állítja be.
	 */
	public void SetBotSlotHighlight(CardSlot a, CardSlot b){
		for(CardSlot s : _slots){
			s.IsHighlighted = (s == a || s == b);
		}
	}
	
	/**
	 * A használt slotokat újra aktiválja.
	 */
	public void ResetSlotUsedStates(){
		for(CardSlot s : _slots){
			if(s instanceof CardSlotPlayfield){
				((CardSlotPlayfield)s).Used = false;
			}
		}
	}
	
	/**
	 * Ki és be kapcsolja a menüt.
	 */
	public void TogglePauseMenu(){
		if(!_gameOver && _gameRunning && !IsAITurn()){
			_paused = !_paused;
		
			UpdateMenu();
			
			RedrawFrame();
		}
	}
	
	/**
	 * A menü gombok állapotát állítja be a játék állapota alapján.
	 */
	private void UpdateMenu() {
		_exitGameButtonType1.Visible = !_gameRunning;
		
		_exitGameButtonType2.Visible = _saveGameButton.Visible = _gameRunning;
	}
}
