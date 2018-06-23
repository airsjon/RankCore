/**
 * (c) 2015, Jon Boley 2703 Wildrose Ct, Bellingham WA 98229
 */
package com.airsltd.aga.ranking.model;

import java.sql.Connection;
import java.sql.SQLException;

import com.airsltd.core.data.MockSqlConnection;

/**
 * @author Jon Boley
 *
 */
public class RankMockSqlConnection extends MockSqlConnection {

	private static RankMockSqlConnection INSTANCE;

	public static RankMockSqlConnection getInstance() {
		if (INSTANCE == null) {
			new RankMockSqlConnection();
		}
		return INSTANCE;
	}
	
	public RankMockSqlConnection() {
		setInstance(this);
	}
	
	public static void setInstance(RankMockSqlConnection p_instance) {
		INSTANCE = p_instance;
	}

	@Override
	protected void copyData(Connection p_ac, String p_dBaseName) throws SQLException {
	}

	@Override
	protected void createSqlFunctions(Connection p_ac, String p_dBaseName) throws SQLException {
	}

	@Override
	protected String getMockDatabase() {
		return "mockusgo_agarank";
	}

	@Override
	protected String getRealDatabase() {
		return "usgo_agarank";
	}

	@Override
	protected String[] getTables() {
		return new String[] { "analysis", "certificates", "gamelink",
				"games", "liverankobtained", "players",
				"rankhistory", "rankobtained", "ratings",
				"tournament", "values", "variables"
		};
	}

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

}
