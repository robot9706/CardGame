package com.bence.yugioh.cards;

import com.bence.yugioh.YuGiOhGame;
import com.bence.yugioh.player.Player;

public interface MagicEffect {
	boolean RequiresTarget();
	String GetDescription();
	
	void Activate(YuGiOhGame game, Player by);
	void ActivateOnTarget(Card target, YuGiOhGame game);
}
