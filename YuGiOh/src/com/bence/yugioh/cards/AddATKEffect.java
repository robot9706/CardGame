package com.bence.yugioh.cards;

import com.bence.yugioh.YuGiOhGame;
import com.bence.yugioh.player.Player;

/**
 * Varázs kártya képesség, mely egy kiválasztott szörny ATK-ját véglegesen megnöveli
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
		return "Egy kiválasztott szörnynek ad " + String.valueOf(_atk) + " támadási erõt.";
	}

	@Override
	public void Activate(YuGiOhGame game, Player by) {
	}

	@Override
	public void ActivateOnTarget(Card target, YuGiOhGame game) {
		((CardMonster)target).Attack += _atk;
	}
}
