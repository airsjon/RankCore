/**
 *
 */
package com.airsltd.aga.ranking.core.engine;

import com.airsltd.aga.ranking.core.data.GameState;
import com.airsltd.aga.ranking.core.data.RankGame;
import com.airsltd.core.collections.BooleanFunction;

/**
 * Selector method for games to be processed when doing an incremental update.
 *
 * @author Jon Boley
 *
 */
public class IncrementalGameSelector implements BooleanFunction<RankGame> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.airsltd.core.collections.IBooleanMethod#isTrue(java.lang.Object)
	 */
	@Override
	public Boolean apply(RankGame p_object) {
		return p_object.getState() == GameState.VALID;
	}

}
