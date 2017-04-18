package com.bence.yugioh.cards;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.bence.yugioh.Art;

/**
 * Kartya kezelo osztaly.
 * @author Bence
 *
 */
public class AllCards {
	public static ArrayList<Card> MonsterCards;
	public static ArrayList<Card> SpellCards;
	
	public static HashMap<Integer, Card> CardsBySaveUID;
	
	/**
	 * Betolti a kartyakat.
	 * @return
	 */
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
			while((line = rd.readLine()) != null) { //Soronkent vegig megyek a cards.data-n
				if(line != null && (line.startsWith("#") || line.length() == 0)) //Ha nem ures a sor es nem megjegyzes
					continue;
				
				String[] data = line.split(";"); //Pontosvesszok menten szetszedem a sort
				
				Card card = null;
				
				switch(data[0]){ //A kartya tipusa
				case "Monster":
					{
						MonsterSpecial special = null;
						
						if(data.length > 6){ //Ha tobb adat van, mint ami egy alap szornyhoz kell, akkor van kepessege is
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
				
				card.FrontImage = Art.GetCardImage(data[1]); //Betoltom a kartya kepet
				CardsBySaveUID.put(card.SaveUID, card); //Elrakom a kartyat a SaveUID-je alapjan, ez kell majd mentesek betoltesehez
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
	
	/**
	 * Visszaad egy kartyat SaveUID alapjan.
	 */
	public static Card GetCardBySaveUID(int uid){
		if(CardsBySaveUID.containsKey(uid))
			return CardsBySaveUID.get(uid).Clone();
		
		return null;
	}
	
	/**
	 * Keszit egy paklit.
	 */
	public static ArrayList<Card> CreateDeck(int size){
		Random rnd = new Random();
		
		int maxSpells = size / 6; //A pakli meretenek hatoda lehet maximum varazs kartya
		
		ArrayList<Card> shuffle = new ArrayList<Card>(); //Tomb mely minden kartyabol a kategoria alapjan tarol valamennyi darabot
		for(Card c : MonsterCards){
			int ct = ((3 - c.Category) + 1) * 2; //Az elso kategoriasbol 6db, a masodikbol 4db, a harmadikbol pedig 2db lehet
			
			for(int x = 0;x<ct;x++){
				shuffle.add(c);
			}
		}
		
		HashMap<Integer, Integer> counter = new HashMap<Integer, Integer>(); //Tarolja, hogy egy kartyabol mennyi van mar a pakliban
		
		ArrayList<Card> deckSorted = new ArrayList<Card>(); //Rendezett pakli
		while(deckSorted.size() < maxSpells){
			Card c = SpellCards.get(rnd.nextInt(SpellCards.size())); //Valasztok egy random varazs kartyat
			
			int count = 0;
			if(counter.containsKey(c.SaveUID)){
				count = counter.get(c.SaveUID);
				if(count >= 3){ //Ha mar volt belole 3
					continue; //Akkor ezt nem rakhatom
				}
			}
			
			count++;
			counter.put(c.SaveUID, count);
			
			deckSorted.add(c.Clone());
		}
		
		while(deckSorted.size() < size){ //Kitoltom a paklit szorny kartyakkal
			Card c = shuffle.get(rnd.nextInt(shuffle.size())); //Valasztok egy random kartyat a szornykartyak kozul, mivel tobb van a kisebb szintuekbol igy tobb kisebb szintu lesz mint nagyobb
			
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
		
		//osszekeverem a paklit
		ArrayList<Card> deck = new ArrayList<Card>();
		while(deckSorted.size() > 0){
			int idx = rnd.nextInt(deckSorted.size());
			
			deck.add(deckSorted.get(idx));
			deckSorted.remove(idx);
		}
		
		return deck;
	}
}
