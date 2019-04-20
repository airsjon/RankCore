/**
 *
 */
package com.airsltd.aga.ranking.core.engine;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.airsltd.aga.ranking.core.data.GameState;
import com.airsltd.aga.ranking.core.data.RankGame;
import com.airsltd.aga.ranking.core.model.GameModel;

/**
 * For each game that has not been processed, create an evaluation context.
 * <p>
 * The Evaluation context must have the following values setup.
 * <table border="1">
 * <tr>
 * <th>Name</th>
 * <th>Description</th>
 * </tr>
 * <tr>
 * <td>game_record</td>
 * <td>The Current Game being analyzed
 * <td>
 * </tr>
 * <tr>
 * <td>GameState{Value}</td>
 * <td>Each Game State state is stored as a GameState{Value} equal to it's
 * ordinal value</td>
 * </tr>
 * </table>
 * 
 * @author Jon Boley
 *
 */
public class GameIterator implements AutoCloseable, Iterator<RankGame> {

	private Deque<RankGame> f_games;
	private static Log LOGGER = LogFactory.getLog(GameIterator.class);

	public GameIterator(boolean p_reanalyze) {
		setupDeque(p_reanalyze);
	}

	protected void setupDeque(boolean p_reanalyze) {
		GameModel.getInstance().startBlock();
		final Set<RankGame> l_currentGames = GameModel.getInstance().getContentAsList();
		f_games = new ArrayDeque<RankGame>();
		for (final RankGame l_game : l_currentGames) {
			if (p_reanalyze ? l_game.getState() == GameState.REANALYZE : l_game.getState().isRedo()) {
				l_game.setState(GameState.UNKNOWN);
				f_games.addLast(l_game);
			}
		}
	}

	/**
	 * Determine if there is a RankGame of UNKNOWN state on the deque.
	 * <p>
	 * State may be changed by complex rules (for example the Collpase Rule.) We
	 * check if the state has been changed and if so remove it from the deque.
	 *
	 * @return true if there is a RankGame to be processed.
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		while (!f_games.isEmpty() && f_games.peekFirst().getState() != GameState.UNKNOWN) {
			f_games.pollFirst();
		}
		return !f_games.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#next()
	 */
	@Override
	public RankGame next() {
		RankGame l_pollFirst = f_games.pollFirst();
		LOGGER.trace("Game: " + l_pollFirst.getAgaGameId() + ":" + l_pollFirst.getId());
		return l_pollFirst;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#remove()
	 */
	@Override
	public void remove() {
		f_games.pollFirst();
	}

	@Override
	public void close() throws Exception {
		try {
			GameModel.getInstance().endBlock();
		} finally {
			GameModel.getInstance().cancelBlock();
		}
	}

	/**
	 * @return the games
	 */
	public Deque<RankGame> getGames() {
		return f_games;
	}

}
