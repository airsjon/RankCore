/**
 *
 */
package com.airsltd.aga.ranking.core.analysis;

import java.io.Serializable;

import com.airsltd.aga.ranking.core.analysis.Result;
import com.airsltd.aga.ranking.core.data.GoRank;

/**
 * This POJO bean is used to transfer information about what games to do the
 * analysis on.
 * 
 * @author Jon Boley
 *
 */
public class RankGameInput implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -6636262248244872305L;
	private static final int DEFAULTKOMI = 7;
	private static final double DEFAULTRATING = 1.5f;
	private static final int KYUOFFSET = 2;
	/**
	 * Index of the game
	 */
	private String f_index;
	/**
	 * Opponent's rating
	 */
	private double f_rating = DEFAULTRATING;
	/**
	 * True if white
	 */
	private boolean f_color;
	/**
	 * Handicap game played at
	 */
	private int f_handicap = 0;
	/**
	 * Komi game played at
	 */
	private int f_komi = DEFAULTKOMI;
	/**
	 * Result of the game
	 */
	private Result f_resultGame = Result.WIN;

	public RankGameInput(String p_index) {
		f_index = p_index;
	}

	/**
	 * Create a RankGameInput.
	 *
	 * @param p_index
	 *            index of the game, translated into a string
	 * @param p_rating
	 *            rating of the opponent
	 * @param p_color
	 *            color taken
	 * @param p_handicap
	 *            handicap stones in the game
	 * @param p_komi
	 *            komi given by white in the game (negative number represents
	 *            reverse komi)
	 * @param p_resultGame
	 *            result of the game
	 */
	public RankGameInput(int p_index, double p_rating, boolean p_color, int p_handicap, int p_komi,
			Result p_resultGame) {
		super();
		f_index = p_index + "";
		f_rating = p_rating;
		f_color = p_color;
		f_handicap = p_handicap;
		f_komi = p_komi;
		f_resultGame = p_resultGame;
	}

	/**
	 * @return the rating
	 */
	public double getRating() {
		return f_rating;
	}

	/**
	 * @param p_rating
	 *            the rating to set
	 */
	public void setRating(double p_rating) {
		f_rating = p_rating;
	}

	public double getActualStrength() {
		double l_actual = f_rating + GoRank.calculateGameStrength(f_handicap, f_komi) * (f_color ? 1 : -1);
		if (l_actual < 1 && l_actual > -1) {
			/*
			 * If f_rating was dan (>0) then we need to adjust down to kyu so we
			 * subtract. If f_rating was kyu (<0) then we need to adjust up to
			 * dan so we add.
			 */
			l_actual += KYUOFFSET * (f_rating > 0 ? -1 : 1);
		}
		return l_actual;
	}

	/**
	 * @return the color <code>true</code> if white; <code>false</code> if black
	 */
	public boolean isColor() {
		return f_color;
	}

	/**
	 * @param p_color
	 *            the color to set
	 */
	public void setColor(boolean p_color) {
		f_color = p_color;
	}

	/**
	 * @return the handicap
	 */
	public int getHandicap() {
		return f_handicap;
	}

	/**
	 * @param p_handicap
	 *            the handicap to set
	 */
	public void setHandicap(int p_handicap) {
		f_handicap = p_handicap;
	}

	/**
	 * @return the komi
	 */
	public int getKomi() {
		return f_komi;
	}

	/**
	 * @param p_komi
	 *            the komi to set
	 */
	public void setKomi(int p_komi) {
		f_komi = p_komi;
	}

	/**
	 * @return the result
	 */
	public String getResult() {
		return f_resultGame.ordinal() + 1 + "";
	}

	/**
	 * @param p_result
	 *            the result to set
	 */
	public void setResult(String p_result) {
		final int l_ordinal = Integer.parseInt(p_result);
		f_resultGame = Result.values()[l_ordinal - 1];
	}

	/**
	 * @return the resultGame
	 */
	public Result getResultGame() {
		return f_resultGame;
	}

	/**
	 * @param p_resultGame
	 *            the resultGame to set
	 */
	public void setResultGame(Result p_resultGame) {
		f_resultGame = p_resultGame;
	}

	/**
	 * @return the index
	 */
	public String getIndex() {
		return f_index;
	}

	/**
	 * @param p_index
	 *            the index to set
	 */
	public void setIndex(String p_index) {
		f_index = p_index;
	}

}
