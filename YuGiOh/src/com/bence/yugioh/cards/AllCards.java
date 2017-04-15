package com.bence.yugioh.cards;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class AllCards {
	public static ArrayList<Card> Cards;
	
	public static boolean Load(){
		if(Cards != null)
			return false;
		
		Cards = new ArrayList<Card>();
		
		try
		{
			InputStream is = Card.class.getResourceAsStream("/cards.data");
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		
			String line = null;
			while((line = rd.readLine()) != null) {
				if(line != null && line.startsWith("#"))
					continue;
				
				String[] data = line.split(";");
				
				switch(data[0]){
				case "Monster":
					{
						Cards.add(new CardMonster(data[1], Integer.parseInt(data[2]), Integer.parseInt(data[3])));
					}
					break;
				case "Magic":
					{
						Cards.add(new CardMagic(data[1]));
					}
					break;
				}
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
	
	public static ArrayList<Card> CreateDeck(int size){
		ArrayList<Card> deck = new ArrayList<Card>();
		
		Random rnd = new Random();
		for(int x = 0;x<size;x++){
			deck.add(Cards.get(rnd.nextInt(Cards.size())));
		}
		
		return deck;
	}
}
