package com.airsltd.aga.ranking.core.states;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class GameResultTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testFromDelta() {
		// given
		// when
		// then
		assertEquals(GameResult.PLAYERLOST, GameResult.fromDelta(-1));
		assertEquals(GameResult.PLAYERWON, GameResult.fromDelta(0));
	}

	@Test
	public void testNiceString() {
		// given
		// when
		// then
		assertEquals("Unknown", GameResult.NORESULT.niceString());
		assertEquals("Both Forfeit", GameResult.BOTHFORFEIT.niceString());
		assertEquals("Won", GameResult.PLAYERWON.niceString());
		assertEquals("Opponent Forfeit", GameResult.OPPONENTFORFEIT.niceString());
		assertEquals("Forfeit", GameResult.PLAYERFORFEIT.niceString());
		assertEquals("Lost", GameResult.PLAYERLOST.niceString());
	}

}
