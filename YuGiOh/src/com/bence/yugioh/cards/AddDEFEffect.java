package com.bence.yugioh.cards;

import com.bence.yugioh.YuGiOhGame;
import com.bence.yugioh.player.Player;
import com.bence.yugioh.utils.Texts;

/**
 * Varazs kartya kepesseg, mely egy kivalasztott szorny DEF-jet veglegesen megnoveli
 * @author Bence
 *
 */
public class AddDEFEffect implements MagicEffect {
	private int _def;
	
	public AddDEFEffect(int def){
		_def = def;
	}
	
	public boolean RequiresTarget() {
		return true;
	}

	public String GetDescription() {
		return Texts.AddDEFEffectText.replace("{0}", String.valueOf(_def));
	}

	@Override
	public void Activate(YuGiOhGame game, Player by) {
	}

	@Override
	public void ActivateOnTarget(Card target, YuGiOhGame game) {
		((CardMonster)target).Defense += _def;
	}
}
