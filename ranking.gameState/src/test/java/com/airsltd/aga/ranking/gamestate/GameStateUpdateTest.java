package com.airsltd.aga.ranking.gamestate;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GameStateUpdateTest extends ConnectionSetup {

	private static final String[] DATESTRING = { "--endDate 12/01/2017 -s 1/1/2010" };

	@Before
	public void setUp() throws Exception {
		super.setUp();
	}
	
	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}
	
	@Test
	public void testGameStateUpdate() {
		fail("Not yet implemented");
	}

	@Test
	public void testMain() {
		fail("Not yet implemented");
	}

	@Test
	public void testLoadModels() {
		fail("Not yet implemented");
	}

	@Test
	public void testProcessGameStatesReset() throws SQLException {
		// given
		// when
		new GameStateUpdate().processGameStates(DATESTRING, true);
		// then
		verify(m_connection).prepareStatement("UPDATE `games` SET State=2 WHERE State=3");
	}

	@Test
	public void testProcessGameStates() throws SQLException {
		// given
		// when
		new GameStateUpdate().processGameStates(DATESTRING, false);
		// then
		
	}

}
