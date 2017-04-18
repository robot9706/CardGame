package com.bence.yugioh;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

import com.bence.yugioh.cards.AllCards;

/**
 * A fõ UI komponens mely a játékot tartalmazza.
 * @author Bence
 *
 */
@SuppressWarnings("serial")
public class GameFrame extends JPanel implements MouseListener, MouseMotionListener, KeyListener {
	private boolean _ready = false; //Tárolja, hogy a játék készen áll-e
	
	private YuGiOhGame _game; //Maga a játék
	
	/**
	 * Elõkészíti a játékot.
	 */
	public boolean PerpareGame(){
		if(!Art.Load()) //Betöltöm a grafikákat
			return false;
				
		if(!AllCards.Load()) //És a kártyákat is
			return false;
		
		//Hozzá adom az esemény kezelõket is
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.addKeyListener(this);
		
		this.setFocusable(true);
		this.requestFocus();
		
		//Létrehozom a játékot
		_game = new YuGiOhGame(this.getWidth(), this.getHeight(), this);
		
		//Ha ide elértem hiba nélkül akkor készen áll a játék
		_ready = true;
		return true;
	}
	
	/**
	 * Ez a függvény hívódik meg, ha grafikát kell megjeleníteni (például redraw esetén)
	 */
	@Override
	protected void paintComponent(Graphics g){
		if(!_ready) //Lehet, hogy akkor is rajzolni kell mikor még nem áll készen a játék
			return;
		
		//Kitölrök mindent a képrõl
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		//És rajzolhat a játék
		_game.Draw(g);
	}
	
	/**
	 * Kényszerített újra rajzolás
	 */
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

	/**
	 * Egér kattintás esemény-
	 */
	@Override
	public void mousePressed(MouseEvent arg0) {
		if (_ready && arg0.getButton() == MouseEvent.BUTTON1){ //Ha készen áll a játék és bal egér gombbal történt kattintás
			_game.OnMouseClick(arg0.getX(), arg0.getY());
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
	}

	/**
	 * Egér mozgás esemény.
	 */
	@Override
	public void mouseMoved(MouseEvent arg0) {
		if(_ready){ //Ha készen áll a játék
			_game.OnMouseMove(arg0.getX(), arg0.getY());
		}
	}

	/**
	 * Billentyûzet gombnyomás esemény.
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		if(_ready && e.getKeyCode() == KeyEvent.VK_ESCAPE){ //Ha készen áll a játék és ESC gomb lenyomása történt
			_game.TogglePauseMenu();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}
}