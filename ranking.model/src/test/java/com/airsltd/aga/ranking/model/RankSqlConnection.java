package com.airsltd.aga.ranking.model;

import java.sql.Connection;
import java.sql.SQLException;

import com.airsltd.core.data.MockSqlConnection;

public class RankSqlConnection extends MockSqlConnection {

	private static RankSqlConnection INSTANCE;

	public static RankSqlConnection getInstance() {
		if (INSTANCE == null) {
			new RankSqlConnection();
		}
		return INSTANCE;
	}
	
	public RankSqlConnection() {
		setInstance(this);
	}
	
	public static void setInstance(RankSqlConnection p_instance) {
		INSTANCE = p_instance;
	}

	@Override
	protected void copyData(Connection p_ac, String p_dBaseName) throws SQLException {
	}

	@Override
	protected void createSqlFunctions(Connection p_ac, String p_dBaseName) throws SQLException {
	}

	@Override
	protected String getDataBase() {
		return "usgo_agarank";
	}

	@Override
	protected void copySQLData() {
	};
	
	/* (non-Javadoc)
	 * @see com.airsltd.core.MockSqlConnection#getUser()
	 */
	@Override
	protected String getUser() {
		return "agaRankUser";
	}

	/* (non-Javadoc)
	 * @see com.airsltd.core.MockSqlConnection#getPassword()
	 */
	@Override
	protected String getPassword() {
		return "agarank34";
	}

	@Override
	protected String getMockDatabase() {
		return "";
	}

	@Override
	protected String getRealDatabase() {
		return "";
	}

	@Override
	protected String[] getTables() {
		return new String[] {};
	}

}