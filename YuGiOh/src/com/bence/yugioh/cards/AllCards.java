package com.bence.yugioh.cards;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.bence.yugioh.Art;

/**
 * K�rtya kezel� oszt�ly.
 * @author Bence
 *
 */
public class AllCards {
	public static ArrayList<Card> MonsterCards;
	public static ArrayList<Card> SpellCards;
	
	public static HashMap<Integer, Card> CardsBySaveUID;
	
	/**
	 * Bet�lti a k�rty�kat.
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
			while((line = rd.readLine()) != null) { //Soronk�nt v�gig megyek a cards.data-n
				if(line != null && (line.startsWith("#") || line.length() == 0)) //Ha nem �res a sor �s nem megjegyz�s
					continue;
				
				String[] data = line.split(";"); //Pontosvessz�k ment�n sz�tszedem a sort
				
				Card card = null;
				
				switch(data[0]){ //A k�rtya t�pusa
				case "Monster":
					{
						MonsterSpecial special = null;
						
						if(data.length > 6){ //Ha t�bb adat van, mint ami egy alap sz�rnyh�z kell, akkor van k�pess�ge is
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
				
				card.FrontImage = Art.GetCardImage(data[1]); //Bet�lt�m a k�rtya k�p�t
				CardsBySaveUID.put(card.SaveUID, card); //Elrakom a k�rty�t a SaveUID-je alapj�n, ez kell majd ment�sek bet�lt�s�hez
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
	 * Visszaad egy k�rty�t SaveUID alapj�n.
	 */
	public static Card GetCardBySaveUID(int uid){
		if(CardsBySaveUID.containsKey(uid))
			return CardsBySaveUID.get(uid).Clone();
		
		return null;
	}
	
	/**
	 * K�sz�t egy paklit.
	 */
	public static ArrayList<Card> CreateDeck(int size){
		Random rnd = new Random();
		
		int maxSpells = size / 6; //A pakli m�ret�nek hatoda lehet maximum var�zs k�rtya
		
		ArrayList<Card> shuffle = new ArrayList<Card>(); //T�mb mely minden k�rty�b�l a kateg�ria alapj�n t�rol valamennyi darabot
		for(Card c : MonsterCards){
			int ct = ((3 - c.Category) + 1) * 2; //Az els� kateg�ri�sb�l 6db, a m�sodikb�l 4db, a harmadikb�l pedig 2db lehet
			
			for(int x = 0;x<ct;x++){
				shuffle.add(c);
			}
		}
		
		HashMap<Integer, Integer> counter = new HashMap<Integer, Integer>(); //T�rolja, hogy egy k�rty�b�l mennyi van m�r a pakliban
		
		ArrayList<Card> deckSorted = new ArrayList<Card>(); //Rendezett pakli
		while(deckSorted.size() < maxSpells){
			Card c = SpellCards.get(rnd.nextInt(SpellCards.size())); //V�lasztok egy random var�zs k�rty�t
			
			int count = 0;
			if(counter.containsKey(c.SaveUID)){
				count = counter.get(c.SaveUID);
				if(count >= 3){ //Ha m�r volt bel�le 3
					continue; //Akkor ezt nem rakhatom
				}
			}
			
			count++;
			counter.put(c.SaveUID, count);
			
			deckSorted.add(c.Clone());
		}
		
		while(deckSorted.size() < size){ //Kit�lt�m a paklit sz�rny k�rty�kkal
			Card c = shuffle.get(rnd.nextInt(shuffle.size())); //V�lasztok egy random k�rty�t a sz�rnyk�rty�k k�z�l, mivel t�bb van a kisebb szint�ekb�l �gy t�bb kisebb szint� lesz mint nagyobb
			
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
		
		//�sszekeverem a paklit
		ArrayList<Card> deck = new ArrayList<Card>();
		while(deckSorted.size() > 0){
			int idx = rnd.nextInt(deckSorted.size());
			
			deck.add(deckSorted.get(idx));
			deckSorted.remove(idx);
		}
		
		return deck;
	}
}
