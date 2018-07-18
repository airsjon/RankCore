/**
 * 
 */
package com.airsltd.aga.ranking.variables;

import java.sql.Date;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.airsltd.aga.ranking.core.data.RankGame;
import com.airsltd.aga.ranking.core.data.RankGameExt;
import com.airsltd.aga.ranking.core.data.RankGameOrder;
import com.airsltd.aga.ranking.core.data.RankPlayer;
import com.airsltd.aga.ranking.core.data.RankRating;
import com.airsltd.aga.ranking.core.model.GameExtModel;
import com.airsltd.aga.ranking.core.model.GameModel;
import com.airsltd.aga.ranking.core.model.GameReverseModel;
import com.airsltd.aga.ranking.core.model.PlayerModel;
import com.airsltd.aga.ranking.core.model.RankConnection;
import com.airsltd.aga.ranking.core.model.RatingModel;
import com.airsltd.aga.ranking.core.model.TournamentModel;
import com.airsltd.core.data.AirsJavaDatabaseApp;
import com.airsltd.core.data.ISqlConnection;
import com.airsltd.core.data.Tuple;
import com.airsltd.core.math.Counter;

/**
 * Create extended variables for the game and player records.
 * <p>
 * Current extend variables.
 * <ul>
 * <li>Rating</li>
 * <li>Opponent Rating</li>
 * <li>Quality</li>
 * <li>Probability</li>
 * <li>Trailing Rating</li>
 * <li>Trailing Opponent Rating</li>
 * <li>Trailing Quality</li>
 * <li>Trailing Probability</li>
 * </ul>
 * 
 * @author Jon Boley
 *
 */
public class CreateVariables extends AirsJavaDatabaseApp implements Runnable {

	private enum VariableCounters {
		NORATINGS, NOTRAILINGRATINGS, PLAYERS, TOURNAMENTS, GAMES, NOTRAILING
	}
	
	private static final Log LOGGER = LogFactory.getLog(CreateVariables.class);
	private static boolean f_incremental;
	private Counter<VariableCounters> f_counter = new Counter<VariableCounters>(VariableCounters.class);
	
	public CreateVariables(String[] p_args) {
		super(p_args);
	}

	/**
	 * @param args
	 */
	public static void main(String[] p_args) {
		CreateVariables l_variables = new CreateVariables(p_args);
		setIncremental(l_variables.switchExists(p_args, "--incremental", "-i"));
		ISqlConnection l_connection = RankConnection.getInstance();
		l_variables.initializeDatabase(l_connection);
		l_variables.loadModels();
		LOGGER.info("Loading Values");
		GameExtModel.getInstance().doBlockUpdate(l_variables);
		l_variables.dumpStastics(System.out);
	}

	protected void dumpCounts() {
		LOGGER.info(f_counter.toString());
	}

	/**
	 * Assign the rating of the player to every game they played in.
	 */
	protected void processRatingVariable() {
		LOGGER.info("Process Rating Variable");
		// create a variable that gives the strength of the opponent played by a player
		for (RankPlayer l_player : PlayerModel.getInstance().getElements()) {
			/*
			 * Process all game where this player is the player
			 */
			processRating(GameModel.getInstance().getContentAsList(l_player), l_player);
		}
	}

	/**
	 * Determine the rating value for a player at each game.
	 * 
	 * @param p_ratingVariable
	 * @param p_content
	 * @param p_gameStrengthVariable
	 * @param p_player
	 */
	protected void processRating(List<RankGame> p_content, RankPlayer p_player) {
		List<RankRating> l_contentAsList = RatingModel.getInstance().getContentAsList(p_player);
		l_contentAsList.sort((x, y) -> x.getDate().compareTo(y.getDate()));
		p_content.sort(new RankGameOrder());
		Deque<RankRating> l_ratingIterator = l_contentAsList.isEmpty()?null:new ArrayDeque<RankRating>(l_contentAsList);
		// store the current rating/sigma tuple in l_current
		Tuple<Double, Double> l_current = new Tuple<Double, Double>();
		for (RankGame l_game : p_content) {
			// get the reverse game associated with this game
			RankGame l_reverseGame = GameReverseModel.getInstance().getElement(l_game.getId()).getReverse();
			// get the ratings for the current game
			l_current = getGameRating(l_current, l_game, l_ratingIterator);
			/**
			 * Tuple of leading Rating, Sigma.
			 * 
			 * If no rating exists for the player, the rank they played at is used.
			 */
			Tuple<Double, Double> l_actual = l_current.getPrimary()==null?new Tuple<Double, Double>(l_game.getRank().toRating(),0d):l_current;
			/**
			 * Tuple of trailing Rating, Sigma
			 */
			Tuple<Double, Double> l_gameTrailingRating = l_ratingIterator==null || l_ratingIterator.isEmpty()?new Tuple<Double, Double>():l_ratingIterator.peekFirst().toTupleDouble();
			if (l_actual.getPrimary()!=null) {
				// Place players rating
				modifyGame(l_game).setPlayersRating(l_actual.getPrimary());
				modifyGame(l_game).setPlayersSigma(l_actual.getSecondary());
				// update opponents rating for the reversed game
				modifyGame(l_reverseGame).setOpponentRating(l_actual.getPrimary());
				modifyGame(l_reverseGame).setOpponentSigma(l_actual.getSecondary());
				modifyGame(l_reverseGame).setQuality(l_reverseGame.realRating(l_actual.getPrimary()));
				check(l_game);
				check(l_reverseGame);
				// Place opponents rating for reversed game
				if (l_gameTrailingRating.getPrimary()!=null) {
					// Place players rating
					modifyGame(l_game).setPlayersTrailingRating(l_gameTrailingRating.getPrimary());
					modifyGame(l_game).setPlayersTrailingSigma(l_gameTrailingRating.getSecondary());
					// update opponents rating for the reversed game
					modifyGame(l_reverseGame).setOpponentsTrailingRating(l_gameTrailingRating.getPrimary());
					modifyGame(l_reverseGame).setOpponentsTrailingSigma(l_gameTrailingRating.getSecondary());
					modifyGame(l_reverseGame).setTrailingQuality(l_reverseGame.realRating(l_gameTrailingRating.getPrimary()));
					checkTrailing(l_game);
					checkTrailing(l_reverseGame);
				} else {
					f_counter.count(VariableCounters.NOTRAILING);
					LOGGER.trace("Unknown trailing rating for game: "+l_reverseGame);
				}
			}
		}
	}
	
	/**
	 * Check if the game has a rating for both players.
	 * 
	 * If both players have a rating, then we can calculate the probability of the game.
	 * 
	 * @param p_game
	 */
	protected void check(RankGame p_game) {
		RankGameExt l_gameExt = GameExtModel.getInstance().getElement(p_game);
		if (l_gameExt.getPlayersRating()!=null && l_gameExt.getOpponentRating()!=null) {
			l_gameExt.setProbability(p_game.calculateProbability(
					l_gameExt.getPlayersRating(), l_gameExt.getOpponentRating()));
		}
	}
	
	/**
	 * Check if the game has a trailing rating for both players.
	 * 
	 * If both players have a trailing rating, then we can calculate the trailing probability of the game.
	 * 
	 * @param p_game
	 */
	protected void checkTrailing(RankGame p_game) {
		RankGameExt l_gameExt = GameExtModel.getInstance().getElement(p_game);
		if (l_gameExt.getPlayersTrailingRating()!=null && l_gameExt.getOpponentsTrailingRating()!=null) {
			l_gameExt.setTrailingProbability(p_game.calculateProbability(
					l_gameExt.getPlayersTrailingRating(), l_gameExt.getOpponentsTrailingRating()));
		}
	}

	/**
	 * Helper function to get the RankGameExt associated with p_game.
	 */
	protected RankGameExt modifyGame(RankGame p_game) {
		RankGameExt l_element = GameExtModel.getInstance().getElement(p_game);
		return l_element;
	}

	/**
	 * Return a player's rating and sigma on the day a game is played.
	 * 
	 * @param p_current  not null, the player's current rating, sigma tuple
	 * @param p_game  not null, the RankGame in question
	 * @param p_ratingQue  can be null, a Deque<RankRating> that contains all the ratings that are left (in date order); if null no ratings exist for player
	 * 
	 * @return a Tuple of Rating and Sigma.
	 */
	protected Tuple<Double, Double> getGameRating(Tuple<Double, Double> p_current,
			RankGame p_game, Deque<RankRating> p_ratingQue) {
		Tuple<Double, Double> l_retValue = p_current;
		Date l_gameDate = p_game.getDate();
		if (l_gameDate!=null && p_ratingQue!=null) {
			while (!p_ratingQue.isEmpty() && isBefore(p_ratingQue.peekFirst().getDate(), l_gameDate)) {
				l_retValue = p_ratingQue.pollFirst().toTupleDouble();
			}
		} 
		return l_retValue;
	}

	protected boolean isBefore(Date p_rankDate, Date p_gameDate) {
		return p_rankDate==null || p_rankDate.before(p_gameDate);
	}

	protected RankRating getNextRating(Iterator<RankRating> p_ratingIterator) {
		RankRating l_retVal = p_ratingIterator.hasNext()?p_ratingIterator.next():null;
		if (l_retVal!=null && l_retVal.getDate()==null) {
			l_retVal = getNextRating(p_ratingIterator);
		}
		return l_retVal;
	}

	protected void loadModels() {
		LOGGER.info("Loading Players");
		PlayerModel.getInstance().loadModel("");
		LOGGER.info("Loading Tournament");
		TournamentModel.getInstance().loadModel("");
		LOGGER.info("Loading Game");
		GameModel.getInstance().loadModel(null);
		LOGGER.info("Load Game Links");
		GameReverseModel.getInstance().loadModel(null);
		LOGGER.info("Loading Rating");
		RatingModel.getInstance().loadModel(null);
	}

	/**
	 * @return the f_incremental
	 */
	public static boolean isIncremental() {
		return f_incremental;
	}

	/**
	 * @param p_f_incremental the f_incremental to set
	 */
	public static void setIncremental(boolean p_incremental) {
		f_incremental = p_incremental;
	}

	@Override
	public void run() {
		processRatingVariable();
		dumpCounts();
	}


}
