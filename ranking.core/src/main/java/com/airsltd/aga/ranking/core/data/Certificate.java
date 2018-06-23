/**
 * 
 */
package com.airsltd.aga.ranking.core.data;

import java.sql.Date;

import com.airsltd.core.data.PersistedData;
import com.airsltd.core.data.annotations.AirsPersistentClass;
import com.airsltd.core.data.annotations.AirsPersistentField;

/**
 * @author jon_000
 *
 */
@AirsPersistentClass(table="certificates", keys=0x3)
public class Certificate extends PersistedData {

	@AirsPersistentField(fieldName="Pin_Player")
	private int 	f_playerId;
	@AirsPersistentField(fieldName="rank_obtained")
	private int		f_rank;
	@AirsPersistentField
	private Date 	f_date;
	@AirsPersistentField
	private boolean	f_accepted;
	
	public Certificate(int p_id, int p_rank, Date p_date, boolean p_accepted) {
		f_playerId = p_id;
		f_rank = p_rank;
		f_date = p_date;
		f_accepted = p_accepted;
	}
	public Certificate(Certificate p_old) {
		copy(p_old);
	}
	/**
	 * @return the playerId
	 */
	public int getPlayerId() {
		return f_playerId;
	}
	/**
	 * @param p_playerId the playerId to set
	 */
	public void setPlayerId(int p_playerId) {
		f_playerId = p_playerId;
	}
	/**
	 * @return the rank
	 */
	public int getRank() {
		return f_rank;
	}
	/**
	 * @param p_rank the rank to set
	 */
	public void setRank(int p_rank) {
		f_rank = p_rank;
	}
	/**
	 * @return the date
	 */
	public Date getDate() {
		return f_date;
	}
	/**
	 * @param p_date the date to set
	 */
	public void setDate(Date p_date) {
		f_date = p_date;
	}
	/**
	 * @return the accepted
	 */
	public boolean isAccepted() {
		return f_accepted;
	}
	/**
	 * @param p_accepted the accepted to set
	 */
	public void setAccepted(boolean p_accepted) {
		f_accepted = p_accepted;
	}
	

}
