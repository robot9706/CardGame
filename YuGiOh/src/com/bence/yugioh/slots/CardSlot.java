package com.bence.yugioh.slots;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;

import com.bence.yugioh.Art;
import com.bence.yugioh.cards.Card;
import com.bence.yugioh.player.Player;
import com.bence.yugioh.utils.Point2;

/**
 * K�rtya hely.
 * @author Bence
 */
public  class CardSlot {
	//M�retek, ez mindre azonos
	public static int Width;
	public static int Height;
	
	//A slot helye
	public int X;
	public int Y;
	
	public Player Owner; //Tulajdonos
	public Card Card; //Tartalom
	
	public boolean IsHighlighted; //Ki van jel�lve?
	
	public CardSlot(Player owner, int x, int y){
		Owner = owner;
		
		X = x;
		Y = y;
		
		IsHighlighted = true;
	}
	
	/**
	 * Kirajzolja a slot-ot
	 */
	public void Draw(Graphics g, Player viewer){
		Graphics2D g2 = (Graphics2D)g;
		
		Image i = GetRenderImage(); //A slot h�ttere
		
		AffineTransform t = new AffineTransform(); //A megjelen�tend� k�p transzform�ci�ja
		t.translate(X, Y); //Elmozgatom X,Y-ra
		t.scale((double)Width / i.getWidth(null), (double)Height / i.getHeight(null)); //�tm�retezem a slot m�ret�re
		
		g2.drawImage(i, t, null);
		
		if(Card != null){ //Ha van k�rtya
			if(Card.IsRotated){ //A k�rtya el van ford�tva?
				t.scale(1.0f / ((double)Width / i.getWidth(null)), 1.0f / ((double)Height / i.getHeight(null))); //"Visszavonom" a m�retez�st
				t.translate(Width / 2.0f, Height / 2.0f); //A transzform�ci�t elmozgatom a slot m�ret�nek fel�vel
				t.rotate(Math.toRadians(-90)); //Elford�tom a k�pet
				t.translate(-Width / 2.0f, -Height / 2.0f); //Visszavonom a mozd�t�st
				t.scale((double)Width / i.getWidth(null), (double)Height / i.getHeight(null)); //Be�ll�tom �jra a m�retez�st
			}
			
			g2.drawImage(GetCardImage(viewer), t, null);
			
			if(!IsHighlighted && Card.IsRotated){ //Ha nincs kijel�lve a k�p (teh�t s�t�tebb) �s el van ford�tva a k�rtya, akkor a k�t kil�g� k�rtya sz�l�t is el kell s�t�t�teni 
				g2.setColor(new Color(0, 0, 0, 100));
				g2.fillRect(X - (Height - Width) / 2, Y + (Height - Width) / 2, (Height - Width) / 2, Width);
				g2.fillRect(X + Width, Y + (Height - Width) / 2, (Height - Width) / 2, Width);
			}
		}
		
		if(!IsHighlighted) //Ha nincs kijel�lve a slot akkor el kell s�t�t�teni
		{
			g2.setColor(new Color(0, 0, 0, 100));
			g2.fillRect(X, Y, Width, Height);
		}
	}
	
	/**
	 * Visszaadja a k�rtya k�p�t egy n�z� alapj�n
	 */
	protected Image GetCardImage(Player viewer){
		return (viewer == Owner) ? Card.FrontImage : Art.CardBack;
	}
	
	/**
	 * A slot k�pe.
	 */
	public Image GetRenderImage(){
		return null;
	}
	
	/**
	 * Visszaadja, hogy egy pont benne van-e a slot-ban.
	 */
	public boolean IsInBounds(int x, int y){
		return (x >= X && y >= Y && x <= X + Width && y <= Y + Height);
	}
	
	public boolean IsInBounds(Point2 p){
		return (p.X >= X && p.Y >= Y && p.X <= X + Width && p.Y <= Y + Height);
	}
	
	/**
	 * Slot-ra kattint�s esem�ny.
	 */
	public void OnClick(Player byPlayer){
	}
}
