/**
 * 
 */
package com.airsltd.aga.ranking.core.engine;

import java.sql.Date;

import com.airsltd.aga.ranking.core.data.GameState;
import com.airsltd.aga.ranking.core.data.RankGame;

/**
 * Process games before a certain date.
 * 
 * @author Jon Boley
 *
 */
public class GameBeforeDate extends GameRule {

	private Date f_date;

	public GameBeforeDate(Date p_date) {
		super(GameRule.PRIMARYRULE, "BeforeDate");
		f_date = p_date;
	}

	/**
	 * Determine if the game has been played before a certain date.
	 * 
	 * @param p_game  not null, the RankGame in question
	 * @return GameState.UNKNOWN if the game is before f_date, GameState.NOTREADY otherwise.
	 */
	/* (non-Javadoc)
	 * @see com.airsltd.aga.ranking.core.engine.GameRule#processGame(com.airsltd.aga.ranking.core.data.RankGame)
	 */
	@Override
	protected GameState processGame(RankGame p_game) {
		return (p_game.getDate().before(f_date))?GameState.UNKNOWN:GameState.NOTREADY;
	}

}
