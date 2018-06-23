/**
 * 
 */
package com.airsltd.aga.ranking.core.data;

import java.sql.Date;

import com.airsltd.core.data.PersistedData;
import com.airsltd.core.data.annotations.AirsPersistentClass;
import com.airsltd.core.data.annotations.AirsPersistentField;

/**
 * @author Jon Boley
 *
 */
@AirsPersistentClass(table="viewGenerated",keys=0x3)
public class ViewCertificate extends PersistedData {

	@AirsPersistentField(fieldName="Pin_Player")
	private int	f_pinPlayer;
	@AirsPersistentField(fieldName="Rank")
	private int f_rank;
	@AirsPersistentField(fieldName="full_name")
	private String f_fullName;
	@AirsPersistentField
	private String f_email;
	@AirsPersistentField
	private	String f_email2;
	@AirsPersistentField(fieldName="RunDate")
	private Date f_runDate;
	@AirsPersistentField(fieldName="Player_ID")
	private int f_playerId;
	/**
	 * @return the pinPlayer
	 */
	public int getPinPlayer() {
		return f_pinPlayer;
	}
	/**
	 * @param p_pinPlayer the pinPlayer to set
	 */
	public void setPinPlayer(int p_pinPlayer) {
		f_pinPlayer = p_pinPlayer;
	}
	/**
	 * @return the fullName
	 */
	public String getFullName() {
		return f_fullName;
	}
	/**
	 * @param p_fullName the fullName to set
	 */
	public void setFullName(String p_fullName) {
		f_fullName = p_fullName;
	}
	/**
	 * @return the email
	 */
	public String getEmail() {
		return f_email;
	}
	/**
	 * @param p_email the email to set
	 */
	public void setEmail(String p_email) {
		f_email = p_email;
	}
	/**
	 * @return the email2
	 */
	public String getEmail2() {
		return f_email2;
	}
	/**
	 * @param p_email2 the email2 to set
	 */
	public void setEmail2(String p_email2) {
		f_email2 = p_email2;
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
	 * @return the runDate
	 */
	public Date getRunDate() {
		return f_runDate;
	}
	/**
	 * @param p_runDate the runDate to set
	 */
	public void setRunDate(Date p_runDate) {
		f_runDate = p_runDate;
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
	 * Generate a nice looking rank string.
	 * 
	 * @return a String representation of the players Rank.
	 * 
	 */
	public CharSequence prettyRank() {
		return Math.abs(f_rank<1?f_rank-1:f_rank) + (f_rank<1?" Kyu":" Dan"); 
	}
	
	public String pdfFile() {
		String l_name = fixName();
		return l_name.replaceAll("\\s", "")+getPinPlayer()+".pdf";
	}

	public String fixName() {
		String l_retVal = getFullName();
		if (l_retVal.contains(",")) {
			String[] l_split = l_retVal.split(",",2);
			l_retVal = l_split[1].trim().replaceAll(",", "")+" "+l_split[0];
		}
		return l_retVal;
	}

	
}
