package com.bence.yugioh.cards;

import com.bence.yugioh.YuGiOhGame;
import com.bence.yugioh.player.Player;

public class AddDEFEffect implements MagicEffect {
private int _def;
	
	public AddDEFEffect(int def){
		_def = def;
	}
	
	public boolean RequiresTarget() {
		return true;
	}

	public String GetDescription() {
		return "Egy kiv�lasztott sz�rnynek ad " + String.valueOf(_def) + " v�dekez�si er�t.";
	}

	@Override
	public void Activate(YuGiOhGame game, Player by) {
	}

	@Override
	public void ActivateOnTarget(Card target, YuGiOhGame game) {
		((CardMonster)target).Defense += _def;
	}
}
