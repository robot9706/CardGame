package com.bence.yugioh.utils;

import java.awt.Color;

import com.bence.yugioh.ButtonAction;
import com.bence.yugioh.YuGiOhGame;

/**
 * Egy menu gomb.
 */
public class UIButtonMenu extends UIButton {
	public UIButtonMenu(YuGiOhGame game, String text, int x, int y, int w, int h, ButtonAction action){
		super(game, text, x, y, w, h, action);
	}
	
	protected Color GetNormalColor(){
		return new Color(125,125,125,255);
	}
	
	protected Color GetMouseHoverColor(){
		return new Color(75,75,75,255);
	}
}
