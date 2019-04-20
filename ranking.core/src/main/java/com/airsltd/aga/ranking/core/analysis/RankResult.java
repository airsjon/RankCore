/**
 *
 */
package com.airsltd.aga.ranking.core.analysis;

import java.io.Serializable;
import java.text.DecimalFormat;

/**
 * Object that saves when a rank was obtained during an analysis
 *
 * @author Jon Boley
 *
 */
public class RankResult implements Comparable<RankResult>, Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -7602596930378895576L;
	private static final float KYUOFFSET = 2;
	private static final int HASHPRIME = 31;
	/**
	 * The rating of the first game
	 */
	private final double f_gameOneRating;
	/**
	 * The rating of the second game
	 */
	private final double f_gameTwoRating;
	/**
	 * The {@link Result} being recorded. Not null.
	 */
	private final Result f_result;
	/**
	 * The rank obtained
	 */
	private final int f_rank;

	/**
	 * Create a {@link RankResult}
	 *
	 * @param p_d
	 * @param p_d2
	 * @param p_result
	 * @param p_rank
	 */
	public RankResult(double p_d, double p_d2, Result p_result, int p_rank) {
		super();
		f_gameOneRating = p_d;
		f_gameTwoRating = p_d2;
		f_result = p_result;
		f_rank = p_rank;
	}

	/**
	 * @return the gameOneRating
	 */
	public double getGameOneRating() {
		return f_gameOneRating;
	}

	/**
	 * @return the gameTwoRating
	 */
	public double getGameTwoRating() {
		return f_gameTwoRating;
	}

	/**
	 * @return the result
	 */
	public Result getResult() {
		return f_result;
	}

	/**
	 * @return the rank
	 */
	public int getRank() {
		return f_rank;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RankResult [f_gameOneRating=" + prettyRating(f_gameOneRating) + ", f_gameTwoRating="
				+ prettyRating(f_gameTwoRating) + ", f_result=" + f_result + ", f_rank=" + prettyRank(f_rank) + "]";
	}

	public static String prettyRank(int p_rank) {
		return (p_rank < 1 ? p_rank - 1 : p_rank) + "";
	}

	public static String prettyRating(double p_rating) {
		double l_offsetRating = (p_rating < 1 ? p_rating - KYUOFFSET : p_rating);
		return new DecimalFormat("##.##").format(l_offsetRating);
	}

	@Override
	public int compareTo(RankResult p_other) {
		int l_retVal = f_result.compareTo(p_other.getResult());
		if (l_retVal == 0) {
			l_retVal = Integer.compare(f_rank, p_other.getRank());
			if (l_retVal == 0) {
				l_retVal = Double.compare(f_gameOneRating, p_other.getGameOneRating());
				if (l_retVal == 0) {
					l_retVal = Double.compare(f_gameTwoRating, p_other.getGameTwoRating());
				}
			}
		}
		return l_retVal;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = HASHPRIME;
		int result = 1;
		result = prime * result + Long.hashCode(Double.doubleToLongBits(f_gameOneRating));
		result = prime * result + Long.hashCode(Double.doubleToLongBits(f_gameTwoRating));
		result = prime * result + f_rank;
		result = prime * result + f_result.ordinal() * HASHPRIME;
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		boolean l_retVal = false;
		if (this == obj) {
			l_retVal = true;
		} else if (obj instanceof RankResult) {
			final RankResult other = (RankResult) obj;
			l_retVal = Long.hashCode(Double.doubleToLongBits(f_gameOneRating)) == 
						Long.hashCode(Double.doubleToLongBits(other.f_gameOneRating))
					&& Long.hashCode(Double.doubleToLongBits(f_gameTwoRating)) == 
						Long.hashCode(Double.doubleToLongBits(other.f_gameTwoRating))
					&& f_rank == other.f_rank 
					&& f_result == other.f_result;
		}
		return l_retVal;
	}

}
