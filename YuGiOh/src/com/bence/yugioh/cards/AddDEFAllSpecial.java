package com.bence.yugioh.cards;

import com.bence.yugioh.utils.Texts;

/**
 * Specialis szorny kepesseg mely minden szornynek plusz DEF-et ad.
 * @author Bence
 *
 */
public class AddDEFAllSpecial implements MonsterSpecial {
	public int DEF;
	
	public AddDEFAllSpecial(int def){
		DEF = def;
	}
	
	public String GetDescription() {
		return Texts.AddDEFAllText.replace("{0}", String.valueOf(DEF));
	}
}

