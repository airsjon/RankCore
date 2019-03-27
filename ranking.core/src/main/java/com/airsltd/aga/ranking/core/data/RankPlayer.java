/**
 *
 */
package com.airsltd.aga.ranking.core.data;

import java.io.Serializable;
import java.sql.Date;

import com.airsltd.aga.ranking.core.model.PlayerModel;
import com.airsltd.core.data.AbstractPersistedIdData;
import com.airsltd.core.data.annotations.AirsPersistentClass;
import com.airsltd.core.data.annotations.AirsPersistentField;
import com.airsltd.core.data.annotations.FieldStyle;

/**
 * @author jon_000
 *
 */
@AirsPersistentClass(table = "players", keys = 1, style = FieldStyle.CAPITALIZED)
public class RankPlayer extends AbstractPersistedIdData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2234015339024929543L;
	/**
	 * Internal field to store number of games played.
	 */
	private int f_games;
	@AirsPersistentField(fieldName = "dob")
	private Date f_dob;

	public RankPlayer() {
		super(RankPlayer.class);
	}

	public RankPlayer(int p_i) {
		this();
		setId(p_i);
	}

	public boolean playedGames() {
		return f_games != 0;
	}

	public void playedGame() {
		f_games++;
	}

	@Override
	protected void addContent() {
		PlayerModel.getInstance().addContent(this);
	}

	@Override
	protected String getIdFieldName() {
		return "Player_ID";
	}

	@Override
	public long primaryKeyFields() {
		return 1;
	}

	/**
	 * @return the pin_Player
	 */
	public long getPin_Player() {
		return getId();
	}

	/**
	 * @param p_pin_Player
	 *            the pin_Player to set
	 */
	public void setPin_Player(long p_pin_Player) {
		setId(p_pin_Player);
	}

	/**
	 * @return the dob
	 */
	public Date getDob() {
		return f_dob;
	}

	/**
	 * @param p_dob
	 *            the dob to set
	 */
	public void setDob(Date p_dob) {
		f_dob = p_dob;
	}

}
