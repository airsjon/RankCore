/**
 *
 */
package com.airsltd.aga.ranking.core.data;

import java.sql.Date;

import com.airsltd.core.data.PersistedData;
import com.airsltd.core.data.Tuple;
import com.airsltd.core.data.annotations.AirsPersistentClass;
import com.airsltd.core.data.annotations.AirsPersistentField;
import com.airsltd.core.data.annotations.FieldStyle;
import com.airsltd.core.model.ISegment;

/**
 * RankRating is the rating calculated by the AGA rating system.
 * 
 * When games are rated a ratings calculated based on the player's previous rating and any new games played.
 * Associated with this rating is a sigma.
 * 
 * @author Jon Boley
 *
 */
@AirsPersistentClass(table = "ratings", keys = 3, style = FieldStyle.CAPITALIZED)
public class RankRating extends PersistedData implements ISegment<RankPlayer> {

	private static final int KYUOFFSET = 2;

	@AirsPersistentField(fieldName = "Player_ID")
	private RankPlayer f_player;
	@AirsPersistentField
	private Date f_date;
	@AirsPersistentField
	private float f_rating;
	@AirsPersistentField
	private float f_sigma;

	public RankRating() {
	}

	public RankRating(RankRating p_rankRating) {
		copy(p_rankRating);
	}

	/**
	 * @return the player
	 */
	public RankPlayer getPlayer() {
		return f_player;
	}

	/**
	 * @param p_player
	 *            the player to set
	 */
	public void setPlayer(RankPlayer p_player) {
		f_player = p_player;
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return f_date;
	}

	/**
	 * @param p_date
	 *            the date to set
	 */
	public void setDate(Date p_date) {
		f_date = p_date;
	}

	/**
	 * @return the rating
	 */
	public float getRating() {
		return f_rating;
	}

	/**
	 * @param p_rating
	 *            the rating to set
	 */
	public void setRating(float p_rating) {
		f_rating = p_rating;
	}

	/**
	 * @return the sigma
	 */
	public float getSigma() {
		return f_sigma;
	}

	/**
	 * @param p_sigma
	 *            the sigma to set
	 */
	public void setSigma(float p_sigma) {
		f_sigma = p_sigma;
	}

	/**
	 * Convert the rating in to a continuous rating.
	 * 
	 * The AGA rating system rates players from [-∞,-1) [1, ∞].
	 * For math involving ratings the gap from -1 to 1 is removed.
	 * 
	 * @return a continuous rating.
	 */
	public float toFixedRating() {
		return f_rating > 1.0 ? f_rating : f_rating + KYUOFFSET;
	}

	@Override
	public RankPlayer toSegment() {
		return f_player;
	}

	/**
	 * Convert the RankRating into a tuple of continuous rating and sigma.
	 * 
	 * 
	 * @return a Tuple of the continuous (primary) and sigma (secondary).
	 * @see #toFixedRating() for a description of continous rating
	 */
	public Tuple<Float, Float> toTupleFloat() {
		return new Tuple<Float, Float>(toFixedRating(), getSigma());
	}

	/**
	 * Convert the RankRating into a tuple of continuous rating and sigma.
	 * 
	 * 
	 * @return a Tuple of the continuous (primary) and sigma (secondary).
	 * @see #toFixedRating() for a description of continous rating
	 */
	public Tuple<Double, Double> toTupleDouble() {
		return new Tuple<Double, Double>((double) toFixedRating(), (double) getSigma());
	}

}
