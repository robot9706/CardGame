package com.bence.yugioh;

import java.awt.Image;
import java.util.HashMap;

import javax.imageio.ImageIO;

/**
 * Osztaly mely kezeli a grafikat.
 * @author Bence
 *
 */
public class Art {
	//Publikus valtozok melyek a kulonfele grafikakat tartalmazzak
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
	
	//Ebben a valtozoban tarolom a kartya grafikakat nev alapjan, hogy ne keljen egyesevel es akar tobbszor betolteni
	private static HashMap<String, Image> _cardImageCache;
	
	/**
	 * Betolti a grafikakat.
	 * @return true ha minden sikerult, false ha valami hiba tortent.
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
			ex.printStackTrace(); //Megjelenitem a hibat konzolban is
		}
		
		return false;
	}
	
	/**
	 * Betolt egy png kepet.
	 * @param name a kep neve kiterjesztes es eleresi ut nelkul.
	 * @return
	 * @throws Exception
	 */
	private static Image LoadImage(String name) throws Exception {
		return ImageIO.read(Art.class.getResource("/" + name + ".png"));
	}
	
	/**
	 * Betolti egy kartya kepet ha az meg nem tortent meg.
	 * @param s
	 * @return
	 * @throws Exception
	 */
	public static Image GetCardImage(String s) throws Exception{
		if(_cardImageCache.containsKey(s)) //Ha mar betoltottem csak visszaadom
			return _cardImageCache.get(s);
	
		//Ha meg nincs betoltve, betoltom es elrakom
		Image load = LoadImage(s);
		_cardImageCache.put(s, load);
		
		return load;
	}
}
