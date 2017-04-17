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

public class ComputerPlayer extends Player {
	private Thread _aiThread;
	
	private ArrayList<CardSlotPlayfield> _monsterSlots;
	private ArrayList<CardSlotPlayfield> _magicSlots;
	
	private Random _random;
	
	public ComputerPlayer(YuGiOhGame game){
		super(game);	
		
		_random = new Random();
	}
	
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
	
	private void MoveCardToSlot(CardSlot slot, Card c){
		if(slot.Card == null){
			super.RemoveCardFromHand(c);
			slot.Card = c;
		}
		
		Game.RedrawFrame();
	}
	
	private CardSlot GetRandomEmptySlot(ArrayList<CardSlotPlayfield> source){
		int empty = 0;
		for(CardSlot s : source){
			if(s.Card == null){
				empty++;
			}
		}
		
		if(empty == 0)
			return null;
		
		int i;
		do{
			i = _random.nextInt(source.size());
			if(source.get(i).Card == null)
				return source.get(i);
		}while(source.get(i).Card != null);
		
		return null;
	}
	
	private boolean PlaceCard(Card c) throws InterruptedException{
		CardSlot empty = null;
		if(c instanceof CardMonster){
			empty = GetRandomEmptySlot(_monsterSlots);
			if(empty != null){
				MoveCardToSlot(empty, c);
				
				CardMonster cm = (CardMonster)c;
				if(cm.Special != null && cm.Special instanceof MonsterOnPlaceSpecial){
					((MonsterOnPlaceSpecial)cm.Special).OnActivate(Game, empty);
				}
			}
		}else if(c instanceof CardMagic){
			empty = GetRandomEmptySlot(_magicSlots);
			if(empty != null){
				MoveCardToSlot(empty, c);
			}
		}
		
		if(empty != null){
			Game.SetBotSlotHighlight(empty, null);
			Thread.sleep(500);
			
			return true;
		}
		
		return false;
	}
	
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
	
	private void AI(GamePhase phase) throws Exception  {
		if(phase instanceof CardPickPhase){
			AI_CardPick((CardPickPhase)phase);
		} else if(phase instanceof TacticsPhase){
			AI_Tactics((TacticsPhase)phase);
		}else if(phase instanceof AttackPhase){
			AI_Attack((AttackPhase)phase);
		}
	}
	
	private void AI_CardPick(CardPickPhase phase) throws Exception {
		Thread.sleep(100);
		super.GrabCardFromDeck();
		phase.GotoNextPhase();
		
		Game.RedrawFrame();
	}
	
	private void AI_Tactics(TacticsPhase phase) throws Exception {
		for(int x = 0;x<_random.nextInt(3)+2;x++){
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
		
		Game.ResetSlotHighlight();
		
		phase.GotoNextPhase();
	}
	
	private void AI_Attack(AttackPhase phase) throws Exception {
		ArrayList<CardSlot> enemy = Game.GetPlayerUsedSlots(Game.HumanPlayer);
		ArrayList<CardSlot> enemyMonsters = new ArrayList<CardSlot>();
		for(CardSlot s : enemy){
			if(s.Card instanceof CardMonster){
				enemyMonsters.add(s);
			}
		}
		
		ArrayList<CardSlot> placed = Game.GetPlayerUsedSlots(this);
		for(CardSlot s : placed){
			if(s.Card == null)
				continue;
			
			if(((CardSlotPlayfield)s).MonsterOnly){
				if(!s.Card.IsRotated){
					CardSlot atkSlot = null;
					int minDmg = Integer.MAX_VALUE;
					
					boolean hasEnemyMonsters = false;
					
					for(CardSlot es : enemyMonsters){
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
					
					if(atkSlot != null){
						Game.SetBotSlotHighlight(s, atkSlot);
						Thread.sleep(1000);
						
						AttackPhase.GetAttackResult(s, atkSlot, Game).DoActions();
						Game.ResetSlotHighlight();
						Game.RedrawFrame();
					}else if(!hasEnemyMonsters){
						Game.SetBotSlotHighlight(s, null);
						Thread.sleep(1000);
						
						Game.ResetSlotHighlight();
						Game.RedrawFrame();
						
						Game.DamagePlayer(Game.HumanPlayer, ((CardMonster)s.Card).Attack);
					}
				}
			}else{
				CardMagic mg = (CardMagic)s.Card;
				if(mg.Effect instanceof HealPlayerEffect){
					Game.SetBotSlotHighlight(s, null);
					Thread.sleep(500);
					
					mg.Effect.Activate(Game, this);
					s.Card = null;
					
					Game.ResetSlotHighlight();
					Game.RedrawFrame();
				}
				else if(mg.Effect instanceof AddATKEffect){
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
				else if(mg.Effect instanceof AddDEFEffect){
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
			
			if(Game.HumanPlayer.Health <= 0)
				return;
		}
		
		Game.ResetSlotHighlight();
		
		Thread.sleep(500);
		
		phase.GotoNextPhase();
	}
}
