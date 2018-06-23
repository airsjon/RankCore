/**
 * 
 */
package com.airsltd.aga.ranking.convert;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.airsltd.aga.ranking.core.data.Color;
import com.airsltd.aga.ranking.core.data.GoRank;
import com.airsltd.aga.ranking.core.data.RankGame;
import com.airsltd.aga.ranking.core.data.RankGameLink;
import com.airsltd.aga.ranking.core.data.RankPlayer;
import com.airsltd.aga.ranking.core.data.RankRating;
import com.airsltd.aga.ranking.core.data.RankTournament;
import com.airsltd.aga.ranking.core.model.GameModel;
import com.airsltd.aga.ranking.core.model.GameReverseModel;
import com.airsltd.aga.ranking.core.model.PlayerModel;
import com.airsltd.aga.ranking.core.model.RankConnection;
import com.airsltd.aga.ranking.core.model.RatingModel;
import com.airsltd.aga.ranking.core.model.TournamentModel;
import com.airsltd.aga.ranking.core.states.GameResult;
import com.airsltd.core.NotificationStatus;
import com.airsltd.core.collections.AirsCollections;
import com.airsltd.core.collections.BooleanFunction;
import com.airsltd.core.data.AirsConnectionException;
import com.airsltd.core.data.AirsJavaDatabaseApp;
import com.airsltd.core.data.CoreInterface;
import com.airsltd.core.data.ISqlConnection;
import com.airsltd.core.data.converters.BlockConverters;
import com.airsltd.core.math.Counter;
import com.airsltd.core.model.ListModel;
import com.airsltd.core.model.PersistentIdSegmentedListModel;
import com.airsltd.core.model.SegmentedListModel;
import com.mysql.cj.xdevapi.AbstractDataResult;

/**
 * @author Jon Boley
 *
 */
public class Convert extends AirsJavaDatabaseApp {

	private enum ConvertCount {
		GAMEREAD, GAMESTORED, GAMEEXISTS,
		PLAYERREAD, PLAYERSTORED, PLAYEREXISTS,
		TOURNAMENTREAD, TOURNAMENTSTORED, TOURNAMENTEXISTS,
		RATINGREAD, RATINGSTORED, RATINGEXISTS, RATINGZERO
	}
	
	private static boolean f_incremental;
	private Map<String, RankTournament> f_tournaments = new HashMap<String, RankTournament> ();
	private List<RankGame> f_games = new ArrayList<RankGame>();
	private List<RankRating> f_ratings = new ArrayList<RankRating>();
	private List<RankGameLink> f_gameLinks = new ArrayList<RankGameLink>();
	private Date f_date;
	private Counter<ConvertCount> f_counts = Counter.fromClass(ConvertCount.class);
	private List<RankPlayer> f_players = new ArrayList<RankPlayer>();
	private static final Log LOGGER = LogFactory.getLog(Convert.class);
	
	public Convert(Date p_date) {
		f_date = p_date;
	}

	/**
	 * @param args
	 * @throws ParseException 
	 */
	public static void main(String[] p_args) throws ParseException {
		
		setIncremental(switchExists(p_args, "--incremental", "-i"));
		Date l_date = BlockConverters.DATECONVERTER.fromSql(null, getArgData(p_args, "--until=", "-u"));
		String l_argData = getArgData(p_args, "--since=", "-s");
		Date l_preDate = l_argData == null?null:BlockConverters.DATECONVERTER.fromSql(null, l_argData);
		Convert l_convert = new Convert(l_date);
		ISqlConnection l_connection = RankConnection.getInstance();
		try {
			l_convert.initializeDatabase(l_connection);
			if (!isIncremental()) {
				l_convert.truncateDatabases();
			}
			
			l_convert.loadModels();
			l_convert.convertPlayers();
			l_convert.convertTournaments();
			l_convert.convertGames(l_preDate);
			l_convert.convertRatings();
			LOGGER.info(l_convert.getCounts());
			l_convert.storeTournaments();
			l_convert.storePlayers();
			l_convert.storeRatings();
			l_convert.storeGames();
			l_convert.storeGameLinks();
			if (!isIncremental()) {
				try (Connection l_conn = CoreInterface.getSystem().getConnection();
						 Statement l_statement = l_conn.createStatement()) {
					LOGGER.info("Inserting AGA ranks from US Opens");;
					l_statement.execute("insert into liverankobtained (Player_ID, Rank, RunDate, accepted) select Player_ID, rank_obtained, daterecorded, accepted from certificates join playerData on Pin_Player=Player_ID;");
				}
			}
		} catch (SQLException e) {
			LOGGER.error("Unable to process SQL requext",e);
		} finally {
			l_convert.dumpStastics(System.out);
		}
		
	}

	protected void truncateDatabases() throws SQLException {
		LOGGER.info("Deleting Databases");
		try (Connection l_connection = CoreInterface.getSystem().getConnection();
			 Statement l_statement = l_connection.createStatement()) {
			l_statement.execute("delete from `usgo_agarank`.`games`");
			l_statement.execute("delete from `usgo_agarank`.`gamelink`");
			l_statement.execute("delete from `usgo_agarank`.`ratings`");
			l_statement.execute("delete from `usgo_agarank`.`tournament`");
			l_statement.execute("delete from `usgo_agarank`.`players`");
			l_statement.execute("delete from `usgo_agarank`.`liverankobtained`");
		}
	}

	protected void loadModels() {
		LOGGER.info("Loading Players");
		PlayerModel.getInstance().loadModel("");
		LOGGER.info("Loading Tournament");
		TournamentModel.getInstance().loadModel("");
		LOGGER.info("Loading Game");
		GameModel.getInstance().loadModel(null);
		LOGGER.info("Loading Rating");
		RatingModel.getInstance().loadModel(null);
	}

	protected void storeGames() {
		LOGGER.info("Storing Games");
		List<RankGame> l_games = new ArrayList<RankGame>(f_games);
//		Collections.shuffle(l_games);
		PersistentIdSegmentedListModel<RankGame, RankPlayer> l_model = GameModel.getInstance();
		try {
			l_model.setInitialLoad(true);
			l_model.startBlock();
			for (RankGame l_game : l_games) {
				l_model.addContent(l_game);
			}
			l_model.endBlock();
		} finally {
			l_model.setInitialLoad(false);
		}
	}

	protected void storeGameLinks() {
		LOGGER.info("Storing Game Links");
		GameReverseModel l_model = GameReverseModel.getInstance();
		try {
			l_model.setInitialLoad(true);
			l_model.startBlock();
			for (RankGameLink l_gameLink : f_gameLinks) {
				l_model.addContent(l_gameLink);
			}
			l_model.endBlock();
		} finally {
			l_model.setInitialLoad(false);
		}
	}


	protected void storeRatings() {
		LOGGER.info("Storing Ratings");
		SegmentedListModel<RankRating, RankPlayer> l_model = RatingModel.getInstance();
		l_model.startBlock();
		for (RankRating l_rating : f_ratings) {
			if (l_rating.getPlayer().playedGames()) {
				l_model.addContent(l_rating);
			}
		}
		l_model.endBlock();
	}


	protected void storePlayers() {
		LOGGER.info("Storing Players");
		ListModel<RankPlayer, Object> l_model = PlayerModel.getInstance();
		l_model.startBlock();
		for (RankPlayer l_player : f_players) {
			if (l_player.playedGames()) {
				l_model.addContent(l_player);
			}
		}
		l_model.endBlock();
	}


	protected void storeTournaments() {
		LOGGER.info("Storing Tournaments");
		List<RankTournament> l_tournaments = new ArrayList<RankTournament>(f_tournaments.values());
		Collections.shuffle(l_tournaments);
		ListModel<RankTournament, Object> l_model = TournamentModel.getInstance();
		l_model.doBlockUpdate(() -> {
			for (RankTournament l_tournament : l_tournaments) {
				l_model.addContent(l_tournament);
			}
		});
	}


	protected void convertPlayers() throws SQLException {
		LOGGER.info("Convert Players");
		long l_currentMaxId = 0;
		for (RankPlayer l_player : PlayerModel.getInstance().getContentAsList("")) {
			if (l_player.getPin_Player()>l_currentMaxId) {
				l_currentMaxId = l_player.getPin_Player();
			}
		};
		String l_selectString = " WHERE p.Pin_Player > "+l_currentMaxId+ (f_date!=null?" AND Elab_Date <" + currentDate():"");
		try (Connection l_connection = CoreInterface.getSystem().getConnection();
				 PreparedStatement l_ps = l_connection.prepareStatement(
						 "SELECT Pin_Player, dob FROM usgo_agagd.players p left join usgo_agagd.members m on p.Pin_Player=m.member_id"
						 + l_selectString);
				 ResultSet l_rs = l_ps.executeQuery()) {
			PlayerModel l_model = PlayerModel.getInstance();
			while (l_rs.next()) {
				f_counts.count(ConvertCount.PLAYERREAD);
				int l_agaId = l_rs.getInt("Pin_Player");
				boolean l_exists = PlayerModel.getInstance().getElement(l_agaId)!=null ||
						AirsCollections.testFor((RankPlayer p) -> p.getId()==l_agaId, f_players, true);
				if (!l_exists) {
					RankPlayer l_player = l_model.fromAGAId(l_agaId);
					try {
						Date l_date = l_rs.getDate("dob");
						l_player.setDob(l_date);
					} catch (IllegalArgumentException p_e) {
						LOGGER.error("Invalid dob for Player ["+l_agaId+"]: "+l_rs.getString("dob"));
					}
					f_players.add(l_player);
					f_counts.count(ConvertCount.PLAYERSTORED);
					LOGGER.trace("Added player: "+l_agaId);
				} else {
					f_counts.count(ConvertCount.PLAYEREXISTS);
				}
			}
		}
		
	}
	
	private String currentDate() {
		return currentDate(f_date);
	}

	private String currentDate(Date p_date) {
		return BlockConverters.DATECONVERTER.toSql(null, p_date);
	}

	protected void convertTournaments() throws SQLException {
		LOGGER.info("Convert Tournaments");
		try (Connection l_connection = CoreInterface.getSystem().getConnection();
				 PreparedStatement l_ps = l_connection.prepareStatement("SELECT Tournament_Code, Tournament_Date, Rounds, Tournament_Descr FROM usgo_agagd.tournaments"
				 		+ (f_date!=null?" WHERE Tournament_Date < "+currentDate():""));
				 ResultSet l_rs = l_ps.executeQuery()) {
			while (l_rs.next()) {
				String l_code = l_rs.getString("Tournament_Code");
				f_counts.count(ConvertCount.TOURNAMENTREAD);
				if (!TournamentModel.getInstance().contains(l_code)) {
					RankTournament l_tournament = new RankTournament();
					l_tournament.setDate(l_rs.getDate("Tournament_Date"));
					l_tournament.setRounds(l_rs.getInt("Rounds"));
					l_tournament.setCode(l_code);
					l_tournament.setDesc(l_rs.getString("Tournament_Descr"));
					f_tournaments.put(l_code, l_tournament);
					LOGGER.trace("Added tournament: " + l_code);
					f_counts.count(ConvertCount.TOURNAMENTSTORED);
				} else {
					f_counts.count(ConvertCount.TOURNAMENTEXISTS);
				}
			}
		}
	}

	protected void convertRatings() throws SQLException {
		LOGGER.info("Converting Ratings");
		List<RankRating> l_ratings = new ArrayList<RankRating>();
		loadRatingsLocal(l_ratings);
		f_ratings.addAll(l_ratings);
	}

	protected void loadRatingsLocal(List<RankRating> p_ratings) throws SQLException {
		String l_selectString = f_date==null?" where Rating <> 0.0":" where Rating <> 0.0 and Elab_Date <"+currentDate();
		try (Connection l_connection = CoreInterface.getSystem().getConnection();
				 PreparedStatement l_lastRating = l_connection.prepareStatement(
						 "SELECT MAX(Elab_Date) lastDate FROM usgo_agagd.ratings"+l_selectString);
				 ResultSet l_lastDateRS = l_lastRating.executeQuery();
				 PreparedStatement l_ps = l_connection.prepareStatement(
						 "SELECT * FROM usgo_agagd.ratings" + l_selectString);
				 ResultSet l_rs = l_ps.executeQuery()) {
			while (l_rs.next()) {
				f_counts.count(ConvertCount.RATINGREAD);
				RankPlayer l_player = getPlayer(l_rs.getInt("Pin_Player"));
				Date l_date = l_rs.getDate("Elab_Date");
				if (l_date == null) {
					l_date = Date.valueOf("1960-01-01");
				}
				if (!RatingModel.getInstance().contains(l_player, l_date)) {
					float l_rating = l_rs.getFloat("Rating");
					float l_sigma = l_rs.getFloat("Sigma");
					if (Float.compare(l_rating, 0f)!=0) {
						RankRating l_rankRating = new RankRating();
						l_rankRating.setPlayer(l_player);
						l_rankRating.setDate(l_date);
						l_rankRating.setRating(l_rating);
						l_rankRating.setSigma(l_sigma);
						p_ratings.add(l_rankRating);
						LOGGER.trace("Added rating record for "+l_player+" on "+l_date);
						f_counts.count(ConvertCount.RATINGSTORED);
					} else {
						LOGGER.trace("Rating record of zero found for "+l_player+" who has games before last rating run");
						f_counts.count(ConvertCount.RATINGZERO);
					}
				} else {
					f_counts.count(ConvertCount.RATINGEXISTS);
				}
			}
		}
	}

	protected boolean convertGames(Date p_preDate) throws SQLException {
		boolean l_retVal = false;
		LOGGER.info("Converting Games");
		try (Connection l_connection = CoreInterface.getSystem().getConnection();
				PreparedStatement l_ps = l_connection.prepareStatement(
						"SELECT * FROM usgo_agagd.games WHERE Exclude=0"
						+ (f_date==null?"":" and Game_Date<"+currentDate())
						+ (p_preDate==null?"":" and Game_Date>"+currentDate(p_preDate))); 
				ResultSet l_rs = l_ps.executeQuery()) {
				while (l_rs.next()) {
					f_counts.count(ConvertCount.GAMEREAD);
					int l_gameId = l_rs.getInt("Game_ID");
					RankTournament l_tournament = f_tournaments.get(l_rs.getString("Tournament_Code"));
					Date l_date = l_rs.getDate("Game_Date");
					int l_round = l_rs.getInt("Round");
					RankPlayer l_whitePlayer = getPlayer(l_rs.getInt("Pin_Player_1"));
					RankPlayer l_blackPlayer = getPlayer(l_rs.getInt("Pin_Player_2"));
					boolean l_whiteWon = "W".equals(l_rs.getString("Result"));
					GoRank l_whiteRank = GoRank.parseRank(l_rs.getString("Rank_1"));
					GoRank l_blackRank = GoRank.parseRank(l_rs.getString("Rank_2"));
					int l_handicap = l_rs.getInt("Handicap");
					int l_komi = l_rs.getInt("Komi");
					RankGame l_firstGame = GameModel.getInstance().getAGAElement(l_whitePlayer, l_gameId);
					boolean l_firstGameNew = false;
					if (l_firstGame != null) {
						f_counts.count(ConvertCount.GAMEEXISTS);
					} else {
						// create white game
						l_firstGame = createGame(l_tournament, l_round, l_whitePlayer, l_date, Color.WHITE,  l_whiteWon?GameResult.PLAYERWON:GameResult.PLAYERLOST,
								l_whiteRank, l_blackPlayer, l_blackRank, l_handicap, l_komi, l_gameId);
						l_whitePlayer.playedGame();
						f_counts.count(ConvertCount.GAMESTORED);
						l_firstGameNew = true;
					}
					RankGame l_secondGame = GameModel.getInstance().getAGAElement(l_blackPlayer, l_gameId);
					if (l_secondGame != null) {
						f_counts.count(ConvertCount.GAMEEXISTS);
					} else {
						// create black game
						l_secondGame = createGame(l_tournament, l_round, l_blackPlayer, l_date, Color.BLACK,  l_whiteWon?GameResult.PLAYERLOST:GameResult.PLAYERWON,
								l_blackRank, l_whitePlayer, l_whiteRank, l_handicap, l_komi, l_gameId);
						l_blackPlayer.playedGame();
						f_gameLinks.add(new RankGameLink(l_secondGame, l_firstGame));
						LOGGER.trace("Added game: "+l_gameId);
						f_counts.count(ConvertCount.GAMESTORED);
					}
					if (l_firstGameNew) {
						f_gameLinks.add(new RankGameLink(l_firstGame, l_secondGame));
					}
				}
		} catch (SQLException se) {
			CoreInterface.getSystem().handleException("Unable to Convert", se, NotificationStatus.LOG);
		} finally {
			l_retVal = true;
		}
		return l_retVal;
	}

	protected RankPlayer getPlayer(int p_int) {
		return PlayerModel.getInstance().fromAGAId(p_int);
	}


	protected RankGame createGame(RankTournament p_tournament, int p_round, RankPlayer p_player, Date p_date, 
			Color p_color, GameResult p_result, GoRank p_rank, RankPlayer p_opponent, GoRank p_opponentRank, 
			int p_handicap, int p_komi, int p_gameId) {
		RankGame l_game = new RankGame();
		l_game.setColor(p_color);
		l_game.setDate(p_date);
		l_game.setHandicap(p_handicap);
		l_game.setKomi(p_komi);
		l_game.setOpponent(p_opponent);
		l_game.setOpponentRank(p_opponentRank);
		l_game.setPlayer(p_player);
		l_game.setRank(p_rank);
		l_game.setResult(p_result);
		l_game.setRound(p_round);
		l_game.setTournament(p_tournament);
		l_game.setAgaGameId(p_gameId);
		f_games.add(l_game);
		return l_game;
	}

	/**
	 * @return the games
	 */
	public List<RankGame> getGames() {
		return f_games;
	}

	/**
	 * @return the tournaments
	 */
	public Map<String, RankTournament> getTournaments() {
		return f_tournaments;
	}

	/**
	 * @return the ratings
	 */
	public List<RankRating> getRatings() {
		return f_ratings;
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

	/**
	 * @return the counts
	 */
	public Counter<ConvertCount> getCounts() {
		return f_counts;
	}

	/**
	 * @param p_counts the counts to set
	 */
	public void setCounts(Counter<ConvertCount> p_counts) {
		f_counts = p_counts;
	}

	/**
	 * @return the players
	 */
	public List<RankPlayer> getPlayers() {
		return f_players;
	}

	/**
	 * @param p_players the players to set
	 */
	public void setPlayers(List<RankPlayer> p_players) {
		f_players = p_players;
	}

}
