/**
 *
 */
package com.airsltd.aga.ranking.core.data;

import java.sql.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.special.Erf;
import org.apache.commons.math3.util.FastMath;

import com.airsltd.aga.ranking.core.model.GameModel;
import com.airsltd.aga.ranking.core.states.GameResult;
import com.airsltd.core.data.AbstractPersistedIdData;
import com.airsltd.core.data.BlockData;
import com.airsltd.core.data.IBlockData;
import com.airsltd.core.data.IDatabaseConverter;
import com.airsltd.core.data.PersistedData;
import com.airsltd.core.data.Tuple;
import com.airsltd.core.data.annotations.AirsPersistentClass;
import com.airsltd.core.data.annotations.AirsPersistentField;
import com.airsltd.core.data.annotations.FieldStyle;
import com.airsltd.core.model.ISegment;

/**
 * Persistent store of a rated game in the AGA database.
 * 
 * @author Jon Boley
 *
 */
@AirsPersistentClass(table = "games", keys = 1, style = FieldStyle.CAPITALIZED, sort = { 2, 0 })
public class RankGame extends AbstractPersistedIdData implements IGame, ISegment<RankPlayer> {

	private static final double[] DEFSIGMA = { 1.0649f, 1.0649f, 1.13672f, 1.18795f, 1.22841f, 1.27457f, 1.31978f,
			1.35881f, 1.39782f, 1.43614f };

	protected static final int RANKLENGTH = 32;
	protected static final int RANKSPAN = 541;

	private static final IDatabaseConverter<IBlockData, GoRank> RANKCONVERTER = new IDatabaseConverter<IBlockData, GoRank>() {

		@Override
		public String toSql(IBlockData p_parent, GoRank p_data) {
			return BlockData.toSql(p_data == null ? "50" : p_data.toSqlString(), RANKLENGTH);
		}

		@Override
		public GoRank fromSql(IBlockData p_parent, String p_string) throws ParseException {
			return GoRank.fromSqlString(p_string);
		}
	};

	private static final double HALFRANK = .5f;

	private static final long PRIMARYKEYS = 0x1002;

	private static final int MAXHANDICAP = 10;

	private static final double KOMIONESIGMA = -0.0021976f;

	private static final double KOMISIGMASQR = 0.00014984f;

	private static final double KOMITWOSIGMA = -0.0035169f;

	private static final double DOUBLETWO = 2d;

	/**
	 * When ratings are stored as Integers they are stored as rating * 100.
	 */
	private static final double RATINGFACTOR = 100d;

	private static final int TWO = 2;

	@AirsPersistentField(fieldName = "Player_ID")
	private RankPlayer f_player;
	@AirsPersistentField
	private Date f_date;
	@AirsPersistentField(fieldName = "Tournament_ID")
	private RankTournament f_tournament;
	@AirsPersistentField
	private int f_round;
	@AirsPersistentField(fieldName = "Opponent_ID")
	private RankPlayer f_opponent;
	@AirsPersistentField
	private Color f_color;
	@AirsPersistentField
	private int f_handicap;
	@AirsPersistentField
	private int f_komi;
	@AirsPersistentField
	private GameResult f_result;
	@AirsPersistentField(fieldName = "Player_Rank")
	private GoRank f_rank;
	@AirsPersistentField(fieldName = "Opponent_Rank")
	private GoRank f_opponentRank;
	@AirsPersistentField
	private int f_agaGameId;
	@AirsPersistentField
	private GameState f_state = GameState.UNKNOWN;
	@AirsPersistentField(size=64)
	private String f_reason;

	private RankGame f_linkedGame;


	private static Map<Tuple<Integer, Integer>, List<Double>> s_cachedProbLists = new HashMap<Tuple<Integer, Integer>, List<Double>>();

	public RankGame() {
		super(RankGame.class);
	}

	public RankGame(RankGame p_rankGame) {
		super(RankGame.class);
		copy(p_rankGame);
	}

	public RankGame(long p_id, int p_handicap, int p_komi, Color p_color, int p_agaGameId) {
		super(RankGame.class);
		setId(p_id);
		f_handicap = p_handicap;
		f_komi = p_komi;
		f_color = p_color;
		f_agaGameId = p_agaGameId;
	}

	/**
	 * @return the linkedGame
	 */
	public RankGame getLinkedGame() {
		return f_linkedGame;
	}

	/**
	 * @param p_linkedGame
	 *            the linkedGame to set
	 */
	public void setLinkedGame(RankGame p_linkedGame) {
		f_linkedGame = p_linkedGame;
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
	 * @return the opponent
	 */
	public RankPlayer getOpponent() {
		return f_opponent;
	}

	/**
	 * @param p_opponent
	 *            the opponent to set
	 */
	public void setOpponent(RankPlayer p_opponent) {
		f_opponent = p_opponent;
	}

	/**
	 * @return the color
	 */
	public Color getColor() {
		return f_color;
	}

	/**
	 * @param p_color
	 *            the color to set
	 */
	public void setColor(Color p_color) {
		f_color = p_color;
	}

	/**
	 * @return the rank
	 */
	public GoRank getRank() {
		return f_rank;
	}

	/**
	 * @param p_rank
	 *            the rank to set
	 */
	public void setRank(GoRank p_rank) {
		f_rank = p_rank;
	}

	/**
	 * @return the opponentRank
	 */
	public GoRank getOpponentRank() {
		return f_opponentRank;
	}

	/**
	 * @param p_opponentRank
	 *            the opponentRank to set
	 */
	public void setOpponentRank(GoRank p_opponentRank) {
		f_opponentRank = p_opponentRank;
	}

	/**
	 * @param p_tournament
	 *            the tournament to set
	 */
	public void setTournament(RankTournament p_tournament) {
		f_tournament = p_tournament;
	}

	/**
	 * @param p_round
	 *            the round to set
	 */
	public void setRound(int p_round) {
		f_round = p_round;
	}

	/**
	 * @param p_handicap
	 *            the handicap to set
	 */
	public void setHandicap(int p_handicap) {
		f_handicap = p_handicap;
	}

	/**
	 * @param p_komi
	 *            the komi to set
	 */
	public void setKomi(int p_komi) {
		f_komi = p_komi;
	}

	/**
	 * @param p_result
	 *            the result to set
	 */
	public void setResult(GameResult p_result) {
		f_result = p_result;
	}

	
	/**
	 * @return the reason
	 */
	public String getReason() {
		return f_reason;
	}

	/**
	 * @param p_reason the reason to set
	 */
	public void setReason(String p_reason) {
		f_reason = p_reason;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.airsltd.aga.ranking.core.data.IGame#getTournament()
	 */
	@Override
	public RankTournament getTournament() {
		return f_tournament;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.airsltd.aga.ranking.core.data.IGame#getRound()
	 */
	@Override
	public int getRound() {
		return f_round;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.airsltd.aga.ranking.core.data.IGame#getHandicap()
	 */
	@Override
	public int getHandicap() {
		return f_handicap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.airsltd.aga.ranking.core.data.IGame#getKomi()
	 */
	@Override
	public int getKomi() {
		return f_komi;
	}

	/**
	 * @return the agaGameId
	 */
	public int getAgaGameId() {
		return f_agaGameId;
	}

	/**
	 * @param p_agaGameId
	 *            the agaGameId to set
	 */
	public void setAgaGameId(int p_agaGameId) {
		f_agaGameId = p_agaGameId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.airsltd.aga.ranking.core.data.IGame#getResult()
	 */
	@Override
	public GameResult getResult() {
		return f_result;
	}

	@Override
	public long primaryKeyFields() {
		return PRIMARYKEYS;
	}

	/**
	 * A <code>null</code> value represents an invalid rating
	 *
	 * @param p_value
	 *            a continous strength to be adjusted by the handicap
	 *            equivalence of the game
	 * @return
	 */
	public Double toStrength(double p_value) {
		return p_value + (f_color == Color.BLACK ? -1 : 1) * calculateHandicapEquiv();
	}

	public static int toRank(double p_value) {
		return (int)Math.round(p_value - HALFRANK);
	}

	public double calculateSigma() {
		double l_retVal = f_handicap >= 0 && f_handicap < MAXHANDICAP ? DEFSIGMA[f_handicap] : 0f;
		if (f_handicap == 0) {
			l_retVal += KOMIONESIGMA * f_komi + KOMISIGMASQR * f_komi * f_komi;
		} else if (f_handicap == 1) {
			l_retVal += KOMIONESIGMA * f_komi + KOMISIGMASQR * f_komi * f_komi;
		} else {
			l_retVal += KOMITWOSIGMA * f_komi;
		}
		return l_retVal;
	}

	public double calculateHandicapEquiv() {
		return GoRank.calculateGameStrength(f_handicap, f_komi);
	}

	/**
	 * Calculate the chance the p_playerRating will be beat p_oponentRating in
	 * the current game.
	 *
	 * @param p_playerRating
	 * @param p_opponentRating
	 * @return
	 */
	public double calculateProbability(Double p_playerRating, Double p_opponentRating) {
		int l_colorAdj = getColor().equals(Color.WHITE) ? -1 : 1;
		final double l_equiv = calculateHandicapEquiv();
		double l_retVal = Erf.erfc((l_colorAdj * (p_playerRating - p_opponentRating) + l_equiv)
				/ calculateSigma() / FastMath.sqrt(DOUBLETWO)) / DOUBLETWO;
		return l_colorAdj>0?1-l_retVal:l_retVal;
	}

	/**
	 * Calculate the probability of the current event (win/loss) in the current
	 * game.
	 * <p>
	 * Given the two possible ratings, calculate the probability that the
	 * outcome of the game would occur.
	 *
	 *
	 * @param p_playerRating
	 *            not null, the rating of the player under consideration
	 * @param p_opponentRating
	 *            not null, the rating of the opponent
	 * @return the probability that the outcome of the game would happen.
	 */
	public double calculateEventProbability(Double p_playerRating, Double p_opponentRating) {
		final double l_equiv = calculateHandicapEquiv();
		final boolean isWhite = getColor().equals(Color.WHITE);
		double l_win = Erf.erfc(((isWhite ? 1 : -1) * (p_opponentRating - p_playerRating) + l_equiv) / calculateSigma()
				/ FastMath.sqrt(DOUBLETWO)) / DOUBLETWO;
		if (!isWhite) {
			l_win = 1 - l_win;
		}
		return getResult() == GameResult.PLAYERLOST ? 1 - l_win : l_win;
	}

	/**
	 * Return an array of the probability that a black win will occur.
	 * <p>
	 * These arrays are cached so that future calls will not have to recalculate
	 * all the data.
	 *
	 * @param p_ratingDifference
	 *            not null, the rating differnce is the black players rating -
	 *            white players rating + game equiv rating
	 * @return
	 */
	public List<Double> calculateEventProbabilty() {
		List<Double> l_retVal = null;
		final Tuple<Integer, Integer> l_handiKomi = new Tuple<Integer, Integer>(getHandicap(), getKomi());
		if (s_cachedProbLists.containsKey(l_handiKomi)) {
			l_retVal = s_cachedProbLists.get(l_handiKomi);
		} else {
			l_retVal = new ArrayList<Double>();
			final double l_sigma = calculateSigma() * FastMath.sqrt(DOUBLETWO);
			for (int l_ratingDifference = 0; l_ratingDifference < RANKSPAN; l_ratingDifference++) {
				l_retVal.add(1d - Erf.erfc(-l_ratingDifference / RATINGFACTOR / l_sigma) / DOUBLETWO);
			}
			s_cachedProbLists.put(l_handiKomi, l_retVal);
		}
		return l_retVal;
	}

	/**
	 * Expands the event probability array into its full glory.
	 * <p>
	 * 
	 * @return a double array of the probability that 
	 */
	public double[] calculateEventProbabilityArray() {
		final List<Double> l_doubles = calculateEventProbabilty();
		final double[] l_retVal = new double[l_doubles.size() * TWO - 1];
		int l_index = 0;
		final int l_midPoint = l_doubles.size() - 1;
		for (final Double l_double : l_doubles) {
			if (l_index != 0) {
				l_retVal[l_midPoint - l_index] = l_double;
			}
			l_retVal[l_midPoint + l_index++] = 1 - l_double;
		}
		return l_retVal;
	}

	@Override
	public RankPlayer toSegment() {
		return f_player;
	}

	/**
	 * Convert the players rating to the rating the game was played at.
	 * <p>
	 * Taking into consideration of the handicap and komi of the game, determine
	 * what strength the game was played at.
	 * 
	 * @param p_playerRating
	 *            the rating of the opponent
	 * @return the adjusted rating of the opponent
	 */
	public double realRating(double p_playerRating) {
		return p_playerRating + (getColor() == Color.BLACK ? -1 : 1) * calculateHandicapEquiv();
	}

	/**
	 * Helper method to remove the gap from 1kyu to 1dan in ratings.
	 *
	 * @param p_rating
	 *            double representing the rating in the AGA system
	 * @return
	 */
	public static double fixedRating(double p_rating) {
		return p_rating < 0 ? p_rating + TWO : p_rating;
	}

	/**
	 * Is this a valid game to model.
	 * <p>
	 * Games at 9 stones against players of insufficient strength need to be
	 * ignored.
	 * 
	 * @param p_rank
	 * @param p_opponentStrength
	 *
	 * @return
	 */
	public boolean validToModel(int p_rank, Double p_opponentStrength) {
		return f_handicap < 5;
	}

	@Override
	protected void addContent() {
		GameModel.getInstance().addContent(this);
	}

	@Override
	protected String getIdFieldName() {
		return "Game_ID";
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
		return (IDatabaseConverter<T, U>) (p_currentIndex == 10 || p_currentIndex == 11 ? RANKCONVERTER
				: super.overrideConverter(p_currentIndex, p_parentClass, p_fieldClass, p_pField));
	}

	/**
	 * @return the state
	 */
	public GameState getState() {
		return f_state;
	}

	/**
	 * @param p_state
	 *            the state to set
	 */
	public void setState(GameState p_state) {
		f_state = p_state;
	}

	public boolean similiar(RankGame p_other) {
		return p_other.getTournament() == f_tournament && 
				p_other.getHandicap() == f_handicap && 
				p_other.getKomi() == f_komi && 
				p_other.getColor() == f_color && 
				p_other.getOpponent() == f_opponent;
	}

}
