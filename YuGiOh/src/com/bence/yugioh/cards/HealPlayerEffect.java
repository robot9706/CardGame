package com.bence.yugioh.cards;

import com.bence.yugioh.YuGiOhGame;
import com.bence.yugioh.player.Player;

/**
 * Varázykártya képesség, mely a használóját gyógyítja.
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
		return "A kártya használója kap " + String.valueOf(_heal) + " életerõ pontot.";
	}

	@Override
	public void Activate(YuGiOhGame game, Player by) {
		by.Health += _heal;
	}

	@Override
	public void ActivateOnTarget(Card target, YuGiOhGame game) {
	}
}
