/**
 *
 */
package com.airsltd.aga.ranking.core.analysis;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Based on a set of games (4) we return all the ways a rank can be obtained
 * from the next two games.
 *
 * @author Jon Boley
 *
 */
public class FindRanks implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -8334269675420173361L;

	private static final int TWOWINS = 2;

	private static final int THREEWINS = 3;

	private static final int FOURWINS = 4;

	private static final int SPANGRANUALITY = 10;

	private static final float RATINGINC = .1f;

	private static final int INITIALHIGHBOUND = -20;

	private static final int RANKROUNDER = 100;

	private static Map<Integer, FindRanks> s_rankProcessors = new HashMap<>();

	private int f_currentIndex = -1;
	private float f_totalSpan;
	private int f_rankId = new Random().nextInt();

	public FindRanks() {
		s_rankProcessors.put(f_rankId, this);
	}

	/**
	 * Determine the possible Ranks achieved for initial games p_gamePlayed with
	 * a range limit of p_range.
	 *
	 * @param p_gamesPlayed
	 *            not null, the games played
	 * @param l_range
	 *            an integer limiting the range of games to limit the search for
	 * @return a list of all the {@link RankResult}s generated for this game
	 *         subset.
	 */
	public List<RankResult> evaluate(List<RankGameInput> p_gamesPlayed, int p_range) {
		final List<RankResult> l_retVal = new ArrayList<>();
		final int l_gamesWon = gamesWon(p_gamesPlayed);
		final List<FindRankContext> l_contexts = new ArrayList<>();
		if (l_gamesWon >= TWOWINS) {
			l_contexts.add(new FindRankContext(p_gamesPlayed, TWOWINS));
			if (l_gamesWon >= THREEWINS) {
				l_contexts.add(new FindRankContext(p_gamesPlayed, 1));
				if (l_gamesWon == FOURWINS) {
					l_contexts.add(new FindRankContext(p_gamesPlayed, 0));
				}
			}
		}
		final int l_highBound = getHighbound(p_gamesPlayed, p_range);
		final int l_lowBound = getLowbound(p_gamesPlayed, p_range);
		f_totalSpan = (l_highBound - l_lowBound) * SPANGRANUALITY;
		f_totalSpan *= f_totalSpan;
		f_currentIndex = 0;
		for (float l_gameOneRating = l_lowBound; l_gameOneRating < l_highBound; l_gameOneRating += RATINGINC) {
			for (final FindRankContext l_context : l_contexts) {
				l_context.clearCurrent();
			}
			for (float l_gameTwoRating = l_lowBound; l_gameTwoRating < l_highBound; l_gameTwoRating += RATINGINC) {
				for (final FindRankContext l_context : l_contexts) {
					l_context.process(l_gameOneRating, l_gameTwoRating);
				}
				f_currentIndex++;
			}
		}
		for (final FindRankContext l_context : l_contexts) {
			l_retVal.addAll(l_context.getResults());
		}
		Collections.sort(l_retVal);
		f_currentIndex++;
		return l_retVal;
	}

	protected int getLowbound(List<RankGameInput> p_gamesPlayed, int p_range) {
		int l_retVal = SPANGRANUALITY;
		for (final RankGameInput l_rankGame : p_gamesPlayed) {
			if (Math.floor(l_rankGame.getActualStrength()) - p_range < l_retVal) {
				l_retVal = (int) Math.floor(l_rankGame.getActualStrength()) - p_range;
			}
		}
		return l_retVal;
	}

	protected int getHighbound(List<RankGameInput> p_gamesPlayed, int p_range) {
		int l_retVal = INITIALHIGHBOUND;
		for (final RankGameInput l_rankGame : p_gamesPlayed) {
			if (Math.floor(l_rankGame.getActualStrength() + 1f) + p_range > l_retVal) {
				l_retVal = (int) Math.floor(l_rankGame.getActualStrength() + 1f) + p_range;
			}
		}
		return l_retVal;
	}

	private int gamesWon(List<RankGameInput> p_gamesPlayed) {
		int l_retVal = 0;
		for (final RankGameInput l_game : p_gamesPlayed) {
			if (l_game.getResultGame() == com.airsltd.aga.ranking.core.analysis.Result.WIN) {
				l_retVal++;
			}
		}
		return l_retVal;
	}

	public int getPercentDone() {
		int l_retVal = 0;
		if (f_totalSpan != 0 && f_currentIndex >= 0 && f_currentIndex <= f_totalSpan) {
			l_retVal = (int) Math.floor(f_currentIndex * RANKROUNDER / f_totalSpan);
		} else {
			if (f_currentIndex > f_totalSpan) {
				l_retVal = RANKROUNDER;
			}
		}
		return l_retVal;
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeInt(f_rankId);
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		f_rankId = in.readInt();
	}

	private Object readResolve() throws ObjectStreamException {
		return s_rankProcessors.get(f_rankId);
	}

}
