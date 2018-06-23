/**
 *
 */
package com.airsltd.aga.ranking.core.data;

import java.sql.Date;

import com.airsltd.aga.ranking.core.model.TournamentModel;
import com.airsltd.core.data.AbstractPersistedIdData;
import com.airsltd.core.data.annotations.AirsPersistentClass;
import com.airsltd.core.data.annotations.AirsPersistentField;
import com.airsltd.core.data.annotations.FieldStyle;

/**
 * @author jon_000
 *
 */
@AirsPersistentClass(keys = 1, table = "tournament", style = FieldStyle.CAPITALIZED)
public class RankTournament extends AbstractPersistedIdData implements ITournament {

	@AirsPersistentField
	private Date f_date;
	@AirsPersistentField
	private int f_rounds;
	@AirsPersistentField(size = 20)
	private String f_code;
	@AirsPersistentField(size = 80)
	private String f_desc;

	public RankTournament(RankTournament p_rankTournament) {
		super(RankTournament.class);
		copy(p_rankTournament);
	}

	public RankTournament() {
		super(RankTournament.class);
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
	 * @return the rounds
	 */
	public int getRounds() {
		return f_rounds;
	}

	/**
	 * @param p_rounds
	 *            the rounds to set
	 */
	public void setRounds(int p_rounds) {
		f_rounds = p_rounds;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.airsltd.aga.ranking.core.data.ITournament#startDate()
	 */
	@Override
	public Date startDate() {
		return getDate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.airsltd.aga.ranking.core.data.ITournament#endDate()
	 */
	@Override
	public Date endDate() {
		return getDate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.airsltd.aga.ranking.core.data.ITournament#roundDate(int)
	 */
	@Override
	public Date roundDate(int p_round) {
		return getDate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.airsltd.aga.ranking.core.data.ITournament#rounds()
	 */
	@Override
	public int rounds() {
		return getRounds();
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return f_code;
	}

	/**
	 * @param p_code
	 *            the code to set
	 */
	public void setCode(String p_code) {
		f_code = p_code;
	}

	/**
	 * @return the descr
	 */
	public String getDesc() {
		return f_desc;
	}

	/**
	 * @param p_descr
	 *            the descr to set
	 */
	public void setDesc(String p_desc) {
		f_desc = p_desc;
	}

	@Override
	public long primaryKeyFields() {
		return 1;
	}

	@Override
	protected void addContent() {
		TournamentModel.getInstance().addContent(this);
	}

	@Override
	protected String getIdFieldName() {
		return "Tournament_ID";
	}

}
