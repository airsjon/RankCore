/**
 * 
 */
package com.airsltd.aga.ranking.gamestate;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.airsltd.aga.ranking.core.engine.GameEngine;
import com.airsltd.aga.ranking.core.model.GameExtModel;
import com.airsltd.aga.ranking.core.model.GameModel;
import com.airsltd.aga.ranking.core.model.PlayerModel;
import com.airsltd.aga.ranking.core.model.RankConnection;
import com.airsltd.aga.ranking.core.model.RatingModel;
import com.airsltd.aga.ranking.core.model.TournamentModel;
import com.airsltd.core.data.AirsJavaDatabaseApp;
import com.airsltd.core.data.CoreInterface;
import com.airsltd.core.data.ISqlConnection;

/**
 * Check and set the status of all unknown Games.
 * 
 * @author Jon Boley
 *
 */
public class GameStateUpdate extends AirsJavaDatabaseApp {

	private static final Log LOGGER = LogFactory.getLog(GameStateUpdate.class);
	
	public GameStateUpdate(String[] p_args) {
		super(p_args);
	}

	/**
	 * @param args
	 * @throws ParseException 
	 */
	public static void main(String[] p_args) throws ParseException {
		
		GameStateUpdate l_gameState = new GameStateUpdate(p_args);
		ISqlConnection l_connection = RankConnection.getInstance();
		boolean l_reset = l_gameState.switchExists(p_args, "--reset", "-r");
		try {
			l_gameState.initializeDatabase(l_connection);
			if (!l_reset) {
				l_gameState.loadModels();
			}
			l_gameState.processGameStates(p_args, l_reset);
		} catch (SQLException e) {
			LOGGER.error("Unable to process SQL requext",e);
		} finally {
			l_gameState.dumpStastics(System.out);
		}
		
	}

	protected void loadModels() {
		LOGGER.info("Loading Players");
		PlayerModel.getInstance().loadModel("");
		LOGGER.info("Loading Tournament");
		TournamentModel.getInstance().loadModel("");
		LOGGER.info("Loading Game");
		GameModel.getInstance().loadModel(null);
		LOGGER.info("Loading Extend Game Data");
		GameExtModel.getInstance().loadModel(null);
		LOGGER.info("Loading Rating");
		RatingModel.getInstance().loadModel(null);
	}

	protected void processGameStates(String[] p_args, boolean p_reset) throws SQLException {
		if (p_reset) {
			/**
			 * reset all games that have been processed to unprocessed (state 3 set to state 2)
			 */
			LOGGER.info("Reseting Games");
			try (PreparedStatement l_ps = CoreInterface.getSystem().getConnection().prepareStatement(
					"UPDATE `games` SET State=2 WHERE State=3")) {
				l_ps.execute();
			}
			LOGGER.info("Games Reset");
			/**
			 * Clear all ranks that have been obtained.
			 */
			LOGGER.info("Clearing Ranks");
			try (PreparedStatement l_ps = CoreInterface.getSystem().getConnection().prepareStatement(
					"TRUNCATE TABLE `liverankobtained`")) {
				l_ps.execute();
			}
			LOGGER.info("Ranks Cleared");
		} else {
			LOGGER.info("Processing Games");
			Date l_startDate;
			Date l_endDate;
			try {
				l_startDate = getArgDate(p_args, "--endDate", "-e");
				l_endDate = getArgDate(p_args, "--startDate", "-s");
				final GameEngine l_engine = new GameEngine(l_startDate, l_endDate);
				GameModel.getInstance().doBlockUpdate(()  -> l_engine.process());
				LOGGER.info(l_engine.dumpCounts());
			} catch (ParseException e) {
				LOGGER.error("Unable to parse date switches.", e);
			}
		}
	}

}
