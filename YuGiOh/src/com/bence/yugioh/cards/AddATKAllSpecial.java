package com.bence.yugioh.cards;

import com.bence.yugioh.utils.Texts;

/**
 * Specialis szorny kepesseg mely minden szornynek plusz ATK-t ad.
 * @author Bence
 *
 */
public class AddATKAllSpecial implements MonsterSpecial {
	public int ATK;
	
	public AddATKAllSpecial(int atk){
		ATK = atk;
	}
	
	public String GetDescription() {
		return Texts.AddATKAllText.replace("{0}", String.valueOf(ATK));
	}
}
