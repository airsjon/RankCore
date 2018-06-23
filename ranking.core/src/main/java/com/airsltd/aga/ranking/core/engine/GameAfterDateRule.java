/**
 * 
 */
package com.airsltd.aga.ranking.core.engine;

import java.sql.Date;

import com.airsltd.aga.ranking.core.data.GameState;
import com.airsltd.aga.ranking.core.data.RankGame;

/**
 * Exclude all games before a particular date.
 * 
 * This is used to provide an initial global cutoff date for all games.
 * 
 * @author Jon Boley
 *
 */
public class GameAfterDateRule extends GameRule {

	private Date f_date;

	public GameAfterDateRule(Date p_date) {
		super(GameRule.PRIMARYRULE, "AfterDate");
		f_date = p_date;
	}

	/**
	 * Determine if the Game was played before a particular date and, if so, reject it.
	 * 
	 * @param p_game  not null, the game in question
	 * @return GameState.REJECTED if the game is played before f_date, otherwise GameState.UNKNOWN 
	 */
	/* (non-Javadoc)
	 * @see com.airsltd.aga.ranking.core.engine.GameRule#processGame(com.airsltd.aga.ranking.core.data.RankGame)
	 */
	@Override
	protected GameState processGame(RankGame p_game) {
		return (p_game.getDate().before(f_date))?GameState.REJECTED:GameState.UNKNOWN;
	}

}
