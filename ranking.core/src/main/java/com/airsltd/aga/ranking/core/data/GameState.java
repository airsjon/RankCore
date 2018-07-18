/**
 *
 */
package com.airsltd.aga.ranking.core.data;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.airsltd.core.IPrettyObject;

/**
 * The state that a game can have.
 * <p>
 * Values are {@link #UNKNOWN}, {@link #REJECTED}, {@link #VALID},
 * {@link #PROCESSED}, {@link #COLLAPSED}, {@link #NOTREADY}
 * 
 * @author Jon Boley
 *
 */
public enum GameState implements IPrettyObject {
	/**
	 * Initial state of all games.
	 * <p>
	 * Also, after a run, it represents a game that has not defined state.
	 *
	 */
	UNKNOWN("Unknown"), 
	/**
	 * The game will not be used for rank determination
	 */
	REJECTED("Rejected"),
	/**
	 * The game is to be used for rank
	 * determination
	 */
	VALID("Valid"), 
	/**
	 * The game is valid but has already been used for all
	 * possible rank determinations.
	 */
	PROCESSED("Processed"), 
	/**
	 * The game was part of a series of games that has
	 * been collapsed into a single game.
	 */
	COLLAPSED("Collapsed"), 
	/**
	 * The game is not ready for rank processing.
	 * <p>
	 * Most common reason being that the game has not be
	 * rated yet.
	 */
	NOTREADY("NotReady"), 
	/**
	 * Game out of order.
	 * <p>
	 * For now manual intervention needed.
	 */
	OUTOFORDER("OutOfOrder"), 
	/**
	 * The game will need to be analyzed again after
	 * this round of processing.
	 * 
	 */
	REANALYZE("Reanalyze");

	private static Log LOGGER = LogFactory.getLog(GameState.class);
	private String f_prettyString;
	private int f_count;

	GameState(String p_prettyString) {
		f_prettyString = p_prettyString;
	}

	public boolean isRedo() {
		return this == UNKNOWN || this == NOTREADY;
	}

	@Override
	public String niceString() {
		return f_prettyString;
	}

//	public static void loadValues(EvaluationContext p_context) {
//		int l_index = 0;
//		for (final GameState l_state : GameState.values()) {
//			p_context.createValue("GameState" + l_state.niceString(), Type.functionInteger(l_index));
//			l_index++;
//		}
//	}

	public static void clearAll() {
		for (final GameState l_state : GameState.values()) {
			l_state.clear();
		}
	}

	public void record() {
		f_count++;
	}

	public void clear() {
		f_count = 0;
	}

	public int getCount() {
		return f_count;
	}

	public static void logCounts() {
		final StringBuilder l_sb = new StringBuilder();
		for (final GameState l_state : GameState.values()) {
			l_sb.append(l_state);
			l_sb.append(": ");
			l_sb.append(l_state.getCount());
			l_sb.append(System.getProperty("line.separator"));
		}
		LOGGER.info(l_sb.toString());
	}

	public boolean isModelled() {
		return this == VALID || this == PROCESSED;
	}

}
