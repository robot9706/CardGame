package com.bence.yugioh.cards;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.bence.yugioh.Art;

/**
 * Kártya kezelõ osztály.
 * @author Bence
 *
 */
public class AllCards {
	public static ArrayList<Card> MonsterCards;
	public static ArrayList<Card> SpellCards;
	
	public static HashMap<Integer, Card> CardsBySaveUID;
	
	/**
	 * Betölti a kártyákat.
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
			while((line = rd.readLine()) != null) { //Soronként végig megyek a cards.data-n
				if(line != null && (line.startsWith("#") || line.length() == 0)) //Ha nem üres a sor és nem megjegyzés
					continue;
				
				String[] data = line.split(";"); //Pontosvesszõk mentén szétszedem a sort
				
				Card card = null;
				
				switch(data[0]){ //A kártya típusa
				case "Monster":
					{
						MonsterSpecial special = null;
						
						if(data.length > 6){ //Ha több adat van, mint ami egy alap szörnyhöz kell, akkor van képessége is
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
				
				card.FrontImage = Art.GetCardImage(data[1]); //Betöltöm a kártya képét
				CardsBySaveUID.put(card.SaveUID, card); //Elrakom a kártyát a SaveUID-je alapján, ez kell majd mentések betöltéséhez
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
	 * Visszaad egy kártyát SaveUID alapján.
	 */
	public static Card GetCardBySaveUID(int uid){
		if(CardsBySaveUID.containsKey(uid))
			return CardsBySaveUID.get(uid).Clone();
		
		return null;
	}
	
	/**
	 * Készít egy paklit.
	 */
	public static ArrayList<Card> CreateDeck(int size){
		Random rnd = new Random();
		
		int maxSpells = size / 6; //A pakli méretének hatoda lehet maximum varázs kártya
		
		ArrayList<Card> shuffle = new ArrayList<Card>(); //Tömb mely minden kártyából a kategória alapján tárol valamennyi darabot
		for(Card c : MonsterCards){
			int ct = ((3 - c.Category) + 1) * 2; //Az elsõ kategóriásból 6db, a másodikból 4db, a harmadikból pedig 2db lehet
			
			for(int x = 0;x<ct;x++){
				shuffle.add(c);
			}
		}
		
		HashMap<Integer, Integer> counter = new HashMap<Integer, Integer>(); //Tárolja, hogy egy kártyából mennyi van már a pakliban
		
		ArrayList<Card> deckSorted = new ArrayList<Card>(); //Rendezett pakli
		while(deckSorted.size() < maxSpells){
			Card c = SpellCards.get(rnd.nextInt(SpellCards.size())); //Választok egy random varázs kártyát
			
			int count = 0;
			if(counter.containsKey(c.SaveUID)){
				count = counter.get(c.SaveUID);
				if(count >= 3){ //Ha már volt belõle 3
					continue; //Akkor ezt nem rakhatom
				}
			}
			
			count++;
			counter.put(c.SaveUID, count);
			
			deckSorted.add(c.Clone());
		}
		
		while(deckSorted.size() < size){ //Kitöltöm a paklit szörny kártyákkal
			Card c = shuffle.get(rnd.nextInt(shuffle.size())); //Választok egy random kártyát a szörnykártyák közül, mivel több van a kisebb szintûekbõl így több kisebb szintû lesz mint nagyobb
			
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
		
		//Összekeverem a paklit
		ArrayList<Card> deck = new ArrayList<Card>();
		while(deckSorted.size() > 0){
			int idx = rnd.nextInt(deckSorted.size());
			
			deck.add(deckSorted.get(idx));
			deckSorted.remove(idx);
		}
		
		return deck;
	}
}
