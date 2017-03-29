package com.bence.yugioh;

public class Rect {
	public int X;
	public int Y;
	public int Width;
	public int Height;
	
	public Rect(int x, int y, int w, int h){
		X = x;
		Y = y;
		Width = w;
		Height = h;
	}
	
	public boolean IsPointInRect(int x, int y){
		return (x >= X && y >= Y && x <= X + Width && y <= Y + Height);
	}
}
