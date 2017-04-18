package com.bence.yugioh.cards;

/**
 * Speciális szörny képesség mely minden szörnynek plusz ATK-t ad.
 * @author Bence
 *
 */
public class AddATKAllSpecial implements MonsterSpecial {
	public int ATK;
	
	public AddATKAllSpecial(int atk){
		ATK = atk;
	}
	
	public String GetDescription() {
		return "Minden a pályán lévõ szörnynek ad " + String.valueOf(ATK) + " támadási erõt.";
	}
}
