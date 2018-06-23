/**
 *
 */
package com.airsltd.aga.ranking.core.data;

import java.sql.Date;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.airsltd.aga.ranking.core.function.special.MovingAverageValue;
import com.airsltd.aga.ranking.core.model.GameExtModel;
import com.airsltd.aga.ranking.core.states.GameResult;
import com.airsltd.core.data.IDatabaseConverter;
import com.airsltd.core.data.PersistedData;
import com.airsltd.core.data.annotations.AirsPersistentArray;
import com.airsltd.core.data.annotations.AirsPersistentClass;
import com.airsltd.core.data.annotations.AirsPersistentField;
import com.airsltd.core.data.annotations.FieldStyle;
import com.airsltd.core.data.converters.EnumSetConvertor;
import com.airsltd.core.model.ISegment;

/**
 * An instance of this class denotes the culmination of a Rank.
 *
 * @author Jon Boley
 *
 */
@AirsPersistentClass(table = "liverankobtained", keys = 3, style = FieldStyle.CAPITALIZED)
public class LiveRankObtained extends PersistedData implements ISegment<RankPlayer> {

	private static final Log LOGGER = LogFactory.getLog(LiveRankObtained.class);

	@AirsPersistentField(fieldName = "Player_ID")
	private RankPlayer f_player;
	@AirsPersistentField
	private int f_rank;
	@AirsPersistentField
	private Date f_runDate;
	@AirsPersistentField
	private int f_made;
	@AirsPersistentField
	private Set<GamesWon> f_wonLost;
	/**
	 * Games are stored wins first, losses second.
	 */
	@AirsPersistentField
	@AirsPersistentArray(fields = 6, fieldNames = { "GameOne", "GameTwo", "GameThree", "GameFour", "GameFive",
			"GameSix" })
	private RankGame[] f_game = new RankGame[6];
	@AirsPersistentField
	@AirsPersistentArray(fields = 6, fieldNames = { "StrengthOne", "StrengthTwo", "StrengthThree", "StrengthFour",
			"StrengthFive", "StrengthSix" })
	private Double[] f_strength = new Double[] { 0d, 0d, 0d, 0d, 0d, 0d };

	public LiveRankObtained() {
	}

	public LiveRankObtained(String[] p_args) {
		this();
		fromStringCsv(p_args);
	}

	public LiveRankObtained(LiveRankObtained p_toCopy) {
		copy(p_toCopy);
	}

	public LiveRankObtained(int p_rank, RankPlayer p_player, Date p_runDate, int p_made) {
		f_player = p_player;
		f_rank = p_rank;
		f_runDate = p_runDate;
		f_made = p_made;
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
	 * @return the rank
	 */
	public int getRank() {
		return f_rank;
	}

	/**
	 * @param p_rank
	 *            the rank to set
	 */
	public void setRank(int p_rank) {
		f_rank = p_rank;
	}

	/**
	 * @return the made
	 */
	public int getMade() {
		return f_made;
	}

	/**
	 * @param p_made
	 *            the made to set
	 */
	public void setMade(int p_made) {
		f_made = p_made;
	}

	/**
	 * @return the runDate
	 */
	public Date getRunDate() {
		return f_runDate;
	}
	
	/**
	 * Iterate through games to find the date of the final game in the set.
	 * 
	 * @return the Date of the final game(s) played in the set.
	 */
	public Date getMadeDate() {
		Date l_retVal = null;
		for (RankGame l_game : f_game) {
			if (l_game != null) {
				if (l_retVal==null || l_game.getDate().after(l_retVal)) {
					l_retVal = l_game.getDate();
				}
			}
		}
		return l_retVal;
	}

	/**
	 * @param p_runDate
	 *            the runDate to set
	 */
	public void setRunDate(Date p_runDate) {
		f_runDate = p_runDate;
	}

	public String getPrettyRank() {
		final GoRank l_rank = GoRank.getInstance(f_rank > 0 ? f_rank + 50 : 49 + f_rank);
		return l_rank.niceString();
	}

	@Override
	public RankPlayer toSegment() {
		return f_player;
	}

	/**
	 * @return the wonLost
	 */
	public Set<GamesWon> getWonLost() {
		return f_wonLost;
	}

	/**
	 * @param p_wonLost
	 *            the wonLost to set
	 */
	public void setWonLost(Set<GamesWon> p_wonLost) {
		f_wonLost = p_wonLost;
	}

	/**
	 * @return the game
	 */
	public RankGame[] getGame() {
		return f_game;
	}

	/**
	 * @param p_game
	 *            the game to set
	 */
	public void setGame(RankGame[] p_game) {
		f_game = p_game;
	}

	/**
	 * @return the strength
	 */
	public Double[] getStrength() {
		return f_strength;
	}

	/**
	 * @param p_strength
	 *            the strength to set
	 */
	public void setStrength(Double[] p_strength) {
		f_strength = p_strength;
	}

	public void fillState(MovingAverageValue<RankGame> p_games) {
		LOGGER.trace("Filling Games for rank: " + f_rank);
		int l_winIndex = 0;
		int l_lossIndex = 5;
		int l_gamesWon = 0;
		RankGame[] l_games = getGame();
		Double[] l_strengths = getStrength();
		for (final RankGame l_currentGame : p_games) {
			Double l_strength = GameExtModel.getInstance().getElement(l_currentGame).getTrailingQuality();
			if (l_currentGame.getResult() == GameResult.PLAYERWON) {
				l_gamesWon++;
				l_strengths[l_winIndex] = l_strength;
				l_games[l_winIndex++] = l_currentGame;
			} else {
				l_strengths[l_lossIndex] = l_strength;
				l_games[l_lossIndex--] = l_currentGame;
			}
		}
		f_wonLost = GamesWon.totalGames(l_gamesWon);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.airsltd.core.data.PersistedData#overrideConverter(int,
	 * java.lang.Class, java.lang.Class,
	 * com.airsltd.core.data.annotations.AirsPersistentField)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected <T extends PersistedData, U> IDatabaseConverter<T, U> overrideConverter(int p_currentIndex,
			Class<T> p_parentClass, Class<U> p_fieldClass, AirsPersistentField p_pField) {
		return (IDatabaseConverter<T, U>) (p_currentIndex == 4 ? new EnumSetConvertor<GamesWon>(GamesWon.class) : null);
	}

}
