/**
 * 
 */
package com.airsltd.aga.ranking.core.engine;

import com.airsltd.aga.ranking.core.data.GameState;
import com.airsltd.aga.ranking.core.data.RankGame;

/**
 * Check if a Game was played at a higher handicap than 9.
 * 
 * @author Jon Boley
 *
 */
public class InvalidHandicapRule extends GameRule {

	public InvalidHandicapRule() {
		super(PRIMARYRULE, "InvalidHandicap");
	}

	/**
	 * Test if Handicap is greater than 9.
	 * 
	 * @param p_game  not null, the RankGame to be checked.
	 * @return either {@link GameState}.REJECTED or {@link GameState}.UNKNOWN
	 */
	/* (non-Javadoc)
	 * @see com.airsltd.aga.ranking.core.engine.GameRule#processGame(com.airsltd.aga.ranking.core.data.RankGame)
	 */
	@Override
	protected GameState processGame(RankGame p_game) {
		return (p_game.getHandicap() > 9)?GameState.REJECTED:GameState.UNKNOWN;
	}

}
