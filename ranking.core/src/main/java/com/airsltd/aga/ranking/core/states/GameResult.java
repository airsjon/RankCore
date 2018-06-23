/**
 * Copyright Jon Boley 2014
 */
package com.airsltd.aga.ranking.core.states;

import com.airsltd.core.IPrettyObject;

/**
 * Valid Game Results.
 *
 * @author Jon Boley
 *
 */
public enum GameResult implements IPrettyObject {
	/**
	 * The player won the game.
	 */
	PLAYERWON("Won"), /**
				 * The player lost the game.
				 */
	PLAYERLOST("Lost"), /**
				 * The player forfeit the game.
				 */
	PLAYERFORFEIT("Forfeit"), /**
					 * The player's opponent forfeited the game.
					 */
	OPPONENTFORFEIT("Opponent Forfeit"), /**
						 * Both players forfeited the game.
						 */
	BOTHFORFEIT("Both Forfeit"), /**
					 * There was no result for the game.
					 */
	NORESULT("Unknown");

	private String f_prettyName;

	private GameResult(String p_prettyName) {
		f_prettyName = p_prettyName;
	}
	
	public static GameResult fromDelta(int p_won) {
		return p_won < 0 ? PLAYERLOST : PLAYERWON;
	}
	
	@Override
	public String niceString() {
		return f_prettyName;
	}
}
