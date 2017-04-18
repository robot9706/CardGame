package com.bence.yugioh.cards;

import com.bence.yugioh.YuGiOhGame;
import com.bence.yugioh.player.Player;
import com.bence.yugioh.utils.Texts;

/**
 * Varazykartya kepesseg, mely a hasznalojat gyogyitja.
 * @author Bence
 *
 */
public class HealPlayerEffect implements MagicEffect {
	private int _heal;
	
	public HealPlayerEffect(int heal){
		_heal = heal;
	}
	
	public boolean RequiresTarget() {
		return false;
	}

	@Override
	public String GetDescription() {
		return Texts.HealPlayerText.replace("{0}", String.valueOf(_heal));
	}

	@Override
	public void Activate(YuGiOhGame game, Player by) {
		by.Health += _heal;
	}

	@Override
	public void ActivateOnTarget(Card target, YuGiOhGame game) {
	}
}
