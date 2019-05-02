/**
 *
 */
package com.airsltd.aga.ranking.core.function.special;

/**
 * Enumerator describing whether a series of games requires a limit.
 * <p>
 * If a set of N games consists of nothing but wins or losses, a limit is needed.
 * 
 * @author Jon Boley
 *
 */
public enum LimitSet {

	/**
	 * All games were lost.
	 * <p>
	 * Limit needed.
	 */
	LOSTALL,
	/**
	 * All games were won.
	 * <p>
	 * Limit needed.
	 */
	WONALL, 
	/**
	 * Games were both won and lost.
	 * <p>
	 * No limit needed.
	 */
	MIXED;

	/**
	 * Return if a limit is required.
	 * 
	 * @return true if this is not MIXED.
	 */
	public boolean limitNeeded() {
		return this != MIXED;
	}

}
