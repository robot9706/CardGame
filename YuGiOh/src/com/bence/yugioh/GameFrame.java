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
import com.bence.yugioh.utils.Texts;

/**
 * A fo UI komponens mely a jatekot tartalmazza.
 * @author Bence
 *
 */
@SuppressWarnings("serial")
public class GameFrame extends JPanel implements MouseListener, MouseMotionListener, KeyListener {
	private boolean _ready = false; //Tarolja, hogy a jatek keszen all-e
	
	private YuGiOhGame _game; //Maga a jatek
	
	/**
	 * Elokesziti a jatekot.
	 */
	public boolean PerpareGame(){
		if(!Art.Load()) //Betoltom a grafikakat
			return false;
				
		if(!AllCards.Load()) //es a kartyakat is
			return false;
		
		try{
			Texts.Init();
		}catch(Exception ex){
			ex.printStackTrace();
			return false;
		}
		
		//Hozza adom az esemeny kezeloket is
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.addKeyListener(this);
		
		this.setFocusable(true);
		this.requestFocus();
		
		//Letrehozom a jatekot
		_game = new YuGiOhGame(this.getWidth(), this.getHeight(), this);
		
		//Ha ide elertem hiba nelkul akkor keszen all a jatek
		_ready = true;
		return true;
	}
	
	/**
	 * Ez a fuggveny hivodik meg, ha grafikat kell megjeleniteni (peldaul redraw eseten)
	 */
	@Override
	protected void paintComponent(Graphics g){
		if(!_ready) //Lehet, hogy akkor is rajzolni kell mikor meg nem all keszen a jatek
			return;
		
		//Kitolrok mindent a keprol
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		//es rajzolhat a jatek
		_game.Draw(g);
	}
	
	/**
	 * Kenyszeritett ujra rajzolas
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
	 * Eger kattintas esemeny-
	 */
	@Override
	public void mousePressed(MouseEvent arg0) {
		if (_ready && arg0.getButton() == MouseEvent.BUTTON1){ //Ha keszen all a jatek es bal eger gombbal tortent kattintas
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
	 * Eger mozgas esemeny.
	 */
	@Override
	public void mouseMoved(MouseEvent arg0) {
		if(_ready){ //Ha keszen all a jatek
			_game.OnMouseMove(arg0.getX(), arg0.getY());
		}
	}

	/**
	 * Billentyuzet gombnyomas esemeny.
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		if(_ready && e.getKeyCode() == KeyEvent.VK_ESCAPE){ //Ha keszen all a jatek es ESC gomb lenyomasa tortent
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