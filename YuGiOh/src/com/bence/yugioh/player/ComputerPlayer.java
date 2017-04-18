package com.bence.yugioh.player;

import java.util.ArrayList;
import java.util.Random;

import com.bence.yugioh.YuGiOhGame;
import com.bence.yugioh.cards.AddATKEffect;
import com.bence.yugioh.cards.AddDEFEffect;
import com.bence.yugioh.cards.Card;
import com.bence.yugioh.cards.CardMagic;
import com.bence.yugioh.cards.CardMonster;
import com.bence.yugioh.cards.HealPlayerEffect;
import com.bence.yugioh.cards.MonsterOnPlaceSpecial;
import com.bence.yugioh.phases.*;
import com.bence.yugioh.slots.CardSlot;
import com.bence.yugioh.slots.CardSlotPlayfield;

/**
 * Számítógépes játékos.
 * @author Bence
 *
 */
public class ComputerPlayer extends Player {
	private Thread _aiThread;
	
	private ArrayList<CardSlotPlayfield> _monsterSlots;
	private ArrayList<CardSlotPlayfield> _magicSlots;
	
	private Random _random;
	
	public ComputerPlayer(YuGiOhGame game){
		super(game);	
		
		_random = new Random();
	}
	
	/**
	 * Elõkészíti a kártya helyeket az AI-nak.
	 */
	public void InitSlots(ArrayList<CardSlot> cpuSlots){
		_monsterSlots = new ArrayList<CardSlotPlayfield>();
		_magicSlots = new ArrayList<CardSlotPlayfield>();
		
		for(CardSlot s : cpuSlots){
			if(s instanceof CardSlotPlayfield){
				CardSlotPlayfield sl = (CardSlotPlayfield)s;
				if(sl.MonsterOnly){
					_monsterSlots.add(sl);
				}else{
					_magicSlots.add(sl);
				}
			}
		}
	}
	
	/**
	 * Kártya helyezése egy slot-ba.
	 */
	private void MoveCardToSlot(CardSlot slot, Card c){
		if(slot.Card == null){
			super.RemoveCardFromHand(c);
			slot.Card = c;
		}
		
		Game.RedrawFrame();
	}
	
	/**
	 * Keres egy véletlenszerû üres slot-ot.
	 */
	private CardSlot GetRandomEmptySlot(ArrayList<CardSlotPlayfield> source){
		int empty = 0;
		for(CardSlot s : source){
			if(s.Card == null){
				empty++;
			}
		}
		
		if(empty == 0) //Ha egyátalán nincs üres hely, akkor nem is kell megpróbálni
			return null;
		
		int i;
		do{
			i = _random.nextInt(source.size());
			if(source.get(i).Card == null)
				return source.get(i);
		}while(source.get(i).Card != null);
		
		return null;
	}
	
	/**
	 * Elhelyez egy kártyát a pályán. Visszaadja, hogy sikerült-e.
	 */
	private boolean PlaceCard(Card c) throws InterruptedException{
		CardSlot empty = null;
		if(c instanceof CardMonster){ //Ha szörnykártya
			empty = GetRandomEmptySlot(_monsterSlots);
			if(empty != null){
				MoveCardToSlot(empty, c);
				
				CardMonster cm = (CardMonster)c;
				if(cm.Special != null && cm.Special instanceof MonsterOnPlaceSpecial){ //Ha a szörnykártyának van képessége aktiválni kell
					((MonsterOnPlaceSpecial)cm.Special).OnActivate(Game, empty);
				}
			}
		}else if(c instanceof CardMagic){ //Ha varázskártya
			empty = GetRandomEmptySlot(_magicSlots);
			if(empty != null){
				MoveCardToSlot(empty, c);
			}
		}
		
		if(empty != null){ //Ha volt hova helyezni a kártyát, akkor azt megjelenítem
			Game.SetBotSlotHighlight(empty, null);
			Thread.sleep(500);
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Az AI mely kezeli az aktuális játék fázist.
	 */
	public void DoPhase(final GamePhase phase){
		_aiThread = new Thread(new Runnable(){
			public void run() {
				try {
					AI(phase);
				} catch (Exception e) {
					e.printStackTrace();
					
					phase.GotoNextPhase();
					return;
				}

				if(Game.HumanPlayer.Health <= 0){
					Game.OnGameOver();
				}
			}
		});
		_aiThread.start();
	}
	
	/**
	 * Kiválasztja, hogy melyik fázist kell kezelni.
	 */
	private void AI(GamePhase phase) throws Exception  {
		if(phase instanceof CardPickPhase){
			AI_CardPick((CardPickPhase)phase);
		} else if(phase instanceof TacticsPhase){
			AI_Tactics((TacticsPhase)phase);
		}else if(phase instanceof AttackPhase){
			AI_Attack((AttackPhase)phase);
		}
	}
	
	/**
	 * Kezeli a kártya húzás fázist.
	 */
	private void AI_CardPick(CardPickPhase phase) throws Exception {
		Thread.sleep(100);
		super.GrabCardFromDeck();
		phase.GotoNextPhase();
		
		Game.RedrawFrame();
	}
	
	/**
	 * Kezeli a taktikai fázist.
	 */
	private void AI_Tactics(TacticsPhase phase) throws Exception {
		for(int x = 0;x<_random.nextInt(3)+2;x++){ //Random darab kártyát helyez a pályára.
			if(Hand.size() > 0){
				Card c = Hand.get(0);
								
				if (PlaceCard(c) && c instanceof CardMonster){
					CardMonster m = (CardMonster)c;
					if(m.Defense > m.Attack){
						m.IsRotated = true;

						Game.RedrawFrame();
					}
				}
			}
		}
		
		//Fogom a pályára helyezett varázskártyákat és aktiválom ha tudom
		ArrayList<CardSlot> placed = Game.GetPlayerUsedSlots(this);
		for(CardSlot s : placed){
			if(s.Card == null)
				continue;
			
			if(!((CardSlotPlayfield)s).MonsterOnly){ //Csak a varázskártyákat nézem
				CardMagic mg = (CardMagic)s.Card;
				if(mg.Effect instanceof HealPlayerEffect){ //Ha játékost gyógít elhasználom
					Game.SetBotSlotHighlight(s, null);
					Thread.sleep(500);
					
					mg.Effect.Activate(Game, this);
					s.Card = null;
					
					Game.ResetSlotHighlight();
					Game.RedrawFrame();
				}
				else if(mg.Effect instanceof AddATKEffect){ //Ha +ATK-t ad, megkeresem a legnagyobb ATK-val rendelkezõ szörnyet és arra aktiválom
					CardSlot select = null;
					int atk = 0;
					for(CardSlot slt : placed){
						if(slt.Card != null && ((CardSlotPlayfield)slt).MonsterOnly){
							if (((CardMonster)slt.Card).Attack > atk){
								atk = ((CardMonster)slt.Card).Attack;
								select = slt;
							}
						}
					}
					if(select != null){
						Game.SetBotSlotHighlight(s, select);
						Thread.sleep(500);
						
						mg.Effect.ActivateOnTarget(select.Card, Game);
						s.Card = null;
						
						Game.ResetSlotHighlight();
						Game.RedrawFrame();
					}
				}
				else if(mg.Effect instanceof AddDEFEffect){ //Ha +DEF-et ad akkor megkeresem a legnagyobb DEF-el rendelkezõ szörnyet és azon aktiválom
					CardSlot select = null;
					int def = 0;
					for(CardSlot slt : placed){
						if(slt.Card != null && ((CardSlotPlayfield)slt).MonsterOnly){
							if (((CardMonster)slt.Card).Defense > def){
								def = ((CardMonster)slt.Card).Defense;
								select = slt;
							}
						}
					}
					if(select != null){
						Game.SetBotSlotHighlight(s, select);
						Thread.sleep(500);
						
						mg.Effect.ActivateOnTarget(select.Card, Game);
						s.Card = null;
						
						Game.ResetSlotHighlight();
						Game.RedrawFrame();
					}
				}
			}
		}
		
		Game.ResetSlotHighlight();
		
		phase.GotoNextPhase();
	}
	
	/**
	 * Kezeli a támadó fázist.
	 */
	private void AI_Attack(AttackPhase phase) throws Exception {
		//Összeszedem az ellenfél szörny kártyáit.
		ArrayList<CardSlot> enemy = Game.GetPlayerUsedSlots(Game.HumanPlayer);
		ArrayList<CardSlot> enemyMonsters = new ArrayList<CardSlot>();
		for(CardSlot s : enemy){
			if(s.Card instanceof CardMonster){
				enemyMonsters.add(s);
			}
		}
		
		//Végigmegyek az AI kártyáin
		ArrayList<CardSlot> placed = Game.GetPlayerUsedSlots(this);
		for(CardSlot s : placed){
			if(s.Card == null)
				continue;
			
			if(((CardSlotPlayfield)s).MonsterOnly){ //Ha a kártya szörny
				if(!s.Card.IsRotated){ //És nem védekezõ állásban van
					CardSlot atkSlot = null;
					int minDmg = Integer.MAX_VALUE;
					
					boolean hasEnemyMonsters = false;
					
					for(CardSlot es : enemyMonsters){ //Megkeresem azt a szörnyet akit le tud gyõzni ez a kártya
						if(s == null || es == null || s.Card == null || es.Card == null)
							continue;
						
						hasEnemyMonsters = true;
						
						AttackResult atk = AttackPhase.GetAttackResult(s, es, Game);
						if(!atk.SourceDestroyed && atk.TargetDestroyed){
							int lsd = (atk.WinnerSlot == s ? -1 : atk.LoserDamage);
							
							if(minDmg == Integer.MAX_VALUE || lsd < minDmg){
								atkSlot = es;
								minDmg = lsd;
							}
						}
					}
					
					if(atkSlot != null){ //Ha van mit támadni, megtámadom
						Game.SetBotSlotHighlight(s, atkSlot);
						Thread.sleep(500);
						
						AttackPhase.GetAttackResult(s, atkSlot, Game).DoActions();
						Game.ResetSlotHighlight();
						Game.RedrawFrame();
					}else if(!hasEnemyMonsters){ //Ha az ellenfél nincs is szörnye, egybõl az ellenfél támadom
						Game.SetBotSlotHighlight(s, null);
						Thread.sleep(500);
						
						Game.ResetSlotHighlight();
						Game.RedrawFrame();
						
						Game.DamagePlayer(Game.HumanPlayer, ((CardMonster)s.Card).Attack);
					}
				}
			}
			
			if(Game.HumanPlayer.Health <= 0) //Ha az ellenfélnek nincs élete végzett az AI
				return;
		}
		
		Game.ResetSlotHighlight();
		
		Thread.sleep(500);
		
		phase.GotoNextPhase();
	}
}
