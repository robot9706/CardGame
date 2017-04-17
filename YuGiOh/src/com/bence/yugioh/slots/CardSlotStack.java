package com.bence.yugioh.slots;

import java.awt.Graphics;

import com.bence.yugioh.Art;
import com.bence.yugioh.player.Player;

public class CardSlotStack extends CardSlot {
	public CardSlotStack(Player o,int x, int y){
		super(o,x,y);
	}

	
	@Override
	public void Draw(Graphics g, Player viewer){
		if(Owner.Deck != null && Owner.Deck.size() > 0){
			for(int x = 0; x < Math.min(4, Owner.Deck.size()); x++){
				g.drawImage(Art.CardBack, X - 5 * x, Y - 7 * x, Width, Height, null);
			}
		}else{
			g.drawImage(Art.CardSlot_Stack, X, Y, Width, Height, null);
		}
	}
	
	@Override
	public void OnClick(Player click){
		if(click == Owner){
			click.GrabCardFromDeck();
		}
	}
	
	@Override
	public boolean IsInBounds(int x, int y){
		int stackSize = Math.min(4, Owner.Deck.size());
		
		return (x >= X - 5 * stackSize && y >= Y - 7 * stackSize && x <= X + Width && y <= Y + Height);
	}
}
