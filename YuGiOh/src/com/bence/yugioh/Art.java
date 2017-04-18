package com.bence.yugioh;

import java.awt.Image;
import java.util.HashMap;

import javax.imageio.ImageIO;

/**
 * Osztály mely kezeli a grafikát.
 * @author Bence
 *
 */
public class Art {
	//Publikus változók melyek a különféle grafikákat tartalmazzák
	public static Image CardSlot_Hand;
	public static Image CardSlot_Monsters;
	public static Image CardSlot_MagicTrap;
	public static Image CardSlot_Stack;
	
	public static Image CardBack;
	
	public static Image ArrowRight;
	public static Image ArrowLeft;
	
	public static Image Background;
	public static Image Logo;
	
	public static Image No;
	
	//Ebben a változóban tárolom a kártya grafikákat név alapján, hogy ne keljen egyesével és akár többször betölteni
	private static HashMap<String, Image> _cardImageCache;
	
	/**
	 * Betölti a grafikákat.
	 * @return true ha minden sikerült, false ha valami hiba történt.
	 */
	public static boolean Load(){
		try{
			_cardImageCache = new HashMap<String, Image>();
			
			CardSlot_Hand = LoadImage("slot_hand");
			CardSlot_Monsters = LoadImage("slot_monster");
			CardSlot_MagicTrap = LoadImage("slot_magictrap");
			CardSlot_Stack = LoadImage("slot_stack");
			
			CardBack = LoadImage("card_back");

			ArrowRight = LoadImage("arrow_right");
			ArrowLeft = LoadImage("arrow_left");
			
			Background = LoadImage("background");
			Logo = LoadImage("logo");
			
			No = LoadImage("no");
			
			return true;
		}
		catch(Exception ex){
			ex.printStackTrace(); //Megjelenítem a hibát konzolban is
		}
		
		return false;
	}
	
	/**
	 * Betölt egy png képet.
	 * @param name a kép neve kiterjesztés és elérési út nélkül.
	 * @return
	 * @throws Exception
	 */
	private static Image LoadImage(String name) throws Exception {
		return ImageIO.read(Art.class.getResource("/" + name + ".png"));
	}
	
	/**
	 * Betölti egy kártya képét ha az még nem történt meg.
	 * @param s
	 * @return
	 * @throws Exception
	 */
	public static Image GetCardImage(String s) throws Exception{
		if(_cardImageCache.containsKey(s)) //Ha már betöltöttem csak visszaadom
			return _cardImageCache.get(s);
	
		//Ha még nincs betöltve, betöltöm és elrakom
		Image load = LoadImage(s);
		_cardImageCache.put(s, load);
		
		return load;
	}
}
