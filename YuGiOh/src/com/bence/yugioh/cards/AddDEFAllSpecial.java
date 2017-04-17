package com.bence.yugioh.cards;

public class AddDEFAllSpecial implements MonsterSpecial {
	public int DEF;
	
	public AddDEFAllSpecial(int def){
		DEF = def;
	}
	
	public String GetDescription() {
		return "Minden a pályán lévõ szörnynek ad " + String.valueOf(DEF) + " védekezési erõt.";
	}
}

