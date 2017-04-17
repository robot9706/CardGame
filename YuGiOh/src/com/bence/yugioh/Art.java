package com.bence.yugioh;

import java.awt.Image;
import java.util.HashMap;

import javax.imageio.ImageIO;

public class Art {
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
	
	private static HashMap<String, Image> _cardImageCache;
	
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
			ex.printStackTrace();
		}
		
		return false;
	}
	
	private static Image LoadImage(String name) throws Exception {
		return ImageIO.read(Art.class.getResource("/" + name + ".png"));
	}
	
	public static Image GetCardImage(String s) throws Exception{
		if(_cardImageCache.containsKey(s))
			return _cardImageCache.get(s);
		
		Image load = LoadImage(s);
		_cardImageCache.put(s, load);
		
		return load;
	}
}
