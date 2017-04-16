package com.bence.yugioh.player;

import java.util.ArrayList;
import java.util.Random;

import com.bence.yugioh.YuGiOhGame;
import com.bence.yugioh.cards.Card;
import com.bence.yugioh.cards.CardMagic;
import com.bence.yugioh.cards.CardMonster;
import com.bence.yugioh.phases.*;
import com.bence.yugioh.slots.CardSlot;
import com.bence.yugioh.slots.CardSlotPlayfield;

public class ComputerPlayer extends Player {
	private Thread _aiThread;
	
	private ArrayList<CardSlotPlayfield> _monsterSlots;
	private ArrayList<CardSlotPlayfield> _magicSlots;
	
	public ComputerPlayer(YuGiOhGame game){
		super(game);	
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
		
		Random rnd = new Random();
		int i;
		do{
			i = rnd.nextInt(source.size());
			if(source.get(i).Card == null)
				return source.get(i);
		}while(source.get(i).Card != null);
		
		return null;
	}
	
	private void PlaceCard(Card c){
		if(c instanceof CardMonster){
			CardSlot empty = GetRandomEmptySlot(_monsterSlots);
			if(empty != null){
				MoveCardToSlot(empty, c);
			}
		}else if(c instanceof CardMagic){
			CardSlot empty = GetRandomEmptySlot(_magicSlots);
			if(empty != null){
				MoveCardToSlot(empty, c);
			}
		}
	}
	
	public void DoPhase(final GamePhase phase){
		_aiThread = new Thread(new Runnable(){
			public void run() {
				try {
					AI(phase);
				} catch (Exception e) {
					e.printStackTrace();
					
					phase.GotoNextPhase();
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
		for(int x = 0;x<3;x++){
			if(Hand.size() > 0){
				Thread.sleep(100);
				
				Card c = Hand.get(0);
								
				PlaceCard(c);
				
				if(c instanceof CardMonster){
					CardMonster m = (CardMonster)c;
					if(m.Defense > m.Attack){
						m.IsRotated = true;

						Game.RedrawFrame();
					}
				}
			}
		}
		
		phase.GotoNextPhase();
	}
	
	private void AI_Attack(AttackPhase phase) throws Exception {
		Thread.sleep(100);
		
		phase.GotoNextPhase();
	}
}
