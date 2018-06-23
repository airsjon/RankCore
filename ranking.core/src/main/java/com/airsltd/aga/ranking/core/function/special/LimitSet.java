/**
 *
 */
package com.airsltd.aga.ranking.core.function.special;

/**
 * @author Jon Boley
 *
 */
public enum LimitSet {

	LOSTALL, WONALL, MIXED;

	public boolean limitNeeded() {
		return this != MIXED;
	}

}
