/**
 *
 */
package com.airsltd.aga.ranking.core.data;

import java.util.EnumSet;
import java.util.Set;

/**
 * An enumerated set of this will give a list of all the games won/lost.
 * <p>
 * Normally, all the games lost will be placed first. If GameSix is not set then
 * no other games should be set.
 *
 * @author Jon Boley
 *
 */
public enum GamesWon {
	GameOne, GameTwo, GameThree, GameFour, GameFive, GameSix;

	/**
	 * Add to the Set all the games that have been won.
	 * <br>
	 * Games are ordered with wins first and losses last.
	 * 
	 * @param p_gamesWon  total number of games won
	 * @return a set of the games won
	 */
	public static Set<GamesWon> totalGames(int p_gamesWon) {
		Set<GamesWon> l_retVal = EnumSet.noneOf(GamesWon.class);
		if (p_gamesWon > 0) {
			l_retVal = EnumSet.range(GameOne, GamesWon.values()[p_gamesWon - 1]);
		}
		return l_retVal;
	}
}
