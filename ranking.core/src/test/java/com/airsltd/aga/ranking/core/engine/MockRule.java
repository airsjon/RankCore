/**
 * 
 */
package com.airsltd.aga.ranking.core.engine;

import com.airsltd.aga.ranking.core.data.GameState;
import com.airsltd.aga.ranking.core.data.RankGame;

/**
 * For testing purpose.
 * 
 * @author Jon Boley
 *
 */
public class MockRule extends AbstractRule<GameState, RankGame> {

	public MockRule(int p_i, String p_string) {
		super(p_i, p_string);
	}

	public MockRule(String p_string) {
		super(p_string);
	}
	
	public MockRule() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see com.airsltd.aga.ranking.core.engine.IRule#process(com.airsltd.aga.ranking.core.function.EvaluationContext)
	 */
	@Override
	public boolean process(RankGame p_context) {
		return false;
	}

}
