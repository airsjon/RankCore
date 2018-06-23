/**
 * 
 */
package com.airsltd.aga.ranking.core.engine;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.airsltd.aga.ranking.core.data.GameState;
import com.airsltd.aga.ranking.core.data.RankGame;
import com.airsltd.aga.ranking.core.data.RankGameExt;
import com.airsltd.aga.ranking.core.model.GameExtModel;

/**
 * Determine if the game was valid due to the strength of the opponent and handicap.
 * 
 * @author Jon Boley
 *
 */
public class GameThresholdRule extends GameRule {

	private static Log LOGGER = LogFactory.getLog(GameThresholdRule.class);

	private float f_threshold;

	public GameThresholdRule(float p_threshold) {
		super(GameRule.PRIMARYRULE, "Threshold");
		f_threshold = p_threshold;
	}

	/**
	 * If the probability of p_game falls in the {@link #threshold(float)} then pass the game.
	 * 
	 * @param p_game  not null, the game in question
	 * @return GameState.Valid if the game is inside the threshold, otherwise GameState.REJECTED
	 */
	/* (non-Javadoc)
	 * @see com.airsltd.aga.ranking.core.engine.GameRule#processGame(com.airsltd.aga.ranking.core.data.RankGame)
	 */
	@Override
	protected GameState processGame(RankGame p_game) {
		GameState l_retVal = GameState.VALID;
		RankGameExt l_gameExt = GameExtModel.getInstance().getElement(p_game);
		if (l_gameExt.getProbability()==null || l_gameExt.getTrailingProbability()==null) {
			l_retVal = GameState.REJECTED;
			LOGGER.warn("Game "+p_game+" with ext values "+l_gameExt+" has ill defined probabilities");
		} else {
			l_retVal = (threshold(l_gameExt.getProbability()) && threshold(l_gameExt.getTrailingProbability()))?
				GameState.REJECTED:GameState.VALID;
		}
		return l_retVal;
	}
	
	/**
	 * Return true if the game is within the threshold.
	 * <br>
	 * All games that have a smaller chance of f_threshold of being won by either player are rejected.
	 * @param p_probability  double, representing the probability that the current player will win the game
	 * @return true if the probability lies in the range [f_threshold, 1-f_threshold]
	 */
	private boolean threshold(double p_probability) {
		return p_probability<f_threshold || p_probability>(1-f_threshold);
	}

}
