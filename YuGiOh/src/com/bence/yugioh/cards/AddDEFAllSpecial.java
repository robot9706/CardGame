package com.bence.yugioh.cards;

public class AddDEFAllSpecial implements MonsterSpecial {
	public int DEF;
	
	public AddDEFAllSpecial(int def){
		DEF = def;
	}
	
	public String GetDescription() {
		return "Minden a p�ly�n l�v� sz�rnynek ad " + String.valueOf(DEF) + " v�dekez�si er�t.";
	}
}

