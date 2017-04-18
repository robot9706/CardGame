package com.bence.yugioh.cards;

import com.bence.yugioh.YuGiOhGame;
import com.bence.yugioh.player.Player;

/**
 * Var�zyk�rtya k�pess�g, mely a haszn�l�j�t gy�gy�tja.
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
		return "A k�rtya haszn�l�ja kap " + String.valueOf(_heal) + " �leter� pontot.";
	}

	@Override
	public void Activate(YuGiOhGame game, Player by) {
		by.Health += _heal;
	}

	@Override
	public void ActivateOnTarget(Card target, YuGiOhGame game) {
	}
}
