package com.airsltd.aga.ranking.model;

import java.text.ParseException;
import java.sql.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.airsltd.aga.ranking.core.engine.Engine;
import com.airsltd.aga.ranking.core.model.GameExtModel;
import com.airsltd.aga.ranking.core.model.GameModel;
import com.airsltd.aga.ranking.core.model.PlayerModel;
import com.airsltd.aga.ranking.core.model.RankConnection;
import com.airsltd.aga.ranking.core.model.RatingModel;
import com.airsltd.aga.ranking.core.model.TournamentModel;
import com.airsltd.core.data.AirsJavaDatabaseApp;
import com.airsltd.core.data.ISqlConnection;

public class Model extends AirsJavaDatabaseApp {

	private static final Log LOGGER = LogFactory.getLog(Model.class);
	private Engine f_engine;

	/**
	 * 
	 * @param p_args
	 * @throws ParseException 
	 */
	public static void main(String[] p_args) throws ParseException {
		Model l_model = new Model();
		Date l_runDate = getArgDate(p_args, "--runDate=", "-d");
		int l_gameSpan = 6;
		try {
			l_gameSpan = Integer.parseInt(getArgData(p_args, "--span=", "-s"));
		} catch (NumberFormatException nfe) {};
		l_model.loadEngine(new Engine(l_runDate,l_gameSpan));
		ISqlConnection l_connection = RankConnection.getInstance();
		l_model.initializeDatabase(l_connection);
		loadModels();
		l_model.runEngine();
		l_model.dumpStastics(System.out);
	}

	private void loadEngine(Engine p_engine) {
		f_engine = p_engine;
	}

	private void runEngine() {
		f_engine.run();
		LOGGER.info("Players processed: " + f_engine.getPlayers());
		LOGGER.info("Ranks processed: " + f_engine.getRanks());
		LOGGER.info("Games processed: " + f_engine.getGames());
	}

	public static void loadModels() {
		LOGGER.info("Loading Players");
		PlayerModel.getInstance().loadModel("");
		LOGGER.info(PlayerModel.getInstance().getElements().size()+" Players Loaded");
		LOGGER.info("Loading Tournament");
		TournamentModel.getInstance().loadModel("");
		LOGGER.info(TournamentModel.getInstance().getElements().size()+" Tournaments Loaded");
		LOGGER.info("Loading Game");
		GameModel.getInstance().loadModel(null);
		LOGGER.info("Loading Extend Info");
		GameExtModel.getInstance().loadModel(null);
		LOGGER.info(GameModel.getInstance().getData().size()+" Games Loaded");
		LOGGER.info("Loading Rating");
		RatingModel.getInstance().loadModel(null);
		LOGGER.info(RatingModel.getInstance().getData().size()+" Ratings Loaded");
	}


}
