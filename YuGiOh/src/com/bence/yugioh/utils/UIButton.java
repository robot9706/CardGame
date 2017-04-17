package com.bence.yugioh.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import com.bence.yugioh.ButtonAction;
import com.bence.yugioh.YuGiOhGame;

public class UIButton {
	public String Text;
	public Rect Rectangle;
	public boolean Visible;
	
	private YuGiOhGame _game;
	private boolean _mouseHover = false;
	
	private ButtonAction _action;
	
	public UIButton(YuGiOhGame game, String text, int x, int y, int w, int h, ButtonAction action){
		_game = game;
		_action = action;
		
		Text = text;
		Rectangle = new Rect(x, y, w, h);
		Visible = true;
	}
	
	protected Color GetNormalColor(){
		return new Color(0,0,0,128);
	}
	
	protected Color GetMouseHoverColor(){
		return new Color(128,128,128,128);
	}
	
	public void Draw(Graphics g){
		if(!Visible)
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
	
	public boolean OnMouseMove(int x, int y){
		boolean o = (boolean)_mouseHover;
		
		if(Visible){
			_mouseHover = Rectangle.IsPointInRect(x, y);
		}else{
			_mouseHover = false;
		}
		
		return (o != _mouseHover);
	}
	
	public void OnMouseClick(int x, int y){
		if(Visible && Rectangle.IsPointInRect(x, y)){
			_game.OnUIButtonClick(_action);
		}
	}
}
