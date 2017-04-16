package com.bence.yugioh.slots;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;

import com.bence.yugioh.cards.Card;
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
		
		Image i = GetRenderImage();
		
		AffineTransform t = new AffineTransform();
		t.translate(X, Y);
		t.scale((double)Width / i.getWidth(null), (double)Height / i.getHeight(null));
		
		g2.drawImage(i, t, null);
		
		if(Card != null){
			if(Card.IsRotated){
				t.scale(1.0f / ((double)Width / i.getWidth(null)), 1.0f / ((double)Height / i.getHeight(null)));
				t.translate(Width / 2.0f, Height / 2.0f);
				t.rotate(Math.toRadians(-90));
				t.translate(-Width / 2.0f, -Height / 2.0f);
				t.scale((double)Width / i.getWidth(null), (double)Height / i.getHeight(null));
			}
			
			g2.drawImage(GetCardImage(viewer), t, null);
			
			if(!IsHighlighted && Card.IsRotated){
				g2.setColor(new Color(0, 0, 0, 100));
				g2.fillRect(X - (Height - Width) / 2, Y + (Height - Width) / 2, (Height - Width) / 2, Width);
				g2.fillRect(X + Width, Y + (Height - Width) / 2, (Height - Width) / 2, Width);
			}
		}
		
		if(!IsHighlighted)
		{
			g2.setColor(new Color(0, 0, 0, 100));
			g2.fillRect(X, Y, Width, Height);
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
