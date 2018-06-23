/**
 *
 */
package com.airsltd.aga.ranking.core.data;

import java.util.Date;

/**
 * @author jon
 *
 */
public interface ITournament {

	/**
	 * Start date of the tournament
	 *
	 * @return
	 */
	Date startDate();

	/**
	 * End date of the tournament
	 *
	 * @return
	 */
	Date endDate();

	/**
	 * Date on which the round finished.
	 *
	 * @param round
	 * @return
	 */
	Date roundDate(int p_round);

	/**
	 * Number of rounds in the tournament.
	 *
	 * @return
	 */
	int rounds();

}
