package com.bence.yugioh.phases;

import com.bence.yugioh.YuGiOhGame;

public class AttackPhase extends GamePhase {
	public AttackPhase(YuGiOhGame game){
		super(game);
		
		Name = "Támadó fázis";
	}

	public void GotoNextPhase() {
	}
}
