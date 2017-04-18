package com.bence.yugioh.cards;

import com.bence.yugioh.YuGiOhGame;
import com.bence.yugioh.player.Player;

/**
 * Var�zs k�rtya k�pess�g, mely egy kiv�lasztott sz�rny ATK-j�t v�glegesen megn�veli
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
		return "Egy kiv�lasztott sz�rnynek ad " + String.valueOf(_atk) + " t�mad�si er�t.";
	}

	@Override
	public void Activate(YuGiOhGame game, Player by) {
	}

	@Override
	public void ActivateOnTarget(Card target, YuGiOhGame game) {
		((CardMonster)target).Attack += _atk;
	}
}
