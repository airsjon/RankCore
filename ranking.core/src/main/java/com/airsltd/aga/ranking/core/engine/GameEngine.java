/**
 *
 */
package com.airsltd.aga.ranking.core.engine;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import com.airsltd.aga.ranking.core.data.GameState;
import com.airsltd.aga.ranking.core.data.RankGame;
import com.airsltd.core.NotificationStatus;
import com.airsltd.core.data.CoreInterface;

/**
 * Load the rules for game state assignment and process the unassigned games.
 * <p>
 *
 * @author Jon Boley
 *
 */
public class GameEngine extends ApplyRulesEngine<GameState, RankGame> {

	/**
	 * The threshold to exclude games at.
	 */
	private static final float THRESHOLD = .05f;
	private Date f_startDate;
	private Date f_endDate;
	private float f_threshold = THRESHOLD;

	public GameEngine(Date p_startDate, Date p_endDate) {
		super();
		f_startDate = p_startDate;
		f_endDate = p_endDate;
		loadRules();
	}

	/**
	 * Load the rules.
	 * <p>
	 *
	 */
	protected void loadRules() {
		final List<IRule<GameState, RankGame>> l_rules = new ArrayList<>();
		l_rules.add(new CollapseGameRule());
		l_rules.add(new OutOfOrderRule());
		l_rules.add(new InvalidHandicapRule());
		l_rules.add(new GameNotReadyRule());
		l_rules.add(new GameAfterDateRule(f_startDate));
		l_rules.add(new GameBeforeDate(f_endDate));
		l_rules.add(new GameThresholdRule(f_threshold));
		setRules(l_rules);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.airsltd.aga.ranking.core.engine.ApplyRulesEngine#process()
	 */
	@Override
	public void process() {
		GameState.clearAll();
		process(false);
		GameState.logCounts();
	}

	protected void process(boolean p_reanalyze) {
		boolean l_recursive = false;
		try (GameIterator l_iterator = new GameIterator(p_reanalyze)) {
			if (l_iterator.hasNext()) {
				setIterator(l_iterator);
				super.process();
				l_recursive = true;
			}
 		} catch (final Exception e) {
			CoreInterface.getSystem().handleException("Unable to process games", e, NotificationStatus.BLOCK);
		}
		if (l_recursive) {
			process(true);
		}
	}

	public void reset() {
		
	}

}
