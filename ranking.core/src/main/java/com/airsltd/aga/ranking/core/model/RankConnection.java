/**
 *
 */
package com.airsltd.aga.ranking.core.model;

import com.airsltd.core.data.ISqlConnection;

/**
 * @author Jon Boley
 *
 */
public class RankConnection implements ISqlConnection {

	private static final int MAXWAIT = 30;
	private static final int MAXACTIVE = 10;

	private static RankConnection INSTANCE;

	private RankConnection() {
		setInstance(this);
	}

	public static void setInstance(RankConnection p_rankConnection) {
		INSTANCE = p_rankConnection;
	}

	public static RankConnection getInstance() {
		if (INSTANCE == null) {
			new RankConnection();
		}
		return INSTANCE;
	}

	@Override
	public void teardownConnection() {
		// nothing to tear down
	}

	@Override
	public void initializeConnection() {
		// nothing special to setup
	}

	@Override
	public String getUser() {
		return "agaRankUser";
	}

	@Override
	public String getUrl() {
		return "jdbc:mysql://localhost/usgo_agarank?serverTimezone=America/Los_Angeles&zeroDateTimeBehavior=convertToNull" + "&characterEncoding=utf-8";
	}

	@Override
	public String getPassword() {
		return "agarank34";
	}

	@Override
	public int getMaxWait() {
		return MAXWAIT;
	}

	@Override
	public int getMaxActive() {
		return MAXACTIVE;
	}

	@Override
	public String getJdbcDriver() {
		return "com.mysql.cj.jdbc.Driver";
	}

}
