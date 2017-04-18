package com.bence.yugioh.cards;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.bence.yugioh.Art;

public class AllCards {
	public static ArrayList<Card> MonsterCards;
	public static ArrayList<Card> SpellCards;
	
	public static HashMap<Integer, Card> CardsBySaveUID;
	
	public static boolean Load(){
		if(MonsterCards != null)
			return false;
		
		CardsBySaveUID = new HashMap<Integer, Card>();
		MonsterCards = new ArrayList<Card>();
		SpellCards = new ArrayList<Card>();
		
		try
		{
			int uidCounter = 0;
			
			InputStream is = Card.class.getResourceAsStream("/cards.data");
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		
			String line = null;
			while((line = rd.readLine()) != null) {
				if(line != null && (line.startsWith("#") || line.length() == 0))
					continue;
				
				String[] data = line.split(";");
				
				Card card = null;
				
				switch(data[0]){
				case "Monster":
					{
						MonsterSpecial special = null;
						
						if(data.length > 6){
							switch(data[6]){
							case "AddATK":
								special = new AddATKAllSpecial(Integer.parseInt(data[7]));
								break;
							case "AddDEF":
								special = new AddDEFAllSpecial(Integer.parseInt(data[7]));
								break;
							case "DestroyRandomCard":
								special = new DestroyRandomCardSpecial();
								break;
							case "GrabRandomCard":
								special = new GrabRandomCardSpecial();
								break;
							}
						}
						
						card = new CardMonster(data[2], Integer.parseInt(data[4]), Integer.parseInt(data[5]), special, (uidCounter++));
						card.Category = Integer.parseInt(data[3]);
						
						MonsterCards.add(card);
					}
					break;
				case "Magic":
					{
						MagicEffect fx = null;
						switch(data[3]){
						case "HealPlayer":
							fx = new HealPlayerEffect(Integer.parseInt(data[4]));
							break;
						case "AddDEF":
							fx = new AddDEFEffect(Integer.parseInt(data[4]));
							break;
						case "AddATK":
							fx = new AddATKEffect(Integer.parseInt(data[4]));
							break;
						}
						
						card = new CardMagic(data[2], fx, (uidCounter++));
						
						SpellCards.add(card);
					}
					break;
				}
				
				card.FrontImage = Art.GetCardImage(data[1]);
				CardsBySaveUID.put(card.SaveUID, card);
			}
			
			rd.close();
			is.close();
			
			return true;
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		
		return false;
	}
	
	public static Card GetCardBySaveUID(int uid){
		if(CardsBySaveUID.containsKey(uid))
			return CardsBySaveUID.get(uid).Clone();
		
		return null;
	}
	
	public static ArrayList<Card> CreateDeck(int size){
		Random rnd = new Random();
		
		int maxSpells = size / 6;
		
		ArrayList<Card> shuffle = new ArrayList<Card>();
		for(Card c : MonsterCards){
			int ct = ((3 - c.Category) + 1) * 2;
			
			for(int x = 0;x<ct;x++){
				shuffle.add(c);
			}
		}
		
		ArrayList<Card> deckSorted = new ArrayList<Card>();
		for(int x = 0;x<maxSpells;x++){
			deckSorted.add(SpellCards.get(rnd.nextInt(SpellCards.size())).Clone());
		}
		
		HashMap<Integer, Integer> counter = new HashMap<Integer, Integer>();
		
		while(deckSorted.size() < size){
			Card c = shuffle.get(rnd.nextInt(shuffle.size()));
			
			int count = 0;
			if(counter.containsKey(c.SaveUID)){
				count = counter.get(c.SaveUID);
				if(count >= 3){
					continue;
				}
			}
			
			count++;
			counter.put(c.SaveUID, count);
			
			deckSorted.add(c.Clone());
		}
		
		ArrayList<Card> deck = new ArrayList<Card>();
		while(deckSorted.size() > 0){
			int idx = rnd.nextInt(deckSorted.size());
			
			deck.add(deckSorted.get(idx));
			deckSorted.remove(idx);
		}
		
		return deck;
	}
}
