/**
 *
 */
package com.airsltd.aga.ranking.core.engine;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.airsltd.aga.ranking.core.data.GameState;
import com.airsltd.aga.ranking.core.data.RankGame;
import com.airsltd.aga.ranking.core.model.GameModel;

/**
 * Process a rule on a given EvaluationContext.
 * <p>
 * The evaluation context is created by {@link GameIterator}.
 *
 * @author Jon Boley
 *
 */
public abstract class GameRule extends AbstractRule<GameState, RankGame> {

	private static Log LOGGER = LogFactory.getLog(GameRule.class);

	public GameRule(int p_category, String p_name) {
		super(p_category, p_name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.airsltd.aga.ranking.core.engine.IRule#process(com.airsltd.aga.ranking
	 * .core.function.EvaluationContext)
	 */
	@Override
	public boolean process(RankGame p_game) {
		boolean l_retVal = false;
		LOGGER.trace("Apply: " + getName());
		final GameState l_state = processGame(p_game);
		LOGGER.trace("Returned: " + l_state);
		if (l_state != GameState.UNKNOWN) {
			final RankGame l_newData = new RankGame(p_game);
			l_newData.setState(l_state);
			l_newData.setReason(getName());
			l_state.record();
			GameModel.getInstance().updateContent(p_game, l_newData);
			l_retVal = true;
		}
		return l_retVal;
	}

	protected abstract GameState processGame(RankGame p_game);
}
