package com.bence.yugioh.cards;

import com.bence.yugioh.YuGiOhGame;
import com.bence.yugioh.player.Player;
import com.bence.yugioh.utils.Texts;

/**
 * Varazs kartya kepesseg, mely egy kivalasztott szorny ATK-jat veglegesen megnoveli
 * @author Bence
 *
 */
public class AddATKEffect implements MagicEffect {
	private int _atk;
	
	public AddATKEffect(int atk){
		_atk = atk;
	}
	
	public boolean RequiresTarget() {
		return true;
	}

	public String GetDescription() {
		return Texts.AddATKEffectText.replace("{0}", String.valueOf(_atk));
	}

	@Override
	public void Activate(YuGiOhGame game, Player by) {
	}

	@Override
	public void ActivateOnTarget(Card target, YuGiOhGame game) {
		((CardMonster)target).Attack += _atk;
	}
}
