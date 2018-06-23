/**
 *
 */
package com.airsltd.aga.ranking.core.engine;

import com.airsltd.aga.ranking.core.data.RankGame;
import com.airsltd.core.collections.BooleanFunction;

/**
 * @author Jon Boley
 *
 */
public class ModelGameSelector implements BooleanFunction<RankGame> {

	@Override
	public Boolean apply(RankGame p_object) {
		return p_object.getState().isModelled();
	}

}
