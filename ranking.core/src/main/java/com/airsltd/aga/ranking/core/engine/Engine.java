/**
 *
 */
package com.airsltd.aga.ranking.core.engine;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.airsltd.aga.ranking.core.data.GameState;
import com.airsltd.aga.ranking.core.data.LiveRankObtained;
import com.airsltd.aga.ranking.core.data.RankGame;
import com.airsltd.aga.ranking.core.data.RankGameExt;
import com.airsltd.aga.ranking.core.data.RankGameOrder;
import com.airsltd.aga.ranking.core.data.RankPlayer;
import com.airsltd.aga.ranking.core.function.special.MovingAverageValue;
import com.airsltd.aga.ranking.core.function.special.ProbabilityAs;
import com.airsltd.aga.ranking.core.model.GameExtModel;
import com.airsltd.aga.ranking.core.model.GameModel;
import com.airsltd.aga.ranking.core.model.LiveRankObtainedModel;
import com.airsltd.aga.ranking.core.model.PlayerModel;
import com.airsltd.aga.ranking.core.states.GameResult;
import com.airsltd.core.collections.AirsCollections;

/**
 * Run the Rank Certificate System.
 * <p>
 * This engine can be run as certificate generator. 
 * Certificate generation is done only on games that are ready to be processed.
 * <p>
 * Certificate generation occurs in incremental mode.
 * <p>
 * Current implementation for 9 kyu to 7 dan
 *
 * @author Jon Boley
 *
 */
public class Engine {

	private static final int ENDRANK = -9;

	private static Boolean s_tracing = false;
	private static final Log LOGGER = LogFactory.getLog(Engine.class);

	private static final Double HUNDREDDOUBLE = 100.0d;

	private int f_players;

	private int f_ranks;

	private int f_games;

	private Date f_runDate = new Date(new java.util.Date().getTime());

	private List<LiveRankObtained> f_liveRanks = new ArrayList<LiveRankObtained>();

	private MovingAverageValue<RankGame> f_gamesAverage = new MovingAverageValue<RankGame>(6);
	
	/**
	 * @param p_tracing the tracing to set
	 */
	public static void setTracing(Boolean p_tracing) {
		s_tracing = p_tracing;
	}

	public Engine(Date p_date, int p_gameSpan) {
		if (p_date != null) {
			f_runDate = p_date;
		}
	}

	public void run() {
		LOGGER.info("Generating Ranks");
		LiveRankObtainedModel.getInstance().loadModel(null);
		LiveRankObtainedModel.getInstance().setCompleteLoad(true);
		GameExtModel.getInstance().doBlockUpdate(() -> {
			GameModel.getInstance().doBlockUpdate(() -> {
				processRanks();
			}); });
		storeCertificates();
	}

	protected void processRanks() {
		for (final RankPlayer l_player : PlayerModel.getInstance().getElements()) {
			f_players++;
			final List<RankGame> l_games = AirsCollections.findAll(new IncrementalGameSelector(),
					GameModel.getInstance().getContentAsList(l_player));
			l_games.sort(new RankGameOrder());
			f_games = l_games.size();
			// load in previous ranks
			int l_toRank = rankLimit(l_player);
			if (s_tracing) {
				LOGGER.info("Processing player: "+l_player.getPin_Player());
			}
			/*
			 * Process each game to determine rank the player qualified and store it.
			 */
			processRankValues(l_player, l_games);
			
			/*
			 * Go through and record all ranks that were obtained.
			 */
			processRank(l_toRank, l_player, l_games);
		}
	}

	/**
	 * Determine the rank to limit checks for.
	 * <p>
	 * If a player has achieved a rank from previous runs/events, we limit our examination to ranks above that value.
	 * 
	 * @param l_player  not null, the player to examine
	 * @return an int specifying the rank to check until.
	 */
	protected int rankLimit(final RankPlayer l_player) {
		int l_toRank = ENDRANK;
		List<LiveRankObtained> l_obtained = LiveRankObtainedModel.getInstance().getContentAsList(l_player);
		if (!l_obtained.isEmpty()) {
			l_toRank = Integer.max(ENDRANK, l_obtained.get(l_obtained.size()-1).getRank());
		}
		return l_toRank;
	}

	/**
	 * Calculate the Rank value for all active games.
	 * <br>
	 * By calculating the Rank value for each game, we can store them for future reports.
	 * 
	 * @param p_player
	 * @param p_games
	 */
	protected void processRankValues(RankPlayer p_player, List<RankGame> p_games) {
		int l_gameIndex = 1;
		f_gamesAverage.clear();
		for (final RankGame l_game : p_games) {
			if (s_tracing) {
				LOGGER.info("@Game: "+l_gameIndex);
			}
			gameSetup(l_game);
			int l_calculateRank = calculateRank();
			if (f_gamesAverage.full()) {
				storeRank(p_player, l_game, l_calculateRank);
			}
			l_gameIndex++;
		}
		
	}
	
	private void storeRank(RankPlayer p_player, RankGame p_game, int p_calculateRank) {
		// store the rank into the database for reports
		GameExtModel.getInstance().getElement(p_game).setCalculatedRating(p_calculateRank);
	}

	private int calculateRank() {
		return ProbabilityAs.getInstance().evaluate(f_gamesAverage);
	}

	protected void gameSetup(RankGame p_game) {
		f_gamesAverage.addValue(p_game);
	}

	protected void processRank(int p_currentRank, RankPlayer p_player, List<RankGame> p_games) {
		int l_currentRank = p_currentRank;
		int l_gameIndex = 1;
		f_gamesAverage.clear();
		for (final RankGame l_game : p_games) {
			processGame(l_game);
			Integer l_rank = rankMade(l_currentRank);
			if (l_game.getPlayer().getPin_Player()==14208) {
				RankGameExt l_gameExt = GameExtModel.getInstance().getElement(l_game);
				LOGGER.info("Game: "+l_game.getAgaGameId()+" "+l_gameExt.getCalculatedRating() + " "+l_gameExt.getWins()+" "+l_gameExt.getSecondRating());
				LOGGER.info("Current Rank: "+l_currentRank+" new: "+l_rank);
			}
			if (l_rank != null) {
				incrementalRankObtained(l_rank, p_player, l_gameIndex);
				l_currentRank = l_rank;
			}
			markProcessedGame();
			l_gameIndex++;
		}
	}

	/**
	 * Determine the rank a p_player made in game p_game.
	 * <br>
	 * Look up the rank obtained in game p_game.
	 * Make sure that the player has also won 4+ games and 2+ wins at rank or higher.
	 * 
	 * @param p_rank  int, not null - players current rank
	 * @return
	 */
	protected Integer rankMade(int p_rank) {
		return (f_gamesAverage.full() && gamesWon())? 
				rankCheck(p_rank):null;
	}

	protected boolean gamesWon() {
		int l_testValue = 0;
		Double l_highest = -20d;
		Double l_second = -20d;
		RankGame l_lastGame = null;
		for (RankGame l_game : f_gamesAverage) {
			l_lastGame = l_game;
			if (l_game.getResult() == GameResult.PLAYERWON) {
				l_testValue++;
				Double l_trailingQuality = GameExtModel.getInstance().getElement(l_game).getTrailingQuality();
				if (l_game.getResult() == GameResult.PLAYERWON) {
					if (l_trailingQuality > l_second) {
						if (l_trailingQuality > l_highest) {
							l_second = l_highest;
							l_highest = l_trailingQuality;
						} else {
							l_second = l_trailingQuality;
						}
					}
				}
			}
		}
		if (l_lastGame !=null) {
			RankGameExt l_gameExt = GameExtModel.getInstance().getElement(l_lastGame);
			l_gameExt.setWins(l_testValue);
			l_gameExt.setSecondRating(new Double(l_second*HUNDREDDOUBLE).longValue());
		}
		return l_testValue > 3;
	}
	
	protected boolean gamesSufficient(int p_rank) {
		int l_testValue = 0;
		boolean l_retVal = false;
		float l_rankCheck = p_rank + .5f;
		for (RankGame l_game : f_gamesAverage) {
			if (l_game.getResult() == GameResult.PLAYERWON) {
				Double l_trailingQuality = GameExtModel.getInstance().getElement(l_game).getTrailingQuality();
				if (l_trailingQuality > l_rankCheck) {
					l_testValue++;
					if (l_testValue == 2) {
						l_retVal = true;
						break;
					}
				}
			}
		}
		return l_retVal;
	}
	
	protected Integer rankCheck(int p_rank) {
		Integer l_retVal = null;
		int l_gameValue = ProbabilityAs.getInstance().evaluate(f_gamesAverage);
		for (int l_rank = 7; l_rank != p_rank; l_rank--) {
			if (gamesSufficient(l_rank) && l_gameValue >= (l_rank * 100 + 50)) {
				l_retVal = l_rank;
				break;
			}
		}
		return l_retVal;
	}

	private void processGame(RankGame p_game) {
		f_gamesAverage.addValue(p_game);
	}

	protected int rankNormalized(int p_type) {
		return Math.floorDiv(p_type-50,100);
	}

	protected void markProcessedGame() {
		if (f_gamesAverage.full()) {
			final RankGame l_game = (RankGame) f_gamesAverage.getSeries().getFirst();
			final RankGame l_newGame = new RankGame(l_game);
			l_newGame.setState(GameState.PROCESSED);
			GameModel.getInstance().updateContent(l_game, l_newGame);
		}
	}

	protected void incrementalRankObtained(int p_rank, RankPlayer p_player,
			int l_gameIndex) {
		final LiveRankObtained l_rankObtained = new LiveRankObtained(p_rank, p_player, f_runDate, l_gameIndex);
		l_rankObtained.fillState(f_gamesAverage);
		f_liveRanks.add(l_rankObtained);
	}

	protected void storeCertificates() {
		LOGGER.info("Storing certificates");
		LiveRankObtainedModel.getInstance().doBlockUpdate(() -> {
			LiveRankObtainedModel l_model = LiveRankObtainedModel.getInstance();
			for (final LiveRankObtained l_obtained : f_liveRanks) {
				l_model.addContent(l_obtained);
			}
		});
	}

	/**
	 * @return the players
	 */
	public int getPlayers() {
		return f_players;
	}

	/**
	 * @return the ranks
	 */
	public int getRanks() {
		return f_ranks;
	}

	/**
	 * @return the games
	 */
	public int getGames() {
		return f_games;
	}

	/**
	 * @return the liveRanks
	 */
	public List<LiveRankObtained> getLiveRanks() {
		return f_liveRanks;
	}

	/**
	 * @param p_liveRanks
	 *            the liveRanks to set
	 */
	public void setLiveRanks(List<LiveRankObtained> p_liveRanks) {
		f_liveRanks = p_liveRanks;
	}

	/**
	 * @return the runDate
	 */
	public Date getRunDate() {
		return f_runDate;
	}

	/**
	 * @param p_runDate
	 *            the runDate to set
	 */
	public void setRunDate(Date p_runDate) {
		f_runDate = p_runDate;
	}

}
