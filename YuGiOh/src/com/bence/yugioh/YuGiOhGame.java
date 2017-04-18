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
	public ComputerPlayer ComputerPlayer; //A felso, szamitogep jatekos
	public Player HumanPlayer; //Az also, emberi jatekos
	public Player PhasePlayer; //Az aktiv jatekos aki eppen a koret jatsza
	
	private ArrayList<CardSlot> _slots; //A palyan levo osszes kartya hely
	
	private GameFrame _frame; //A jatekot megjelenito komponens
	
	private Point2 _lastMousePosition; //Az utolso ismert eger pozicio
	private Point2 _backgroundSize; //A hatter (kepernyo) merete
	
	//Kartya nezegeto beallitasai
	private boolean _doInspectCard; //Kell nezni kartyat?
	private boolean _isInspectedCardPlaced; //A vizsgalt kartya a palyan van?
	private CardSlot _cardSlotToInspect; //A vizsgalt kartya
	private Rect _cardInspector; //A kartya vizsgalo helye a kepernyon
	private Rect _cardInfoRectangle; //A specialis kartya informacio helye
	
	//Grafika
	private int _playerAHPY; //A felso jatekos eleterejenek Y helye
	private int _playerBHPY; //Az also jatekos eleterejenek Y helye
	
	private int _leftColumnCenterX; //A bal oszlop X kozepe
	private int _rightColumnCenterX; //A jobb oszlop X kozepe
	private int _computerPlayerY; //A szamitogepes jatekos felirat helye
	private int _playerY; //Az emberi jatekos felirat helye
	
	private Rect _logoArea; //A menuben a YuGiOh logo helye
	
	//Jatek fazisok
	private boolean _skipFirstAttackPhase; //Az elso tamado fazist at kell ugrani?
	private GamePhase _phase; //Az aktiv fazis
	public GamePhase PhaseCardPick; //Kartya felvetel fazisa
	public GamePhase PhaseTactics; //Taktikai fazis
	public GamePhase PhaseAttack; //Tamado fatis
	
	//Jatek allapot
	private boolean _paused; //A jatek szunetel?
	private boolean _gameRunning; //Fut a jatek?
	
	private boolean _gameOver; //A jateknak vege?
	private String _gameOverText; //A nyertes szoveg
	
	//UI
	private ArrayList<UIButton> _buttonList; //Lista a Ui gombokrol
	private UIButton _nextPhaseButton;
	private UIButton _saveGameButton;
	private UIButton _exitGameButtonType1;
	private UIButton _exitGameButtonType2;
	
	/**
	 * Letrehoz egy YuGiOh jatekot
	 * @param w A kepernyo szelessege.
	 * @param h A kepernyo magassaga.
	 * @param f A jatekot tartalmazo UI komponens.
	 */
	public YuGiOhGame(int w, int h, GameFrame f){
		_frame = f;
		_backgroundSize = new Point2(w,h);
		_lastMousePosition = new Point2(0, 0);
		
		//Fazisok elkeszitese
		PhaseCardPick = new CardPickPhase(this);
		PhaseTactics = new TacticsPhase(this);
		PhaseAttack = new AttackPhase(this);
		
		//Jatekosok elkeszitese
		ComputerPlayer = new ComputerPlayer(this);
		HumanPlayer = new Player(this);
		
		//Kiszamolom a kartya mereteket
		int sideHeight = h / 2; //Terfel merete
		float paddingY = sideHeight / 3; //Egy kartya sor merete
		float cardSize = (paddingY * 0.90f); //A terfelen 3 sor kartya van (minden sor 90% magassagu, igy van hely koztuk)
		float cardRatio = (float)Art.CardSlot_Hand.getWidth(null) / (float)Art.CardSlot_Hand.getHeight(null); //Kiszamolom egy kartya mereteinek aranyat
		
		CardSlot.Width = (int)(cardSize * cardRatio); //Kiszamolom a kartya szelesseget
		CardSlot.Height = (int)(cardSize);
		
		//Elkezdem elkesziteni a palyat
		_slots = new ArrayList<CardSlot>();
		
		int leftColumn = (w / 5); //A bal oldali oszlop merete
		
		_leftColumnCenterX = (int)(leftColumn / 2.0); //A bal oldali oszlop kozepenek szamitasa
		
		//Elhelyezem a ket paklit a palyan, ez egyik felulre kerul, a masik lentre
		_slots.add(new CardSlotStack(ComputerPlayer, GetCardCenterX(leftColumn / 2), (int)(paddingY / 2.0f)));
		_slots.add(new CardSlotStack(HumanPlayer, GetCardCenterX(leftColumn / 2), (int)(h - CardSlot.Height - paddingY / 2.0f)));
		
		//Kiszamolom a jatekos megnevezesek helyet
		_computerPlayerY = (int)(paddingY / 4.0f);
		_playerY = (int)(h - paddingY / 4.0f);
				
		//Kiszamolom a jatekos eletek helyet
		_playerAHPY = (int)(paddingY * 2.0f);
		_playerBHPY = (int)(h - paddingY * 2.0f);
		
		//A kozepso, fo oszlop elokeszietse
		float paddingSize = CardSlot.Height; //Eltolas merete 

		//Ket tomb mely azokat a kartya helyeket tartalmazza ahonann kartyat lehet majd helyezni a palyara 
		ArrayList<CardSlotHand> playerAHand = new ArrayList<CardSlotHand>();
		ArrayList<CardSlotHand> playerBHand = new ArrayList<CardSlotHand>();
		
		CardSlotHand temp = null; //atmeneti valtozo
		
		float rightX = leftColumn + (CardSlot.Width / 2.0f); //Az elso kartya helye, ez a bal olszlop merete + egy fel kartya szelesseg
		for (int x = 0; x < 5; x++){ //5db kartya lehet egy sorban
			//Szamitogepes jatekos:
			temp = new CardSlotHand(ComputerPlayer, GetCardCenterX((int)(rightX + paddingSize * x)), 0); //Elso sor x. kartya
			
			//Ezt elrakom az elso jatekos atmeneti tarolojaba es a kartya helyek koze is
			playerAHand.add(temp);
			_slots.add(temp);
			
			_slots.add(new CardSlotPlayfield(ComputerPlayer, GetCardCenterX((int)(rightX + paddingSize * x)), (int)paddingY, false)); //2. sor x. kartya, ide csak varazskartyakat lehet rakni
			_slots.add(new CardSlotPlayfield(ComputerPlayer, GetCardCenterX((int)(rightX + paddingSize * x)), (int)(paddingY * 1.915f), true)); //3. sor x. kartya, csak szorny kartyak
			
			//Emberi jatekos:
			temp = new CardSlotHand(HumanPlayer, GetCardCenterX((int)(rightX + paddingSize * x)), (int)(h - paddingY)); //6. sor x. kartya, emberi jatekos "keze"
			
			playerBHand.add(temp);
			_slots.add(temp);
			
			_slots.add(new CardSlotPlayfield(HumanPlayer, GetCardCenterX((int)(rightX + paddingSize * x)), (int)(h - paddingY * 2.0f), false)); //5. sor x. kartya, csak varazskartyak
			_slots.add(new CardSlotPlayfield(HumanPlayer, GetCardCenterX((int)(rightX + paddingSize * x)), (int)(h - paddingY * 2.915f), true)); //4. sor x. kartya, csak szornykartyak
		}
		
		//Az elozoleg atmenetileg eltarolt "kez" kartya helyek alapjan elkeszitem a jatekosok "kez" kezelojet
		ComputerPlayer.HandCardManager = new HandCardManager(ComputerPlayer, playerAHand);
		HumanPlayer.HandCardManager = new HandCardManager(HumanPlayer, playerBHand);
		
		//Az eddigi osszes helyet vegig nezve megkeresem azokat, amelyek a szamitogepes jatekoshoz tartoznak es atadom az AI-nak
		ArrayList<CardSlot> cpuSlots = new ArrayList<CardSlot>();
		for(CardSlot s : _slots){
			if(s.Owner == ComputerPlayer){
				cpuSlots.add(s);
			}
		}
		ComputerPlayer.InitSlots(cpuSlots);
		
		//A jobb oldali oszlop merete es helye
		float right2X = w - (w / 5);
		float right2W = w / 5;
		
		_rightColumnCenterX = (int)(right2X + right2W / 2.0);
		
		//A kartya nezegeto elokeszitese
		float iwidth = right2W * 0.75f; //A nezegeto szelessege az oszlop 75%-a
		float iheight = (((float)CardSlot.Height / (float)CardSlot.Width) * iwidth); //A magassagat egy kartya forditott meret aranyabol es a nezoke magassagabol szamolom
		_cardInspector = new Rect((int)(right2X + (right2W / 2.0f) - (iwidth / 2.0f)), (int)(h - iheight * 1.5f), (int)iwidth, (int)iheight); //A kiszamolt mereteket egy teglalapba rendezem
		_doInspectCard = false; //Nem nezek kartyan
		_cardInfoRectangle = new Rect(playerBHand.get(0).X - CardSlot.Width / 2, playerBHand.get(0).Y + CardSlot.Height / 2, 
				playerBHand.get(playerBHand.size() - 1).X - playerBHand.get(0).X + CardSlot.Width * 2, CardSlot.Height / 2); //Az emberi jatekos kez helye alapjan szamolok egy teglalapot mely a kartya leirast tartalmazza
		
		//UI gombok elokeszitese
		_buttonList = new ArrayList<UIButton>();
		
		//A kovetkezo fazis gomb a jobb oldali oszlop kozepebe rakom ugy, hogy az az oszlop szelessegenek 75%-a
		_buttonList.add(_nextPhaseButton = new UIButton(this, Texts.NextPhaseText, (int)(right2X + (right2W / 2.0f) - (right2W * 0.75f * 0.5f)), (int)paddingY, (int)(right2W * 0.75f), (int)(CardSlot.Height * 0.25f), ButtonAction.NextPhase));
		_nextPhaseButton.Visible = false; //A gomb alapbol nem kell, hogy latszodjon
		
		//A jateknak nincs vege, nem fut es meg van allitva (igy van menu, ezek kellenek az UpdateMenu-hoz)
		_paused = true;
		_gameOver = false;
		_gameRunning = false;
		
		//A menu gombjainak elokeszietse
		{
			float logoAspectRatio = ((float)Art.Logo.getHeight(null) / (float)Art.Logo.getWidth(null)); //A logo meretaranya
			
			int logoWidth = w / 2; //A logo szelessege a kep szelessegenek fele legyen
			int logoHeight = (int)(logoAspectRatio * logoWidth);
			_logoArea = new Rect((w / 2) - (logoWidth / 2), (h / 6) - (logoHeight / 2), logoWidth, logoHeight);
			
			//Gombok merete es kezdo X,Y helye
			int buttonsWidth = logoWidth / 2;
			int buttonsHeight = 50;
			int buttonsX = ((w / 2) - (buttonsWidth / 2));
			int buttonsY = (h / 10) * 4;
			
			//Egymas ala rakom a gombokat
			_buttonList.add(new UIButtonMenu(this, Texts.NewGameText, buttonsX, buttonsY, buttonsWidth, buttonsHeight, ButtonAction.NewGame));
			_buttonList.add(new UIButtonMenu(this, Texts.LoadGameText, buttonsX, (int)(buttonsY + buttonsHeight * 1.5f), buttonsWidth, buttonsHeight, ButtonAction.Load));
			
			//Ez a harom gomb trukkos, mert a jatek allapotatol fuggoen kell megjelenniuk, igy ket gomb egymason van
			_buttonList.add(_exitGameButtonType1 = new UIButtonMenu(this, Texts.ExitGameText, buttonsX, buttonsY + buttonsHeight * 3, buttonsWidth, buttonsHeight, ButtonAction.Exit));
			_buttonList.add(_saveGameButton = new UIButtonMenu(this, Texts.SaveGameText, buttonsX, buttonsY + buttonsHeight * 3, buttonsWidth, buttonsHeight, ButtonAction.Save));
			_buttonList.add(_exitGameButtonType2 = new UIButtonMenu(this, Texts.ExitGameText, buttonsX, (int)(buttonsY + buttonsHeight * 4.5f), buttonsWidth, buttonsHeight, ButtonAction.Exit));
			
			//Meghivom az UpdateMenu fuggvenyt, hogy a jo gombok jelenjenek meg
			UpdateMenu();
		}
	}
	
	/**
	 * Elindit egy uj jatekot
	 */
	private void StartGame(){		
		for(CardSlot s : _slots){ //Torlom a kartyakat a palyarol
			s.Card = null;
		}
		
		_skipFirstAttackPhase = true; //Az elso tamado fazist ugrani kell
		
		ComputerPlayer.Health = 4000;
		HumanPlayer.Health = 4000;
		
		int deckSize = 30; //Hard Code-olt pakli meret
		//Keszitek paklit a 2 jatekosnak es atadom azt is, hogy ez egy kezdo pakli
		HumanPlayer.InitCards(AllCards.CreateDeck(deckSize), true);
		ComputerPlayer.InitCards(AllCards.CreateDeck(deckSize), true);
		
		//Alaphelyzetbe allitom a kez kartya manager-t
		ComputerPlayer.HandCardManager.ResetOffset();
		HumanPlayer.HandCardManager.ResetOffset();
		
		//Az emberi jatekos kezd
		PhasePlayer = HumanPlayer;
		
		//Nincs vege a jateknak, nincs megallitva es fut
		_gameOver = false;
		_paused = false;
		_gameRunning = true;
		
		SetPhase(PhaseCardPick, false); //Kartya huzassal kezdodik a jatek es nincs szukseg jatekos cserere
	}
	
	/**
	 * Jatek vege.
	 */
	public void OnGameOver(){
		_gameOverText = (ComputerPlayer.Health > 0 ? Texts.YouLostText : Texts.YouWinText); //Megnezem, hogy ki nyert
		
		//Beallitom az allapotokat a menuhoz
		_gameOver = true;
		_paused = true;
		_gameRunning = false;
		
		_nextPhaseButton.Visible = false;
		
		UpdateMenu();
	}
	
	/**
	 * Jatek fazist cserel.
	 */
	public void SetPhase(GamePhase phase, boolean swapPlayers){
		if(phase instanceof AttackPhase && _skipFirstAttackPhase){ //Ha tamado fazis van es ugrani kell a fazist...
			_skipFirstAttackPhase = false; //Akkor a kovetkezot nem ugrom
			phase.GotoNextPhase();
			return;
		}
		
		if(swapPlayers){ //Ha jatekosokat kell cserelni..
			if(PhasePlayer == HumanPlayer){
				PhasePlayer = ComputerPlayer;
			}else{
				PhasePlayer = HumanPlayer;
			}
		}
		
		_phase = phase;
		_phase.OnPhaseActivated(); //Szolok a fazisnak, hogy kezdodik
		
		_nextPhaseButton.Visible = (_phase.CanShowNextPhaseButton() && PhasePlayer == HumanPlayer); //Kell, hogy latszodjon a kovetkezo fazis gomb?
		
		_frame.Redraw(); //ujra rajzoltatom a kepet
		
		if(PhasePlayer == ComputerPlayer){ //Ha a szamitogep jatszik akkor szolok neki, hogy tegye a dolgat az "AI"
			ComputerPlayer.DoPhase(_phase);
		}
	}
	
	/**
	 * A szamitogep jatszik?
	 */
	private boolean IsAITurn(){
		return (PhasePlayer == ComputerPlayer);
	}
	
	/**
	 * Visszaadja a masik jatekost.
	 */
	public Player GetOtherPlayer(Player player){
		if (player == ComputerPlayer)
			return HumanPlayer;
		
		return ComputerPlayer;
	}
	
	/**
	 * Sebzest szamol a jatekosra.
	 */
	public void DamagePlayer(Player p, int damage){
		p.Health -= damage;
		if(p.Health <= 0){ //Ha a jatekos "meghalt"
			p.Health = 0;
			
			if(!IsAITurn()){ //es nem a szamitogep jatszik, akkor vege a jateknak
				OnGameOver();	
			}
		}
	}
	
	/**
	 * Vissaadja egy jatekos azon kartya helyeit, melyek a palya resze es tartalmaz kartyat
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
	 * Megmondja, hogy egy jatekosnak van-e szorny kartyaja lehelyezve
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
	 * Megadja, hogy hany plusz ATK pontot kell szamolni.
	 */
	public int GetAdditionalATK(CardSlot sourceSlot){
		int add = 0;
		
		Player player = sourceSlot.Owner; //Melyik jatekost kell szamolni?
		
		for(CardSlot s : _slots){
			if(s.Owner == player && s != sourceSlot && s instanceof CardSlotPlayfield){ //Ha a slot a jatekose es nem a forras slot es a slot a palya resze
				CardSlotPlayfield pf = (CardSlotPlayfield)s;
				if(pf.MonsterOnly && pf.Card != null){ //ATK csak szornynek lehet, van-e kartya?
					CardMonster cm = (CardMonster)pf.Card;
					if(cm.Special != null && cm.Special instanceof AddATKAllSpecial){ //Ha van kartya, annak van special-ja es az a special +ATK
						add += ((AddATKAllSpecial)cm.Special).ATK;
					}
				}
			}
		}
		
		return add;
	}
	
	/**
	 * Megadja, hogy hany plusz DEF pontot kell szamolni.
	 */
	public int GetAdditionalDEF(CardSlot sourceSlot){
		int add = 0;
		
		Player player = sourceSlot.Owner; //Melyik jatekost kell szamolni?
		
		for(CardSlot s : _slots){
			if(s.Owner == player && s != sourceSlot && s instanceof CardSlotPlayfield){ //Ha a slot a jatekose es nem a forras slot es a slot a palya resze
				CardSlotPlayfield pf = (CardSlotPlayfield)s;
				if(pf.MonsterOnly && pf.Card != null){ //DEF csak szornynek lehet, van-e kartya?
					CardMonster cm = (CardMonster)pf.Card;
					if(cm.Special != null && cm.Special instanceof AddDEFAllSpecial){ //Ha van kartya, annak van special-ja es az a special +DEF
						add += ((AddDEFAllSpecial)cm.Special).DEF;
					}
				}
			}
		}
		
		return add;
	}
	
	/**
	 * Kirajzolja a kepernyore a jatekot.
	 */
	public void Draw(Graphics g){
		g.drawImage(Art.Background, 0, 0, _backgroundSize.X, _backgroundSize.Y, null); //A hatter

		for(CardSlot s : _slots){ //Kirajzolok minden slot-ot
			s.Draw(g, HumanPlayer);
		}
		
		HumanPlayer.HandCardManager.Draw(g); //Az emberi jatekos kezeben levo kartyak plusz informacioi
		
		g.setColor(Color.white); //Feher szin
		g.setFont(new Font("Arial", Font.BOLD, 24)); //24-es felkover Arial

		//Jatekos eleterok es jatekos megnevezesek
		DrawCenteredText(g, Texts.HealthText + " " + String.valueOf(ComputerPlayer.Health), _leftColumnCenterX, _playerAHPY);
		DrawCenteredText(g, Texts.HealthText + " " + String.valueOf(HumanPlayer.Health), _leftColumnCenterX, _playerBHPY);

		DrawCenteredText(g, Texts.ComputerText, _leftColumnCenterX, _computerPlayerY);
		DrawCenteredText(g, Texts.PlayerText, _leftColumnCenterX, _playerY);
	
		//Ha van aktiv fazis (menuben nincs)
		if(_phase != null){
			//Fazissal kapcsolatos informaciok
			DrawCenteredText(g, (PhasePlayer == HumanPlayer) ? Texts.YourRoundText : Texts.EnemyRoundText, _rightColumnCenterX, (CardSlot.Height / 4) * 1);
			DrawCenteredText(g, Texts.PhaseText, _rightColumnCenterX, CardSlot.Height / 2);
			DrawCenteredText(g, _phase.Name, _rightColumnCenterX, (CardSlot.Height / 4) * 3);
		}
		
		if(_doInspectCard){ //Kartya nezegeto kell?
			Card inspectedCard = _cardSlotToInspect.Card;
			if(inspectedCard == null){ //Ha nincs mit nezni (megszunt a nezegetes kozbe)
				_doInspectCard = false;
			}
			else{
				g.setFont(new Font("Arial", Font.BOLD, 20)); //20-as felkover Arial
				FontMetrics font = g.getFontMetrics(); //A betutipus meretei
				
				//Kirajzolom a megfigyelt kartyat
				g.drawImage(inspectedCard.FrontImage, _cardInspector.X, _cardInspector.Y, _cardInspector.Width, _cardInspector.Height, null);
	
				int baseTextY = (int)(_cardInspector.Y + _cardInspector.Height * 1.1f); //A szoveg kezdo Y-ja
				DrawCenteredText(g, inspectedCard.Name, _cardInspector.X + (_cardInspector.Width / 2), baseTextY); //A kartya neve kozepre igazitva
				
				g.setFont(new Font("Arial", Font.BOLD, 16)); //16-os felkover Arial
				
				if( inspectedCard instanceof CardMonster){ //Ha szornykartyat nezek
					CardMonster monster = (CardMonster)inspectedCard;
					
					//Eltarolom a plusz ATK es DEF ertekeket
					int addATK = GetAdditionalATK(_cardSlotToInspect);
					int addDEF = GetAdditionalDEF(_cardSlotToInspect);
					
					//ATK es DEF informacio
					String monsterData = "ATK: " + String.valueOf(monster.Attack + addATK);
					if(addATK != 0) //Csillaggal jelzem, ha az ertek no mas kartya miatt (atmenetileg)
						monsterData += "*";
					monsterData += " / DEF: " + String.valueOf(monster.Defense + addDEF);
					if(addDEF != 0)
						monsterData += "*";
					
					DrawCenteredText(g, monsterData, _cardInspector.X + (_cardInspector.Width / 2), baseTextY + font.getAscent()); //1 sorral lejjebb
					
					if(_isInspectedCardPlaced){ //Ha a kartya a palyan van, kiirom, hogy milyen allipotban van
						DrawCenteredText(g, inspectedCard.IsRotated ? Texts.DefenseModeText : Texts.AttackModeText, _cardInspector.X + (_cardInspector.Width / 2), 
								baseTextY + font.getAscent() * 2); //2 sorral lejjebb
						
					}
					
					if(monster.Special != null){ //Ha van specialis kepesseg megjelenitem azt is
						DrawInfoBox(g, Texts.SpecialText + " " + monster.Special.GetDescription());
					}
				}else if(inspectedCard instanceof CardMagic){ //Ha varazskartyarol van szo
					DrawInfoBox(g, ((CardMagic)inspectedCard).Effect.GetDescription()); //Megjelenitem a kartya kepessegenek leirasat
				}
			}
		}
		
		_nextPhaseButton.Draw(g); //Kirajzolom a kovetkezo fazis gombot (ez kulon van a tobbitol)
		
		if(_paused){ //Ha van menu
			//Rajzolok egy felig atlatszo fekete teglalapot a jatekra
			g.setColor(new Color(0, 0, 0, 150));	
			g.fillRect(0, 0, _frame.getWidth(), _frame.getHeight());
					
			//Kirajzolom a logot
			g.drawImage(Art.Logo, _logoArea.X, _logoArea.Y, _logoArea.Width, _logoArea.Height, null);
		
			for(UIButton b : _buttonList){ //Kirajzolok minden gombot ami menu gomb
				if (b instanceof UIButtonMenu) {
					b.Draw(g);
				}
			}
			
			if(_gameOver){ //Ha a jatek veget ert
				g.setFont(new Font("Arial", Font.BOLD, 48)); //48-as felkover Arial
				g.setColor(Color.white); //Feher
				DrawCenteredText(g, _gameOverText, _backgroundSize.X / 2, (_backgroundSize.Y / 6) * 2); //Kozepre igazitott szoveg
			}
		}
	}
	
	/**
	 * Kiszamolja az x. kartya helyet a sorban.
	 */
	private int GetCardCenterX(int x){
		return x - (CardSlot.Width / 2);
	}
	
	/**
	 * Kirajzolja a specialis kartya infot.
	 */
	private void DrawInfoBox(Graphics g, String text){
		g.setColor(new Color(0,0,0,175)); //Felig atlatszo fekete teglalap
		g.fillRect(_cardInfoRectangle.X, _cardInfoRectangle.Y, _cardInfoRectangle.Width, _cardInfoRectangle.Height);
		
		g.setColor(Color.white);
		DrawCenteredTextIntoArea(g, text, new Rect(
				_cardInfoRectangle.X + 10, _cardInfoRectangle.Y + 10, _cardInfoRectangle.Width - 20, _cardInfoRectangle.Height - 20));
	}
	
	/**
	 * Kitolt kozepre igazitott szoveggel egy teglalapot
	 */
	public static void DrawCenteredTextIntoArea(Graphics g, String text, Rect area){
		FontMetrics m = g.getFontMetrics(); //Betutipus infok
		
		//Szetvalasztom a szoveget sorokra, minden sor pont belefer a teglalap szelessegebe
		ArrayList<String> lines = new ArrayList<String>();
		
		String line = ""; //Egy atmeneti sor
		int lineW = 0; //A sor szelessege
		
		String[] parts = text.split(" "); //Szetvalasztom a szoveget szavakra.
		for(int x = 0; x < parts.length; x++){
			int partW = m.stringWidth(parts[x] + " "); //Megnezem egy szo + egy szokoz szelesseget
			if(lineW + partW < area.Width){ //Ha ezt hozzarakom a sorhoz es belefer a sorba..
				line += (line.length() == 0 ? "" : " ") + parts[x]; //Hozzaadom a szoveget a sorhoz, ha a sorba mar van szoveg rakok ele egy szokozt is
				lineW += partW;
			}else{ //Ha nem fer bele a sorba uj sort kezdek
				lines.add(line);
				line = parts[x];
				lineW = 0;
			}
		}
		
		if(line.length() > 0){ //Ha maradt valami az csak uj sorba mehet
			lines.add(line);
		}
		
		int drawY = area.Y + area.Height / 2 - ((lines.size() / 2) * (m.getAscent())); //A sorok szama es egy sor magassaga alapjan kiszamolom, hogy honann kezdjem a rajzolast Y-ban
		
		for(int x = 0;x<lines.size();x++){
			DrawCenteredText(g, lines.get(x), area.X + area.Width / 2, drawY + m.getAscent() * x); //Kozepre igazitott sor rajzolas
		}
	}
	
	/**
	 * Kozepre igazitva rajzol egy sor szoveget.
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
	 * Jelzi a UI panelnak hogy ujra kell rajzodnia, mert valami megvaltozott
	 */
	public void RedrawFrame(){
		_frame.Redraw();
	}
	
	/**
	 * A kartya nezegeto frissitese.
	 */
	public void UpdateInspectedSlot(){
		//Megkeresem, hogy melyik slot-on van az eger amit az emberi jatekos megnezhet
		CardSlot inspectedSlot = null;
		for(CardSlot s : _slots){	
			if(s.IsInBounds(_lastMousePosition) && (s.Owner == HumanPlayer || s instanceof CardSlotPlayfield)){
				inspectedSlot = s;
				break;
			}
		}
		
		if(_doInspectCard){ //Ha mar nezek kartyat
			if(inspectedSlot == null){ //Az eger nincs mar kartyan
				_doInspectCard = false;
				_cardSlotToInspect = null;
				_frame.Redraw();
			}else{ //Az eger masik kartyan van
				_cardSlotToInspect = inspectedSlot;
				_doInspectCard = (inspectedSlot.Card != null);
				_isInspectedCardPlaced = (inspectedSlot instanceof CardSlotPlayfield);
				_frame.Redraw();	
			}
		}else{ //Ha nem nezek kartyan
			if(inspectedSlot != null){ //De az eger kartyan van
				_cardSlotToInspect = inspectedSlot;
				_doInspectCard = (inspectedSlot.Card != null);
				_isInspectedCardPlaced = (inspectedSlot instanceof CardSlotPlayfield);
				_frame.Redraw();
			}
		}
	}
	
	/**
	 * Eger mozgasi esemeny.
	 */
	public void OnMouseMove(int x, int y){
		if(_lastMousePosition.X != x || _lastMousePosition.Y != y){
			_lastMousePosition.X = x;
			_lastMousePosition.Y = y;
		
			if(!_gameOver && !_paused){ //Ha nincs vege a jateknak es nem vagyok menube
				UpdateInspectedSlot(); //Frissiteni kell a kartya nezegetot
			}
			
			for(UIButton b : _buttonList){ //Vegig megyek minden gombon amire kattitani lehet es frissitem a kinezetet
				if((b instanceof UIButtonMenu && _paused) || (!(b instanceof UIButtonMenu) && !_paused)){
					if(b.OnMouseMove(x, y)){
						_frame.Redraw();
					}	
				}
			}
		}
	}
	
	/**
	 * Eger kattintasi esemeny.
	 */
	public void OnMouseClick(int x, int y){
		boolean change = false; //Valtozott valami?

		if(!_gameOver && !_paused){
			if(PhasePlayer == HumanPlayer){ //Ha az emberi jatekos kore van akkor megnezem, hogy slot-ra kattintott-e
				for(CardSlot s : _slots){
					if(s.IsInBounds(x, y)){
						_phase.OnSlotClick(s); //Ha igen akkor szolok a fazisnak, hogy kezdjen vele valamit
					
						change = true;
						break;
					}
				}
				if(!change){ //Kattintas volt, de nem slot-ra
					_phase.OnSlotClick(null);
				}
			}
			
			if(HumanPlayer.HandCardManager.OnClick(x, y)){ //Kezre kattintott?
				change = true;
			}
		}
		
		if(change){
			_frame.Redraw();
		}else{
			for(UIButton b : _buttonList){ //Ha nem slot-ra volt kattintas, lehet, hogy gombra volt
				if((b instanceof UIButtonMenu && _paused) || (!(b instanceof UIButtonMenu) && !_paused)){
					b.OnMouseClick(x, y);
				}
			}
		}
	}
	
	/**
	 * UI gomb kattintasi esemeny.
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
	 * Elmenti a jatekot egy fajlba.
	 */
	private void SaveGame() {
		//Nyitok egy fajl mentes ablakot ami sav fajolkat akar menteni a felhasznalo mappajaba
		JFileChooser saveDialog = new JFileChooser();
		saveDialog.setFileFilter(new FileNameExtensionFilter("YuGiOh save files", "sav"));
		saveDialog.setCurrentDirectory(new File(System.getProperty("user.home")));
		if(saveDialog.showSaveDialog(_frame) == JFileChooser.APPROVE_OPTION){
			File file = saveDialog.getSelectedFile();
			if(!file.toString().toLowerCase().endsWith("sav")){ //Ha hianyzik a kiterjesztes
				file = new File(file.toString() + ".sav");
			}
			
			DataOutputStream data = null;
			try{
				data = new DataOutputStream(new FileOutputStream(file));
				
				data.writeUTF("YuGiOh"); //Mentes fejlece
				
				//Elmentem a kartya slot-okat, darabszamat, indexet, tartalmat, allapotat
				data.writeInt(_slots.size());
				for(int x = 0;x<_slots.size();x++){
					CardSlot slot = _slots.get(x);
					data.writeInt(x);
					if(slot instanceof CardSlotPlayfield){ //Csak a palya kartyait mentem el
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
				
				//A jatekosok eletet
				data.writeInt(HumanPlayer.Health);
				data.writeInt(ComputerPlayer.Health);
				
				//Azt, hogy ki jatszik
				data.writeBoolean(PhasePlayer == HumanPlayer);
				
				//A jelenlegi fazist is
				if(_phase == PhaseCardPick){
					data.writeByte(1);
				}else if(_phase == PhaseTactics){
					data.writeByte(2);
				}if(_phase == PhaseAttack){
					data.writeByte(3);
				}
				
				//es azt hogy az elso tamado fazison tul jutott-e a jatek
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
	 * Elment egy tombnyi karyat.
	 */
	private void WriteCardArray(DataOutputStream data, ArrayList<Card> cards) throws IOException{
		data.writeInt(cards.size());
		for(int x = 0;x<cards.size();x++){
			data.writeInt(cards.get(x).SaveUID);
		}
	}
	
	/**
	 * Betolt egy mentest es folytatja onnan a jatekot.
	 */
	private void LoadGame(){
		//Nyitok egy fajl betoltes ablakot.
		JFileChooser openDialog = new JFileChooser();
		openDialog.setFileFilter(new FileNameExtensionFilter("YuGiOh save files", "sav"));
		openDialog.setCurrentDirectory(new File(System.getProperty("user.home")));
		if(openDialog.showOpenDialog(_frame) == JFileChooser.APPROVE_OPTION){
			DataInputStream data = null;
			try{
				data = new DataInputStream(new FileInputStream(openDialog.getSelectedFile()));
				
				String header = data.readUTF();
				if(!header.equalsIgnoreCase("YuGiOh")){ //Ellenorzom a fejlec helyesseget
					throw new Exception("Hibas fejlec!");
				}
				
				//Elkezdem betolteni a kartya slot informaciokat
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
				
				//Betoltom a paklikat is
				ArrayList<Card> tempList = new ArrayList<Card>();
				ReadCardList(data, tempList);
				ComputerPlayer.AddArrayOfCards(tempList);
				ReadCardList(data, tempList);
				ComputerPlayer.InitCards(tempList, false);
								
				ReadCardList(data, tempList);
				HumanPlayer.AddArrayOfCards(tempList);
				ReadCardList(data, tempList);
				HumanPlayer.InitCards(tempList, false);
				
				//A kez manager-t ujra kell inditani
				ComputerPlayer.HandCardManager.ResetOffset();
				HumanPlayer.HandCardManager.ResetOffset();
				
				//Betoltom a jatekosok eletet
				HumanPlayer.Health = data.readInt();
				ComputerPlayer.Health = data.readInt();
				
				if(data.readBoolean()){ //Azt, hogy ki jatszik eppen
					PhasePlayer = HumanPlayer;
				}else{
					PhasePlayer = ComputerPlayer;
				}
				
				switch(data.readByte()){ //A fazist
				case 1:
					SetPhase(PhaseCardPick, false);
					break;
				case 2:
					SetPhase(PhaseTactics, false);
					break;
				case 3:
					SetPhase(PhaseAttack, false);
					
					for(int x = 0;x<usedStates.length;x++){ //Ha tamado fazis van akkor kell az is hogy melyik szorny volt mar hasznalva
						CardSlot s = _slots.get(x);
						if(s instanceof CardSlotPlayfield){
							((CardSlotPlayfield)s).Used = usedStates[x];
						}
					}
					break;
				}
				
				_skipFirstAttackPhase = data.readBoolean();
				
				data.close();
				
				//Beallitom a jatek allapotokat
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
	 * Betolt egy tombnyi kartyat.
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
	 * A slot kijeloleseket alaphelyzetbe allitja.
	 */
	public void ResetSlotHighlight(){
		for(int x = 0;x<_slots.size();x++){
			_slots.get(x).IsHighlighted = true;
		}
	}
	
	/**
	 * A slot kijeloleseket kartya lehelyezese modba allitja.
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
	 * A kartya slot kijeloleseket lehetseges tamadas celpontjaira allitja.
	 */
	public void SetAttackSlotHighlight(CardSlot sourceSlot){
		Player player = sourceSlot.Owner;
		for(CardSlot s : _slots){
			s.IsHighlighted = (s == sourceSlot || (s.Owner != player && s instanceof CardSlotPlayfield && ((CardSlotPlayfield)s).MonsterOnly && s.Card != null));
		}
	}
	
	/**
	 * A slot kijeloleseket szornyekre allitja.
	 */
	public void SetMagicActivateSlotHighlight(CardSlot sourceSlot){
		Player player = sourceSlot.Owner;
		for(CardSlot s : _slots){
			s.IsHighlighted = (s.Owner == player && s instanceof CardSlotPlayfield && ((CardSlotPlayfield)s).MonsterOnly && s.Card != null);
		}
	}
	
	/**
	 * A slot kijeloleseket 2db slot alapjan allitja be.
	 */
	public void SetBotSlotHighlight(CardSlot a, CardSlot b){
		for(CardSlot s : _slots){
			s.IsHighlighted = (s == a || s == b);
		}
	}
	
	/**
	 * A hasznalt slotokat ujra aktivalja.
	 */
	public void ResetSlotUsedStates(){
		for(CardSlot s : _slots){
			if(s instanceof CardSlotPlayfield){
				((CardSlotPlayfield)s).Used = false;
			}
		}
	}
	
	/**
	 * Ki es be kapcsolja a menut.
	 */
	public void TogglePauseMenu(){
		if(!_gameOver && _gameRunning && !IsAITurn()){
			_paused = !_paused;
		
			UpdateMenu();
			
			RedrawFrame();
		}
	}
	
	/**
	 * A menu gombok allapotat allitja be a jatek allapota alapjan.
	 */
	private void UpdateMenu() {
		_exitGameButtonType1.Visible = !_gameRunning;
		
		_exitGameButtonType2.Visible = _saveGameButton.Visible = _gameRunning;
	}
}
