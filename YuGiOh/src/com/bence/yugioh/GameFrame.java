package com.bence.yugioh;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class GameFrame extends JPanel {
	private boolean _ready = false;
	
	private YuGiOhGame _game;
	
	public boolean PerpareGame(){
		if(!Art.Load())
			return false;
		
		_game = new YuGiOhGame(this.getWidth(), this.getHeight());
		
		_ready = true;
		return true;
	}
	
	@Override
	protected void paintComponent(Graphics g){
		if(!_ready)
			return;
		
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		_game.Draw(g);
	}
}