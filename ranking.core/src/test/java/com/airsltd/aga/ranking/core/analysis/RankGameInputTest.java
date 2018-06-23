package com.airsltd.aga.ranking.core.analysis;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class RankGameInputTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	private RankGameInput f_rgi;
	private RankGameInput f_rgi1;
	private RankGameInput f_rgi2;
	private RankGameInput f_rgi3;
	private RankGameInput f_rgi4;
	private RankGameInput f_rgi5;
	private RankGameInput f_rgi6;

	@Before
	public void setUp() throws Exception {
		f_rgi = new RankGameInput(0, 3.2f, false, 3, 0, Result.WIN);
		f_rgi1 = new RankGameInput(1, 3.2f, true, 3, 0, Result.LOSS);
		f_rgi2 = new RankGameInput(2, 3.2f, false, 0, 7, Result.WIN);
		f_rgi3 = new RankGameInput(3, 3.2f, true, 0, 7, Result.LOSS);
		f_rgi4 = new RankGameInput(4, 3.2f, false, 0, 0, Result.WIN);
		f_rgi5 = new RankGameInput(5, 3.2f, true, 0, 0, Result.LOSS);
		f_rgi6 = new RankGameInput("6");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetActualStrength() {
		// given
		// when
		// then
		assertEquals(-1.8f, f_rgi.getActualStrength(),.0001f);
		assertEquals(6.2f, f_rgi1.getActualStrength(),.0001f);
		assertEquals(3.1499f, f_rgi2.getActualStrength(),.0001f);
		assertEquals(3.2501f, f_rgi3.getActualStrength(),.0001f);
		assertEquals(2.62f, f_rgi4.getActualStrength(),.0001f);
		assertEquals(3.78f, f_rgi5.getActualStrength(),.0001f);
		assertEquals(1.4499f, f_rgi6.getActualStrength(),.0001f);
	}

	@Test
	public void testGetSettersAndGetters() {
		// given
		// when
		f_rgi2.setColor(false);
		f_rgi2.setHandicap(9);
		f_rgi2.setIndex("Another Game");
		f_rgi2.setKomi(3);
		f_rgi2.setRating(-2.3f);
		f_rgi2.setResult("2");
		f_rgi3.setResultGame(Result.WINANDLOSS);
		// then
		assertEquals(3,f_rgi.getHandicap());
		assertEquals("0", f_rgi.getIndex());
		assertEquals(0, f_rgi.getKomi());
		assertEquals(Result.WIN, f_rgi.getResultGame());
		assertEquals("1", f_rgi.getResult());
		assertEquals(9,f_rgi2.getHandicap());
		assertEquals("Another Game", f_rgi2.getIndex());
		assertEquals(3, f_rgi2.getKomi());
		assertEquals(Result.LOSS, f_rgi2.getResultGame());
		assertEquals("2", f_rgi2.getResult());
		assertEquals("6", f_rgi3.getResult());
		
	}
}
