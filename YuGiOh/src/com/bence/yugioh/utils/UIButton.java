package com.bence.yugioh.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import com.bence.yugioh.ButtonAction;
import com.bence.yugioh.YuGiOhGame;

/**
 * UI gomb.
 * @author Bence
 *
 */
public class UIButton {
	public String Text; //A gomb szövege.
	public Rect Rectangle; //Helye a képernyõn.
	public boolean Visible; //Látzódik?
	
	private YuGiOhGame _game;
	private boolean _mouseHover = false; //Rajta van az egér?
	
	private ButtonAction _action; //Kattintás esemény.
	
	public UIButton(YuGiOhGame game, String text, int x, int y, int w, int h, ButtonAction action){
		_game = game;
		_action = action;
		
		Text = text;
		Rectangle = new Rect(x, y, w, h);
		Visible = true;
	}
	
	/**
	 * Visszaadja a gomb színét mikor nincs rajta egér.
	 */
	protected Color GetNormalColor(){
		return new Color(0,0,0,128);
	}
	
	/**
	 * Visszaadja a gomb színét mikor van rajta egér.
	 */
	protected Color GetMouseHoverColor(){
		return new Color(128,128,128,128);
	}
	
	/**
	 * Kirajzolja a gombot.
	 */
	public void Draw(Graphics g){
		if(!Visible) //Ha nem látszik akkor nem történik semmi.
			return;
		
		if(_mouseHover){
			g.setColor(GetMouseHoverColor());
		}else{
			g.setColor(GetNormalColor());
		}
		g.fillRect(Rectangle.X, Rectangle.Y, Rectangle.Width, Rectangle.Height);
		
		g.setColor(Color.white);
		g.setFont(new Font("Arial", Font.BOLD, 20));
		
		YuGiOhGame.DrawCenteredText(g, Text, Rectangle.X + Rectangle.Width / 2.0f, Rectangle.Y + Rectangle.Height / 2.0f);
	}
	
	/**
	 * Egér mozdulás esemény. Visszaadja, hogy történt-e változás.
	 */
	public boolean OnMouseMove(int x, int y){
		boolean o = (boolean)_mouseHover;
		
		if(Visible){
			_mouseHover = Rectangle.IsPointInRect(x, y);
		}else{
			_mouseHover = false;
		}
		
		return (o != _mouseHover);
	}
	
	/**
	 * Egér kattintás esemény.
	 */
	public void OnMouseClick(int x, int y){
		if(Visible && Rectangle.IsPointInRect(x, y)){
			_game.OnUIButtonClick(_action);
		}
	}
}
