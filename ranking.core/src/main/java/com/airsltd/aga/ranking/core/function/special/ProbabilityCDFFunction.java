/**
 *
 */
package com.airsltd.aga.ranking.core.function.special;

import java.util.Arrays;

import com.airsltd.aga.ranking.core.data.Color;
import com.airsltd.aga.ranking.core.data.RankGame;
import com.airsltd.aga.ranking.core.data.RankGameExt;
import com.airsltd.aga.ranking.core.model.GameExtModel;
import com.airsltd.aga.ranking.core.states.GameResult;
import com.airsltd.core.NotificationStatus;
import com.airsltd.core.data.CoreInterface;
import com.airsltd.core.data.Tuple;

/**
 * Return the value of a Continuous Distribution Function
 * 
 * @author Jon Boley
 *
 */
public abstract class ProbabilityCDFFunction {
	private static final int MINIMUMRATING = -20;
	private static final int MAXIMUMRATING = 20;
	private static final int MAXIMUMARRAYSIZE = 3200;
	protected static final int RANKASINTEGER = 100;
	protected static final double RANKTHRESHOLD = .1f;
	private static final double HALFDOUBLE = .5d;
	private static final double RATINGSPAN = 5.4f;
	private static final int RATINGSPANINTEGER = 540;

	/**
	 * Evaluate the CDF limit
	 * 
	 * @param p_ratings
	 * @param p_games
	 * @return
	 */
	public int evaluate(MovingAverageValue<RankGame> p_games) {
		int l_retVal = atThreshold();
		double l_probO = 0;
		/*
		 * Check for the value being cached.
		 */
		if (p_games.full()) {
			/*
			 * Get the minimum rating that is feasible and the maximum rating
			 * that is feasible. <p> 
			 * Minimum is equal to Max(Opponent Rating) |
			 * Game Won - 5.4 (to account for spread in 9 stone game.) <p>
			 * Maximum is equal to Min(Opponent Rating) | Game Lost + 5.4 (to account
			 * for the spread in 9 stone game.)
			 *
			 */

			final double[] l_probArray = new double[MAXIMUMARRAYSIZE];
			final LimitSet l_limitNeeded = needsLimit(p_games);
			final int l_size = p_games.getSize() + (l_limitNeeded.limitNeeded() ? 1 : 0);
			final RankGame[] l_gameList = new RankGame[l_size];
			final RankGameExt[] l_gameExtList = new RankGameExt[l_size];
			final double[] l_ratingList = new double[l_size];
			Arrays.fill(l_ratingList, -20f);
			int l_setupCnt = 0;
			for (final RankGame l_currentGame : p_games) {
				l_gameList[l_setupCnt] = l_currentGame;
				l_gameExtList[l_setupCnt] = GameExtModel.getInstance().getElement(l_currentGame);
				double l_gameRating =  l_currentGame.realRating(l_gameExtList[l_setupCnt].getOpponentsTrailingRating());
				if (checkThreshold(l_gameRating)) {
					return l_retVal;
				}
				l_ratingList[l_setupCnt++] = l_gameRating;
			}
			if (l_limitNeeded.limitNeeded()) {
				if (l_limitNeeded == LimitSet.LOSTALL) {
					/*
					 * Add a win
					 */
					l_gameList[l_setupCnt] = new RankGame(-1L, 0, 7, Color.WHITE, 0);
					l_gameExtList[l_setupCnt] = new RankGameExt(l_gameList[l_setupCnt]);
					l_gameList[l_setupCnt].setResult(GameResult.PLAYERWON);
					double l_minRating = minRating(l_ratingList);
					l_gameExtList[l_setupCnt].setTrailingQuality(l_minRating);
					l_ratingList[l_setupCnt++] = l_minRating;
				} else {
					/*
					 * Add a loss
					 */
					l_gameList[l_setupCnt] = new RankGame(-1L, 0, 7, Color.WHITE, 0);
					l_gameExtList[l_setupCnt] = new RankGameExt(l_gameList[l_setupCnt]);
					l_gameList[l_setupCnt].setResult(GameResult.PLAYERLOST);
					double l_maxRating = maxRating(l_ratingList);
					l_gameExtList[l_setupCnt].setTrailingQuality(l_maxRating);
					l_ratingList[l_setupCnt++] = l_maxRating;
				}
			}
			if (!ratingSpan(l_gameList, l_ratingList)) {
				long l_playerID = l_gameList[0].getPlayer().getPin_Player();
				long l_gameID = l_gameList[0].getId();
				CoreInterface.getSystem().handleException("Invalid range for player "+l_playerID+" @ "+l_gameID+" on ratings: " + Arrays.toString(l_ratingList),
						null, NotificationStatus.LOG);
			} else {
				final Tuple<Double, Double> l_cdf = getCDF(l_gameList, l_ratingList, l_probArray);
				for (final double l_prob : l_probArray) {
					l_probO += l_prob;
				}
				final double l_minRating = l_cdf.getPrimary();
				final double l_maxRating = l_cdf.getSecondary();
				l_retVal = determineThreshold(l_probArray, l_probO, l_minRating, l_maxRating, getDirection());
			}
		}
		return l_retVal;
	}

	protected abstract boolean getDirection();

	/**
	 * The value to return if the set of games exceeds the threshold.
	 * 
	 * @return int  not null, the value to return when the limit is obtained.
	 */
	abstract protected int atThreshold();

	/**
	 * Is the p_gameRating at threshold.
	 * 
	 * @param p_gameRating  floating point representation of the game rating.
	 * @return true if the p_gameRating is at threshold.
	 */
	abstract protected boolean checkThreshold(double p_gameRating);

	private boolean ratingSpan(RankGame[] p_gameList, double[] p_ratingList) {
		double l_maxRating = MAXIMUMRATING;
		double l_minRating = MINIMUMRATING;
		for (int l_setupCnt = 0; l_setupCnt != p_gameList.length; l_setupCnt++) {
			final RankGame l_currentGame = p_gameList[l_setupCnt];
			if (l_currentGame.getResult() == GameResult.PLAYERWON) {
				final double l_rateCheck = p_ratingList[l_setupCnt] + RATINGSPAN;
				if (l_rateCheck < l_maxRating) {
					l_maxRating = l_rateCheck;
				}
			} else {
				final double l_rateCheck = p_ratingList[l_setupCnt] - RATINGSPAN;
				if (l_rateCheck > l_minRating) {
					l_minRating = l_rateCheck;
				}
			}
		}
		final boolean l_retVal = l_maxRating >= l_minRating && l_maxRating - l_minRating <= 32;
		if (!l_retVal) {
			CoreInterface.getSystem().getLog().trace("Invalid Span");
		}
		return l_retVal;
	}

	/**
	 * 
	 * @param p_probArray
	 * @param p_probO
	 * @param p_minRating
	 * @param p_maxRating
	 * @param p_direction  boolean, true if positive else negative
	 * @return
	 */
	protected int determineThreshold(double[] p_probArray, double p_probO, double p_minRating,
			double p_maxRating, boolean p_direction) {
		int l_minRank = (int) (p_minRating * RANKASINTEGER);
		double l_probRO = 0f;
		for (int cnt = 0; cnt < (int) (p_maxRating * RANKASINTEGER - p_minRating * RANKASINTEGER); cnt++) {
			/*
			 * Calculate probabilty of the current outcomes.
			 */
			l_probRO += p_probArray[cnt];
			if (l_probRO / p_probO > RANKTHRESHOLD) {
				if (p_direction) {
					l_minRank += cnt;
				} else {
					l_minRank -= cnt + 1;
				}
				break;
			}
		}
		return l_minRank;
	}

	/**
	 * Return maximum value in an array of ratings based on the first n-1 elements.
	 * 
	 * Implementation note: this uses reduction and can benefit from parallel processing.
	 * PS. probably not necessary but also uses library code that is well tested.
	 * 
	 * @param p_ratingList
	 * @return the largest rating in the list
	 */
	protected double maxRating(double[] p_ratingList) {
		return Arrays.stream(p_ratingList,0,p_ratingList.length-1).max().getAsDouble();
	}

	/**
	 * Return minimum value in an array of ratings based on the first n-1 elements.
	 * 
	 * Implementation note: this uses reduction and can benefit from parallel processing.
	 * PS. probably not necessary but also uses library code that is well tested.
	 * 
	 * @param p_ratingList
	 * @return the smallest rating in the list
	 */
	protected double minRating(double[] p_ratingList) {
		return Arrays.stream(p_ratingList,0,p_ratingList.length-1).min().getAsDouble();
	}

	/**
	 * Determine if a set of games is unbound.
	 * <br>
	 * A set of games is unbound if all the games were won or lost.
	 *
	 * @param p_games  not null, MovingAverageValue or RankGame.
	 * @return null if no games<br>LimistSet.MIXED if bound<br>
	 * 			LimitSet.LOSTALL if all games lost<br>
	 * 			LimitSet.WONALL if all game won.
	 */
	protected LimitSet needsLimit(MovingAverageValue<RankGame> p_games) {
		LimitSet l_retVal = null;
		boolean l_first = true;
		boolean l_won = false;
		for (final RankGame l_currentGame : p_games) {
			if (l_first) {
				l_first = false;
				l_won = l_currentGame.getResult() == GameResult.PLAYERWON;
				l_retVal = l_won ? LimitSet.WONALL : LimitSet.LOSTALL;
			} else {
				if (l_won != (l_currentGame.getResult() == GameResult.PLAYERWON)) {
					l_retVal = LimitSet.MIXED;
					break;
				}
			}
		}
		return l_retVal;
	}

	/**
	 * Return the CDF for a set of games.
	 * <p>
	 * 
	 * @param p_gameList
	 *            not null, the games being considered
	 * @param p_ratingList
	 *            not null, the ratings associated with each opponent
	 * @param p_probArray
	 *            not null, output, the value of the CDF is written into this
	 *            array
	 * @return a Tuple describing the range of valid ratings.
	 */
	public Tuple<Double, Double> getCDF(RankGame[] p_gameList, double[] p_ratingList, double[] p_probArray) {
		double l_maxRating = MAXIMUMRATING;
		double l_minRating = MINIMUMRATING;
		final int l_size = p_gameList.length;
		final double[][] l_probArrays = new double[l_size][];
		final int[] l_currentOffset = new int[l_size];
		for (int l_setupCnt = 0; l_setupCnt != l_size; l_setupCnt++) {
			final RankGame l_currentGame = p_gameList[l_setupCnt];
			if (l_currentGame.getResult() == GameResult.PLAYERWON) {
				final double l_rateCheck = p_ratingList[l_setupCnt] + RATINGSPAN;
				if (l_rateCheck < l_maxRating) {
					l_maxRating = l_rateCheck;
				}
			} else {
				final double l_rateCheck = p_ratingList[l_setupCnt] - RATINGSPAN;
				if (l_rateCheck > l_minRating) {
					l_minRating = l_rateCheck;
				}
			}
			l_probArrays[l_setupCnt] = l_currentGame.calculateEventProbabilityArray();
			if (l_currentGame.getResult() == GameResult.PLAYERLOST) {
				invert(l_probArrays[l_setupCnt]);
			}
		}
		for (int l_setupCnt = 0; l_setupCnt != l_size; l_setupCnt++) {
			l_currentOffset[l_setupCnt] = determineOffset(l_minRating, p_ratingList[l_setupCnt]);
		}
		final int maxCnt = (int) (l_maxRating * RANKASINTEGER - l_minRating * RANKASINTEGER);
		for (int cnt = 0; cnt < maxCnt; cnt++) {
			/*
			 * Calculate probabilty of the current outcomes.
			 */
			double l_prob = 1;
			for (int l_outcomeCnt = 0; l_outcomeCnt != l_size; l_outcomeCnt++) {
				final double l_eventProbability = limitedProbability(l_probArrays[l_outcomeCnt],
						l_currentOffset[l_outcomeCnt]++);
				l_prob *= l_eventProbability;
			}
			p_probArray[cnt] = l_prob;
		}
		return new Tuple<Double, Double>(l_minRating, l_maxRating);
	}

	/**
	 * Extend an array or probabilities to [-∞,∞].
	 * <br>
	 * <br>
	 * When p_index < 0 we base the value on the first element in the array.
	 * When p_index > length of the array we base the value on the last element of the array.
	 * When the value of the element being checked is < .5 we return 0d.
	 * When the value of the element being checked is > .5 we return 1.0d.
	 * It should be noted that these values should be approaching the value returned.
	 * If this is not the case then the span of the array was not well defined.
	 * 
	 * @param p_probArray  not null, the double[] array to extend
	 * @param p_index  int, the index to be looked up
	 * @return a double between 0 and 1.0f.
	 * 
	 */
	protected double limitedProbability(double[] p_probArray, int p_index) {
		double l_retVal = 0;
		if (p_index < 0) {
			l_retVal = p_probArray[0] < HALFDOUBLE ? 0f : 1.0f;
		} else if (p_index >= p_probArray.length) {
			l_retVal = p_probArray[0] > HALFDOUBLE ? 0f : 1.0f;
		} else {
			l_retVal = (double) p_probArray[p_index];
		}
		return l_retVal;
	}

	/**
	 * Invert an array of probabilities.
	 * <br><br>
	 * Modifies the array by replacing with 1-x for all elements x in the array.
	 * 
	 * @param p_ds  not null, an array of probabilities expressed as doubles. Modified.
	 */
	protected void invert(double[] p_ds) {
		for (int cnt = 0; cnt != p_ds.length; cnt++) {
			p_ds[cnt] = 1 - p_ds[cnt];
		}
	}

	/**
	 * For a rating p_minRating determine the index into the probability array.
	 * <br><br>
	 * The probability of a win is stored in an array where 0 index is the lowest rating with a chance of a win.
	 * The end of the array is when the probiblity is so high we can treat any higher as being certain (100%).
	 * This function returns the index that the rating would be found in the array.
	 * 
	 * @param p_minRating  not null, 
	 * @param p_ratingOfSpan
	 * @return
	 */
	protected int determineOffset(double p_minRating, double p_ratingOfSpan) {
		int l_retVal = (int) (p_minRating * RANKASINTEGER) + RATINGSPANINTEGER;
		l_retVal -= (int) (p_ratingOfSpan * RANKASINTEGER);
		return l_retVal;
	}

}
