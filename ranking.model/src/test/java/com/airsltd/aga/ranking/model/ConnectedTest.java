package com.airsltd.aga.ranking.model;

import java.sql.Connection;

import com.airsltd.core.data.AbstractConnectedTest;

public class ConnectedTest extends AbstractConnectedTest {

	private boolean f_readOnly;

	@Override
	protected Connection getTestConnection() {
		return f_readOnly?RankSqlConnection.getInstance().getConnection(false):
			RankMockSqlConnection.getInstance().getConnection();
	}

	public void setUp(boolean p_readOnly) throws Exception {
		f_readOnly = p_readOnly;
		super.setUp();
	}

}
