package com.bence.yugioh;

import java.awt.Image;

import javax.imageio.ImageIO;

public class Art {
	public static Image CardSlot_Hand;
	public static Image CardSlot_Monsters;
	public static Image CardSlot_MagicTrap;
	public static Image CardSlot_Stack;
	
	public static Image CardBack;
	public static Image CardFront_Temp;
	
	public static boolean Load(){
		try{
			CardSlot_Hand = LoadImage("slot_hand");
			CardSlot_Monsters = LoadImage("slot_monster");
			CardSlot_MagicTrap = LoadImage("slot_magictrap");
			CardSlot_Stack = LoadImage("slot_stack");
			
			CardBack = LoadImage("card_back");
			CardFront_Temp = LoadImage("card_front");
			
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
}
