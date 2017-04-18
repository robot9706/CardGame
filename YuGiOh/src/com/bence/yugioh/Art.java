package com.bence.yugioh;

import java.awt.Image;
import java.util.HashMap;

import javax.imageio.ImageIO;

/**
 * Oszt�ly mely kezeli a grafik�t.
 * @author Bence
 *
 */
public class Art {
	//Publikus v�ltoz�k melyek a k�l�nf�le grafik�kat tartalmazz�k
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
	
	//Ebben a v�ltoz�ban t�rolom a k�rtya grafik�kat n�v alapj�n, hogy ne keljen egyes�vel �s ak�r t�bbsz�r bet�lteni
	private static HashMap<String, Image> _cardImageCache;
	
	/**
	 * Bet�lti a grafik�kat.
	 * @return true ha minden siker�lt, false ha valami hiba t�rt�nt.
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
			ex.printStackTrace(); //Megjelen�tem a hib�t konzolban is
		}
		
		return false;
	}
	
	/**
	 * Bet�lt egy png k�pet.
	 * @param name a k�p neve kiterjeszt�s �s el�r�si �t n�lk�l.
	 * @return
	 * @throws Exception
	 */
	private static Image LoadImage(String name) throws Exception {
		return ImageIO.read(Art.class.getResource("/" + name + ".png"));
	}
	
	/**
	 * Bet�lti egy k�rtya k�p�t ha az m�g nem t�rt�nt meg.
	 * @param s
	 * @return
	 * @throws Exception
	 */
	public static Image GetCardImage(String s) throws Exception{
		if(_cardImageCache.containsKey(s)) //Ha m�r bet�lt�ttem csak visszaadom
			return _cardImageCache.get(s);
	
		//Ha m�g nincs bet�ltve, bet�lt�m �s elrakom
		Image load = LoadImage(s);
		_cardImageCache.put(s, load);
		
		return load;
	}
}
