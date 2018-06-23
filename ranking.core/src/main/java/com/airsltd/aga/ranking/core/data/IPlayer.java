/**
 * Copyright Jon Boley 2014
 *
 */
package com.airsltd.aga.ranking.core.data;

import java.util.Date;
import java.util.List;

/**
 * A distinct player for the AGA Database but abstracted to remove personal
 * information.
 *
 * @author Jon Boley
 *
 */
public interface IPlayer {

	/**
	 * Return the games played between {@code p_from} and {@code p_to}
	 * 
	 * @param p_from
	 * @param p_to
	 * @return a {@link List} of {@link IGame}
	 */
	List<IGame> gamesPlayed(Date p_from, Date p_to);

	/**
	 * Return the id for the player. Note: this is not the AGA Number of the
	 * player. Each players is assigned random Id to remove bias in selecting
	 * algorithms.
	 *
	 * @return
	 */
	int getId();

}
