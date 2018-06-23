/**
 * 
 */
package com.airsltd.aga.ranking.core.engine;

import com.airsltd.aga.ranking.core.data.GameState;
import com.airsltd.aga.ranking.core.data.RankGame;
import com.airsltd.aga.ranking.core.data.RankGameExt;
import com.airsltd.aga.ranking.core.model.GameExtModel;

/**
 * Games which have not been rated should not be processed (they don't have a trailing rating.)
 * 
 * @author Jon Boley
 *
 */
public class GameNotReadyRule extends GameRule {

	public GameNotReadyRule() {
		super(PRIMARYRULE, "GameNotReady");
	}

	/**
	 * Has the game been rated?
	 * 
	 * @param p_game  not null,l the RankGame in question
	 * @return GameState.UNKNOWN if the game has been rated, otherwise GameState.REJECTED
	 */
	/* (non-Javadoc)
	 * @see com.airsltd.aga.ranking.core.engine.GameRule#processGame(com.airsltd.aga.ranking.core.data.RankGame)
	 */
	@Override
	protected GameState processGame(RankGame p_game) {
		RankGameExt l_gameExt = GameExtModel.getInstance().getElement(p_game);
		return (l_gameExt.getTrailingQuality()!=null)?GameState.UNKNOWN:GameState.NOTREADY;
	}

}
