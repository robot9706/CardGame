package com.bence.yugioh;

import java.awt.Graphics;
import java.awt.Image;

public  class CardSlot {
	public static int Width;
	public static int Height;
	
	protected int X;
	protected int Y;
	
	public Player Owner;
	public Card Card;
	
	public CardSlot(Player owner, int x, int y){
		Owner = owner;
		
		X = x;
		Y = y;
	}
	
	public void Draw(Graphics g, Player viewer){
		g.drawImage(GetRenderImage(), X, Y, Width, Height, null);
		
		if(Card != null){
			g.drawImage((viewer == Owner) ? Card.GetFrontImage() : Card.GetBackImage(), X, Y, Width, Height, null);
		}
	}
	
	public Image GetRenderImage(){
		return null;
	}
}
