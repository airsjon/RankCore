/**
 *
 */
package com.airsltd.aga.ranking.core.data;

import com.airsltd.core.data.IPersistentId;
import com.airsltd.core.data.PersistedData;
import com.airsltd.core.data.annotations.AirsPersistentClass;
import com.airsltd.core.data.annotations.AirsPersistentField;

/**
 * A table linking the game record created for each player.
 * <p>
 * Each players is given a game record for each game played.
 * This table links the two game records for each individual game.
 * 
 * @author Jon Boley
 *
 */
@AirsPersistentClass(table = "gamelink", keys = 1)
public class RankGameLink extends PersistedData implements IPersistentId {

	@AirsPersistentField(fieldName = "Game_ID")
	private RankGame f_game;
	@AirsPersistentField(fieldName = "Reverse_ID")
	private RankGame f_reverse;

	public RankGameLink() {
	}

	public RankGameLink(RankGame p_firstGame, RankGame p_secondGame) {
		f_game = p_firstGame;
		f_reverse = p_secondGame;
	}

	/**
	 * @return the game
	 */
	public RankGame getGame() {
		return f_game;
	}

	/**
	 * @param p_game
	 *            the game to set
	 */
	public void setGame(RankGame p_game) {
		f_game = p_game;
	}

	/**
	 * @return the reverse
	 */
	public RankGame getReverse() {
		return f_reverse;
	}

	/**
	 * @param p_reverse
	 *            the reverse to set
	 */
	public void setReverse(RankGame p_reverse) {
		f_reverse = p_reverse;
	}

	@Override
	public long primaryKeyFields() {
		return keyFields();
	}

	@Override
	public long getPersistentID() {
		return f_game.getPersistentID();
	}

}
