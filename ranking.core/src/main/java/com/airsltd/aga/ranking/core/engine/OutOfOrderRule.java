/**
 *
 */
package com.airsltd.aga.ranking.core.engine;

import java.util.List;

import com.airsltd.aga.ranking.core.data.GameState;
import com.airsltd.aga.ranking.core.data.RankGame;
import com.airsltd.aga.ranking.core.model.GameModel;

/**
 * Mark a game that has processed games after it as Out of Order.
 * <p>
 * This should only happen when a game is added to the database after a run has
 * happened. Manual intervention by the Ranking coordinator is needed.
 *
 * @author Jon Boley
 *
 */
public class OutOfOrderRule extends GameRule {

	public OutOfOrderRule() {
		super(PRIMARYRULE, "OutOfOrder");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.airsltd.aga.ranking.core.engine.GameRule#processGame(com.airsltd.aga.
	 * ranking.core.function.EvaluationContext)
	 */
	@Override
	protected GameState processGame(RankGame p_game) {
		GameState l_retVal = GameState.UNKNOWN;
		final GameModel l_model = GameModel.getInstance();
		final List<RankGame> l_playerGames = l_model.getContentAsList(p_game.getPlayer());
		boolean l_found = false;
		for (final RankGame l_currentGame : l_playerGames) {
			if (l_found) {
				if (l_currentGame.getState() == GameState.PROCESSED) {
					l_retVal = GameState.OUTOFORDER;
					break;
				}
			} else {
				l_found = l_currentGame == p_game;
			}
		}
		return l_retVal;
	}

}
