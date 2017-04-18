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
 * A f� UI komponens mely a j�t�kot tartalmazza.
 * @author Bence
 *
 */
@SuppressWarnings("serial")
public class GameFrame extends JPanel implements MouseListener, MouseMotionListener, KeyListener {
	private boolean _ready = false; //T�rolja, hogy a j�t�k k�szen �ll-e
	
	private YuGiOhGame _game; //Maga a j�t�k
	
	/**
	 * El�k�sz�ti a j�t�kot.
	 */
	public boolean PerpareGame(){
		if(!Art.Load()) //Bet�lt�m a grafik�kat
			return false;
				
		if(!AllCards.Load()) //�s a k�rty�kat is
			return false;
		
		//Hozz� adom az esem�ny kezel�ket is
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.addKeyListener(this);
		
		this.setFocusable(true);
		this.requestFocus();
		
		//L�trehozom a j�t�kot
		_game = new YuGiOhGame(this.getWidth(), this.getHeight(), this);
		
		//Ha ide el�rtem hiba n�lk�l akkor k�szen �ll a j�t�k
		_ready = true;
		return true;
	}
	
	/**
	 * Ez a f�ggv�ny h�v�dik meg, ha grafik�t kell megjelen�teni (p�ld�ul redraw eset�n)
	 */
	@Override
	protected void paintComponent(Graphics g){
		if(!_ready) //Lehet, hogy akkor is rajzolni kell mikor m�g nem �ll k�szen a j�t�k
			return;
		
		//Kit�lr�k mindent a k�pr�l
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		//�s rajzolhat a j�t�k
		_game.Draw(g);
	}
	
	/**
	 * K�nyszer�tett �jra rajzol�s
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
	 * Eg�r kattint�s esem�ny-
	 */
	@Override
	public void mousePressed(MouseEvent arg0) {
		if (_ready && arg0.getButton() == MouseEvent.BUTTON1){ //Ha k�szen �ll a j�t�k �s bal eg�r gombbal t�rt�nt kattint�s
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
	 * Eg�r mozg�s esem�ny.
	 */
	@Override
	public void mouseMoved(MouseEvent arg0) {
		if(_ready){ //Ha k�szen �ll a j�t�k
			_game.OnMouseMove(arg0.getX(), arg0.getY());
		}
	}

	/**
	 * Billenty�zet gombnyom�s esem�ny.
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		if(_ready && e.getKeyCode() == KeyEvent.VK_ESCAPE){ //Ha k�szen �ll a j�t�k �s ESC gomb lenyom�sa t�rt�nt
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