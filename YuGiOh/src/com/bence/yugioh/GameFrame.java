package com.bence.yugioh;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

import com.bence.yugioh.cards.AllCards;

@SuppressWarnings("serial")
public class GameFrame extends JPanel implements MouseListener, MouseMotionListener {
	private boolean _ready = false;
	
	private YuGiOhGame _game;
	
	public boolean PerpareGame(){
		if(!Art.Load())
			return false;
				
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		
		if(!AllCards.Load())
			return false;
		
		_game = new YuGiOhGame(this.getWidth(), this.getHeight(), this);
		
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
	
	public void Redraw(){
		super.invalidate();
		super.validate();
		super.repaint();
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		if (_ready && arg0.getButton() == MouseEvent.BUTTON1){
			_game.OnMouseClick(arg0.getX(), arg0.getY());
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		if(_ready){
			_game.OnMouseMove(arg0.getX(), arg0.getY());
		}
	}
}