package com.bence.yugioh.slots;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;

import com.bence.yugioh.Card;
import com.bence.yugioh.phases.GamePhase;
import com.bence.yugioh.player.Player;
import com.bence.yugioh.utils.Point2;

public  class CardSlot {
	public static int Width;
	public static int Height;
	
	public int X;
	public int Y;
	
	public Player Owner;
	public Card Card;
	
	public boolean IsHighlighted;
	
	public CardSlot(Player owner, int x, int y){
		Owner = owner;
		
		X = x;
		Y = y;
		
		IsHighlighted = true;
	}
	
	public void Draw(Graphics g, Player viewer){
		Graphics2D g2 = (Graphics2D)g;
		
		if(!IsHighlighted)
		{
			g2.setColor(new Color(0, 0, 0, 180));
			g2.fillRect(X, Y, Width, Height);
		}
		
		Image i = GetRenderImage();
		
		AffineTransform t = new AffineTransform();
		t.translate(X, Y);
		t.scale((double)Width / i.getWidth(null), (double)Height / i.getHeight(null));
		
		g2.drawImage(i, t, null);
		
		if(Card != null){
			if(Card.IsRotated){
				t.translate(Width / 2.0, Height / 2.0);
				t.rotate(Math.toRadians(90));
				t.translate(-Width / 2.0, -Height / 2.0);
			}
			
			g2.drawImage(GetCardImage(viewer), t, null);
		}
	}
	
	protected Image GetCardImage(Player viewer){
		return (viewer == Owner) ? Card.GetFrontImage() : Card.GetBackImage();
	}
	
	public Image GetRenderImage(){
		return null;
	}
	
	public boolean IsInBounds(int x, int y){
		return (x >= X && y >= Y && x <= X + Width && y <= Y + Height);
	}
	
	public boolean IsInBounds(Point2 p){
		return (p.X >= X && p.Y >= Y && p.X <= X + Width && p.Y <= Y + Height);
	}
	
	public void OnClick(Player byPlayer){
	}
}
