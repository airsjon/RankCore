/**
 * Copyright (c) 2012, Jon Boley
 * 432 NE Ravenna Blvd
 * Seattle, WA 98115
 */
package com.airsltd.aga.ranking.variables;

import static org.mockito.BDDMockito.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import com.airsltd.core.ICoreInterface;
import com.airsltd.core.data.CoreInterface;

/**
 * @author Jon
 *
 */
public class ConnectionSetupTest  {

	protected ICoreInterface m_system;
	protected Connection m_connection;
	protected PreparedStatement m_ps;
	protected ResultSet m_rs;
	protected Statement m_state;
	private ICoreInterface s_oldSystem;

	public void setUp() throws Exception {
		// given
		m_system = mock(ICoreInterface.class);
		s_oldSystem = CoreInterface.getSystem();
		CoreInterface.setSystem(m_system);
		m_connection = mock(Connection.class);
		m_ps = mock(PreparedStatement.class);
		m_rs = mock(ResultSet.class);
		m_state = mock(Statement.class);
		given(m_system.getConnection()).willReturn(m_connection );
		given(m_connection.prepareStatement(anyString())).willReturn(m_ps);
		given(m_connection.prepareStatement(anyString(),anyInt())).willReturn(m_ps);
		given(m_connection.createStatement()).willReturn(m_state);
		given(m_state.executeQuery(anyString())).willReturn(m_rs);
		given(m_state.executeUpdate(anyString())).willReturn(4);
		given(m_ps.executeQuery()).willReturn(m_rs);
	}

	public void tearDown() throws Exception {
		CoreInterface.setSystem(s_oldSystem);
	}
}
