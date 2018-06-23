/**
 *
 */
package com.airsltd.aga.ranking.core.function.special;

/**
 * This function calculates the rating that a player has a 90% change of being
 * stronger than.
 * <p>
 * Using Bayesian analysis we calculate the probability that a player will have
 * the rating R with the outcome that has occurred. The value returned is the
 * minumum rating R_Threshold such that Sum(R < R_Threshold)/Sum(R all) > .10
 * <p>
 * Parameters: Moving Average of Rating, Moving Average of Game, Integer of rank
 * being tested for, Rating of the player being played, Game being played
 * 
 * @author Jon Boley
 *
 */
public class ProbabilityAs extends ProbabilityCDFFunction {

	private static ProbabilityAs s_instance;

	public ProbabilityAs() {
	}

	@Override
	protected int atThreshold() {
		return -2000;
	}

	@Override
	protected boolean checkThreshold(double l_gameRating) {
		return (l_gameRating < -15f);
	}

	public static ProbabilityAs getInstance() {
		if (s_instance == null) {
			s_instance = new ProbabilityAs();
		}
		return s_instance;
	}

	@Override
	protected boolean getDirection() {
		return true;
	}

}
