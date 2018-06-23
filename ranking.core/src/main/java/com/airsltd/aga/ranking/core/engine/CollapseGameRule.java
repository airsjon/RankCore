/**
 *
 */
package com.airsltd.aga.ranking.core.engine;

import java.util.List;

import com.airsltd.aga.ranking.core.data.GameState;
import com.airsltd.aga.ranking.core.data.RankGame;
import com.airsltd.aga.ranking.core.model.GameModel;
import com.airsltd.aga.ranking.core.states.GameResult;
import com.airsltd.core.data.AutoIncrementField;

/**
 * The complex rule folds all games played between two players in a single
 * tournament at the same handicap/komi into one game.
 * <p>
 * The games that have been collapsed will be marked as
 * {@link GameState#COLLAPSED}. When the games are collapsed, the new game needs
 * to be analyzed.
 *
 * @author Jon Boley
 *
 */
public class CollapseGameRule extends GameRule {

	public CollapseGameRule() {
		super(PRIMARYRULE, "Collapsed");
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
		final GameModel l_model = GameModel.getInstance();
		final List<RankGame> l_similar = l_model.findSimilarGames(p_game);
		return l_similar.size() > 1 ? collapseSimilar(p_game, l_model, l_similar) : GameState.UNKNOWN;
	}

	protected GameState collapseSimilar(RankGame l_game, GameModel l_model, List<RankGame> l_similar) {
		GameState l_retVal;
		int f_won = 0;
		for (final RankGame l_similarGame : l_similar) {
			switch (l_similarGame.getResult()) {
			case PLAYERWON:
				f_won++;
				break;
			case PLAYERLOST:
				f_won--;
				break;
			default:
			}
			final RankGame l_copy = new RankGame(l_similarGame);
			l_similarGame.setState(GameState.COLLAPSED);
			l_similarGame.setReason(getName());
			l_model.updateContent(l_copy, l_similarGame);
		}
		if (f_won != 0) {
			final RankGame l_collapsed = new RankGame(l_game);
			l_collapsed.setInternalId(new AutoIncrementField(RankGame.class));
			l_collapsed.setResult(GameResult.fromDelta(f_won));
			l_collapsed.setState(GameState.REANALYZE);
			l_collapsed.setReason("Reanalyze");
			l_model.addContent(l_collapsed);
		}
		l_retVal = GameState.COLLAPSED;
		return l_retVal;
	}

}
