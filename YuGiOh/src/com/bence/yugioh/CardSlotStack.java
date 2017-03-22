package com.bence.yugioh;

import java.awt.Graphics;
import java.awt.Image;

public class CardSlotStack extends CardSlot {
	public CardSlotStack(Player o,int x, int y){
		super(o,x,y);
	}
	
	public Image GetRenderImage(){
		return Art.CardSlot_Stack;
	}
	
	@Override
	public void Draw(Graphics g, Player viewer){
		g.drawImage(GetRenderImage(), X, Y, Width, Height, null);
		
		for(int x = 0;x<4;x++){
			g.drawImage(Art.CardBack, X - 5 * x, Y - 7 * x, Width, Height, null);
		}
	}
	
	@Override
	public void OnClick(Player click){
		if(click == Owner){
			Owner.AddCardToHand(new Card());
		}
	}
	
	@Override
	public boolean IsInBounds(int x, int y){
		return (x >= X - 20 && y >= Y - 28 && x <= X + Width && y <= Y + Height);
	}
}
