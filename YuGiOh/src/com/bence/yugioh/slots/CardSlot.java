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
 * Kartya hely.
 * @author Bence
 */
public  class CardSlot {
	//Meretek, ez mindre azonos
	public static int Width;
	public static int Height;
	
	//A slot helye
	public int X;
	public int Y;
	
	public Player Owner; //Tulajdonos
	public Card Card; //Tartalom
	
	public boolean IsHighlighted; //Ki van jelolve?
	
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
		
		Image i = GetRenderImage(); //A slot hattere
		
		AffineTransform t = new AffineTransform(); //A megjelenitendo kep transzformacioja
		t.translate(X, Y); //Elmozgatom X,Y-ra
		t.scale((double)Width / i.getWidth(null), (double)Height / i.getHeight(null)); //atmeretezem a slot meretere
		
		g2.drawImage(i, t, null);
		
		if(Card != null){ //Ha van kartya
			if(Card.IsRotated){ //A kartya el van forditva?
				t.scale(1.0f / ((double)Width / i.getWidth(null)), 1.0f / ((double)Height / i.getHeight(null))); //"Visszavonom" a meretezest
				t.translate(Width / 2.0f, Height / 2.0f); //A transzformaciot elmozgatom a slot meretenek felevel
				t.rotate(Math.toRadians(-90)); //Elforditom a kepet
				t.translate(-Width / 2.0f, -Height / 2.0f); //Visszavonom a mozditast
				t.scale((double)Width / i.getWidth(null), (double)Height / i.getHeight(null)); //Beallitom ujra a meretezest
			}
			
			g2.drawImage(GetCardImage(viewer), t, null);
			
			if(!IsHighlighted && Card.IsRotated){ //Ha nincs kijelolve a kep (tehat sotetebb) es el van forditva a kartya, akkor a ket kilogo kartya szelet is el kell sotetiteni 
				g2.setColor(new Color(0, 0, 0, 100));
				g2.fillRect(X - (Height - Width) / 2, Y + (Height - Width) / 2, (Height - Width) / 2, Width);
				g2.fillRect(X + Width, Y + (Height - Width) / 2, (Height - Width) / 2, Width);
			}
		}
		
		if(!IsHighlighted) //Ha nincs kijelolve a slot akkor el kell sotetiteni
		{
			g2.setColor(new Color(0, 0, 0, 100));
			g2.fillRect(X, Y, Width, Height);
		}
	}
	
	/**
	 * Visszaadja a kartya kepet egy nezo alapjan
	 */
	protected Image GetCardImage(Player viewer){
		return (viewer == Owner) ? Card.FrontImage : Art.CardBack;
	}
	
	/**
	 * A slot kepe.
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
	 * Slot-ra kattintas esemeny.
	 */
	public void OnClick(Player byPlayer){
	}
}
