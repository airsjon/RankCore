/**
 * Copyright Jon Boley 2014
 */
package com.airsltd.aga.ranking.core.data;

import com.airsltd.aga.ranking.core.states.GameResult;

/**
 * @author Jon Boley
 *
 */
public interface IGame {

	/**
	 * Return the tournament that the game was played in. A returned value of
	 * <code>null</code> represents a self-paired game.
	 *
	 * @return the tournament that the game was played in
	 */
	ITournament getTournament();

	/**
	 * If the game was played in a tournament, return the round that the game
	 * was played in.
	 *
	 * @return
	 */
	int getRound();

	int getHandicap();

	int getKomi();

	GameResult getResult();
}
