package com.bence.yugioh.cards;

/**
 * Speci�lis sz�rny k�pess�g mely minden sz�rnynek plusz ATK-t ad.
 * @author Bence
 *
 */
public class AddATKAllSpecial implements MonsterSpecial {
	public int ATK;
	
	public AddATKAllSpecial(int atk){
		ATK = atk;
	}
	
	public String GetDescription() {
		return "Minden a p�ly�n l�v� sz�rnynek ad " + String.valueOf(ATK) + " t�mad�si er�t.";
	}
}
