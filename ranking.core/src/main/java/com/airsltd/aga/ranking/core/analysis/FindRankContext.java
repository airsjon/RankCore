/**
 *
 */
package com.airsltd.aga.ranking.core.analysis;

import java.util.ArrayList;
import java.util.List;

import com.airsltd.aga.ranking.core.data.Color;
import com.airsltd.aga.ranking.core.data.RankGame;
import com.airsltd.aga.ranking.core.function.special.MovingAverageValue;
import com.airsltd.aga.ranking.core.function.special.ProbabilityAs;
import com.airsltd.aga.ranking.core.model.GameExtModel;
import com.airsltd.aga.ranking.core.states.GameResult;

/**
 * A context that is used to evaluate the rank possibilities.
 *
 * @author Jon Boley
 *
 */
public class FindRankContext {

	private static final int GAMESLICE = 6;

	private static final int DEFKOMI = 7;

	private static final int GAME5 = 5;

	private static final int GAME6 = 6;

	private static final double INITIALMAXRATING = -100f;

	private static final int TWOWINS = 2;

	private static final Integer HALFRANK = 50;

	private static final int RANKROUND = 100;

	private final ProbabilityAs f_probabilityAs = new ProbabilityAs();

	private List<RankResult> f_results = new ArrayList<>();
	private final RankResult[] f_currentResults = new RankResult[] { null, null };
	private int f_wins = 0;
	private double f_maxRating = INITIALMAXRATING;
	private MovingAverageValue<RankGame> f_gameAverage = new MovingAverageValue<RankGame>(GAMESLICE);

	private final com.airsltd.aga.ranking.core.analysis.Result[] f_result = new com.airsltd.aga.ranking.core.analysis.Result[] {
			null, null };

	private static int s_gameIndex = 10000;

	/**
	 * Generate a FindRankContext to monitor the possible ranks that can be
	 * obtained with the four games input.
	 *
	 * @param p_gamesPlayed
	 * @param p_wins
	 */
	public FindRankContext(List<RankGameInput> p_gamesPlayed, int p_wins) {
		switch (p_wins) {
		case 0:
			f_result[0] = com.airsltd.aga.ranking.core.analysis.Result.TWOLOSSES;
			break;
		case 1:
			f_wins = 1;
			f_result[0] = com.airsltd.aga.ranking.core.analysis.Result.LOSSANDWIN;
			f_result[1] = com.airsltd.aga.ranking.core.analysis.Result.WINANDLOSS;
			break;
		case TWOWINS:
		default:
			f_wins = TWOWINS;
			f_result[0] = com.airsltd.aga.ranking.core.analysis.Result.TWOWINS;
		}
		generateGames(p_gamesPlayed);
	}

	/**
	 * Create the games that need to be processed
	 *
	 * @param p_gamesPlayed
	 */
	protected void generateGames(List<RankGameInput> p_gamesPlayed) {
		int l_index = 0;
		for (final RankGameInput l_gameInput : p_gamesPlayed) {
			final RankGame l_game = new RankGame(l_index + 1, l_gameInput.getHandicap(), l_gameInput.getKomi(),
					l_gameInput.isColor() ? Color.WHITE : Color.BLACK, s_gameIndex ++);
			l_game.setResult(l_gameInput.getResultGame() == com.airsltd.aga.ranking.core.analysis.Result.WIN
					? GameResult.PLAYERWON : GameResult.PLAYERLOST);
			f_gameAverage.addValue(l_game);
			GameExtModel.getInstance().getElement(l_game).setOpponentsTrailingRating(l_gameInput.getRating());
			if (l_gameInput.getRating() > f_maxRating) {
				f_maxRating = l_gameInput.getRating();
			}
			l_index++;
		}
		boolean l_extraGame1Win = false;
		boolean l_extraGame2Win = false;
		switch (f_wins) {
		case TWOWINS:
			l_extraGame1Win = true;
			l_extraGame2Win = true;
			break;
		case 1:
			l_extraGame1Win = true;
			break;
		case 0:
		default:
		}
		final RankGame l_game5 = new RankGame(GAME5, 0, DEFKOMI, Color.BLACK, s_gameIndex++);
		l_game5.setResult(l_extraGame1Win ? GameResult.PLAYERWON : GameResult.PLAYERLOST);
		f_gameAverage.addValue(l_game5);
		GameExtModel.getInstance().getElement(l_game5).setOpponentsTrailingRating(0d);
		final RankGame l_game6 = new RankGame(GAME6, 0, DEFKOMI, Color.WHITE, s_gameIndex++);
		l_game6.setResult(l_extraGame2Win ? GameResult.PLAYERWON : GameResult.PLAYERLOST);
		GameExtModel.getInstance().getElement(l_game6).setOpponentsTrailingRating(0d);
		f_gameAverage.addValue(l_game6);
	}

	/**
	 * Determine the rank that would be obtained playing against game strengths
	 * of p_gameOneRating and p_gameTwoRating. This Rank will then be stored in
	 * the context for later retrievel
	 *
	 * @param p_gameOneRating
	 * @param p_gameTwoRating
	 */
	public void process(double p_gameOneRating, double p_gameTwoRating) {
		processInternal(p_gameOneRating, p_gameTwoRating, 0);
		if (f_wins == 1) {
			processInternal(p_gameTwoRating, p_gameOneRating, 1);
		}
	}

	// TODO removal of f_ratings destroys this analysis needs to be fixed.
	private void processInternal(double p_gameOneRating, double p_gameTwoRating, int p_index) {
		GameExtModel.getInstance().getElement(f_gameAverage.get(GAME5 - 1)).setOpponentsTrailingRating(p_gameOneRating);
		GameExtModel.getInstance().getElement(f_gameAverage.get(GAME6 - 1)).setOpponentsTrailingRating(p_gameTwoRating);
		final double l_retRank = f_probabilityAs.evaluate(f_gameAverage);
		final int l_rank = (int) Math
				.floor((l_retRank - HALFRANK) / RANKROUND);
		if (f_currentResults[p_index] == null || l_rank > f_currentResults[p_index].getRank()) {
			final RankResult l_newRankResult = new RankResult(p_index == 1 ? p_gameTwoRating : p_gameOneRating,
					p_index == 1 ? p_gameOneRating : p_gameTwoRating, f_result[p_index], l_rank);
			f_currentResults[p_index] = l_newRankResult;
			f_results.add(l_newRankResult);
		}
	}

	/**
	 * Clear the current results (needed when resetting the starting analysis
	 * point.)
	 *
	 */
	public void clearCurrent() {
		f_currentResults[0] = null;
		f_currentResults[1] = null;
	}

	/**
	 * @return the results
	 */
	public List<RankResult> getResults() {
		return f_results;
	}

	/**
	 * @param p_results
	 *            the results to set
	 */
	public void setResults(List<RankResult> p_results) {
		f_results = p_results;
	}

	/**
	 * @return the gameAverage
	 */
	public MovingAverageValue<RankGame> getGameAverage() {
		return f_gameAverage;
	}

	/**
	 * @param p_gameAverage
	 *            the gameAverage to set
	 */
	public void setGameAverage(MovingAverageValue<RankGame> p_gameAverage) {
		f_gameAverage = p_gameAverage;
	}

}
