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
 * Kártya hely.
 * @author Bence
 */
public  class CardSlot {
	//Méretek, ez mindre azonos
	public static int Width;
	public static int Height;
	
	//A slot helye
	public int X;
	public int Y;
	
	public Player Owner; //Tulajdonos
	public Card Card; //Tartalom
	
	public boolean IsHighlighted; //Ki van jelölve?
	
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
		
		Image i = GetRenderImage(); //A slot háttere
		
		AffineTransform t = new AffineTransform(); //A megjelenítendõ kép transzformációja
		t.translate(X, Y); //Elmozgatom X,Y-ra
		t.scale((double)Width / i.getWidth(null), (double)Height / i.getHeight(null)); //Átméretezem a slot méretére
		
		g2.drawImage(i, t, null);
		
		if(Card != null){ //Ha van kártya
			if(Card.IsRotated){ //A kártya el van fordítva?
				t.scale(1.0f / ((double)Width / i.getWidth(null)), 1.0f / ((double)Height / i.getHeight(null))); //"Visszavonom" a méretezést
				t.translate(Width / 2.0f, Height / 2.0f); //A transzformációt elmozgatom a slot méretének felével
				t.rotate(Math.toRadians(-90)); //Elfordítom a képet
				t.translate(-Width / 2.0f, -Height / 2.0f); //Visszavonom a mozdítást
				t.scale((double)Width / i.getWidth(null), (double)Height / i.getHeight(null)); //Beállítom újra a méretezést
			}
			
			g2.drawImage(GetCardImage(viewer), t, null);
			
			if(!IsHighlighted && Card.IsRotated){ //Ha nincs kijelölve a kép (tehát sötétebb) és el van fordítva a kártya, akkor a két kilógó kártya szélét is el kell sötétíteni 
				g2.setColor(new Color(0, 0, 0, 100));
				g2.fillRect(X - (Height - Width) / 2, Y + (Height - Width) / 2, (Height - Width) / 2, Width);
				g2.fillRect(X + Width, Y + (Height - Width) / 2, (Height - Width) / 2, Width);
			}
		}
		
		if(!IsHighlighted) //Ha nincs kijelölve a slot akkor el kell sötétíteni
		{
			g2.setColor(new Color(0, 0, 0, 100));
			g2.fillRect(X, Y, Width, Height);
		}
	}
	
	/**
	 * Visszaadja a kártya képét egy nézõ alapján
	 */
	protected Image GetCardImage(Player viewer){
		return (viewer == Owner) ? Card.FrontImage : Art.CardBack;
	}
	
	/**
	 * A slot képe.
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
	 * Slot-ra kattintás esemény.
	 */
	public void OnClick(Player byPlayer){
	}
}
