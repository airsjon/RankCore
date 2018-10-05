package com.airsltd.aga.ranking.core.data;

import com.airsltd.core.data.PersistedData;
import com.airsltd.core.data.annotations.AirsPersistentClass;
import com.airsltd.core.data.annotations.AirsPersistentField;
import com.airsltd.core.data.annotations.FieldStyle;
import com.airsltd.core.model.ISegment;

@AirsPersistentClass(table = "gamesext", keys = 1, style = FieldStyle.CAPITALIZED)
public class RankGameExt extends PersistedData implements ISegment<RankGame> {

	@AirsPersistentField(fieldName="Game_ID")
	private RankGame f_game;
	@AirsPersistentField
	private Double f_playersRating;
	@AirsPersistentField
	private Double f_playersSigma;
	@AirsPersistentField
	private Double f_playersTrailingRating;
	@AirsPersistentField
	private Double f_playersTrailingSigma;
	@AirsPersistentField
	private Double f_opponentRating;
	@AirsPersistentField
	private Double f_opponentSigma;
	@AirsPersistentField
	private Double f_opponentsTrailingRating;
	@AirsPersistentField
	private Double f_opponentsTrailingSigma;
	@AirsPersistentField
	private Double f_quality;
	@AirsPersistentField
	private Double f_trailingQuality;
	@AirsPersistentField
	private Double f_probability;
	@AirsPersistentField
	private Double f_trailingProbability;
	@AirsPersistentField
	private Integer f_calculatedRating;
	/**
	 * The number of wins in the last six games.
	 */
	@AirsPersistentField
	private int f_wins;
	/**
	 * The second highest rating of the games won.
	 */
	@AirsPersistentField
	private long f_secondRating;

	public RankGameExt() {
		super();
	}
	
	public RankGameExt(RankGame p_element) {
		super();
		f_game = p_element;
	}
	
	public RankGameExt(RankGameExt p_element) {
		super();
		copy(p_element);
	}

	@Override
	public RankGame toSegment() {
		return f_game;
	}

	/**
	 * @return the game
	 */
	public RankGame getGame() {
		return f_game;
	}

	/**
	 * @param p_game the game to set
	 */
	public void setGame(RankGame p_game) {
		f_game = p_game;
	}

	/**
	 * @return the playersRating
	 */
	public Double getPlayersRating() {
		return f_playersRating;
	}

	/**
	 * @param p_playersRating the playersRating to set
	 */
	public void setPlayersRating(Double p_playersRating) {
		f_playersRating = p_playersRating;
	}

	/**
	 * @return the playersSigma
	 */
	public Double getPlayersSigma() {
		return f_playersSigma;
	}

	/**
	 * @param p_playersSigma the playersSigma to set
	 */
	public void setPlayersSigma(Double p_playersSigma) {
		f_playersSigma = p_playersSigma;
	}

	/**
	 * @return the playersTrailingRating
	 */
	public Double getPlayersTrailingRating() {
		return f_playersTrailingRating;
	}

	/**
	 * @param p_playersTrailingRating the playersTrailingRating to set
	 */
	public void setPlayersTrailingRating(Double p_playersTrailingRating) {
		f_playersTrailingRating = p_playersTrailingRating;
	}

	/**
	 * @return the playersTrailingSigma
	 */
	public Double getPlayersTrailingSigma() {
		return f_playersTrailingSigma;
	}

	/**
	 * @param p_playersTrailingSigma the playersTrailingSigma to set
	 */
	public void setPlayersTrailingSigma(Double p_playersTrailingSigma) {
		f_playersTrailingSigma = p_playersTrailingSigma;
	}

	/**
	 * @return the opponentRating
	 */
	public Double getOpponentRating() {
		return f_opponentRating;
	}

	/**
	 * @param p_opponentRating the opponentRating to set
	 */
	public void setOpponentRating(Double p_opponentRating) {
		f_opponentRating = p_opponentRating;
	}

	/**
	 * @return the opponentSigma
	 */
	public Double getOpponentSigma() {
		return f_opponentSigma;
	}

	/**
	 * @param p_opponentSigma the opponentSigma to set
	 */
	public void setOpponentSigma(Double p_opponentSigma) {
		f_opponentSigma = p_opponentSigma;
	}

	/**
	 * @return the opponentsTrailingRating
	 */
	public Double getOpponentsTrailingRating() {
		return f_opponentsTrailingRating;
	}

	/**
	 * @param p_opponentsTrailingRating the opponentsTrailingRating to set
	 */
	public void setOpponentsTrailingRating(Double p_opponentsTrailingRating) {
		f_opponentsTrailingRating = p_opponentsTrailingRating;
	}

	/**
	 * @return the opponentsTrailingSigma
	 */
	public Double getOpponentsTrailingSigma() {
		return f_opponentsTrailingSigma;
	}

	/**
	 * @param p_opponentsTrailingSigma the opponentsTrailingSigma to set
	 */
	public void setOpponentsTrailingSigma(Double p_opponentsTrailingSigma) {
		f_opponentsTrailingSigma = p_opponentsTrailingSigma;
	}

	/**
	 * @return the quality
	 */
	public Double getQuality() {
		return f_quality;
	}

	/**
	 * @param p_quality the quality to set
	 */
	public void setQuality(Double p_quality) {
		f_quality = p_quality;
	}

	/**
	 * @return the trailingQuality
	 */
	public Double getTrailingQuality() {
		return f_trailingQuality;
	}

	/**
	 * @param p_trailingQuality the trailingQuality to set
	 */
	public void setTrailingQuality(Double p_trailingQuality) {
		f_trailingQuality = p_trailingQuality;
	}

	/**
	 * @return the probability
	 */
	public Double getProbability() {
		return f_probability;
	}

	/**
	 * @param p_probability the probability to set
	 */
	public void setProbability(Double p_probability) {
		f_probability = p_probability;
	}

	/**
	 * @return the trailingProbability
	 */
	public Double getTrailingProbability() {
		return f_trailingProbability;
	}

	/**
	 * @param p_trailingProbability the trailingProbability to set
	 */
	public void setTrailingProbability(Double p_trailingProbability) {
		f_trailingProbability = p_trailingProbability;
	}

	/**
	 * @return the calculatedRating
	 */
	public Integer getCalculatedRating() {
		return f_calculatedRating;
	}

	/**
	 * @param p_calculatedRating the calculatedRating to set
	 */
	public void setCalculatedRating(Integer p_calculatedRating) {
		f_calculatedRating = p_calculatedRating;
	}

	/**
	 * @return the wins
	 */
	public int getWins() {
		return f_wins;
	}

	/**
	 * @param p_wins the wins to set
	 */
	public void setWins(int p_wins) {
		f_wins = p_wins;
	}

	/**
	 * @return the secondRating
	 */
	public long getSecondRating() {
		return f_secondRating;
	}

	/**
	 * @param p_secondRating the secondRating to set
	 */
	public void setSecondRating(long p_secondRating) {
		f_secondRating = p_secondRating;
	}


}
