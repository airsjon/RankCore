package com.airsltd.aga.ranking.core.analysis;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class RankResultTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	private RankResult f_rankResult1;

	@Before
	public void setUp() throws Exception {
		f_rankResult1 = new RankResult(3.2f, 4.5f, Result.TWOWINS, 4);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testHashCode() {
		// given
		// when
		// then
		assertEquals(13200417, f_rankResult1.hashCode());
	}

	@Test
	public void testToString() {
		// given
		// when
		// then
		assertEquals("RankResult [f_gameOneRating=3.2, f_gameTwoRating=4.5, f_result=TWOWINS, f_rank=4]", f_rankResult1.toString());
	}

	@Test
	public void testPrettyRank() {
		// given
		// when
		// then
		assertEquals("1", RankResult.prettyRank(1));
		assertEquals("-1", RankResult.prettyRank(0));
		assertEquals("-4", RankResult.prettyRank(-3));
	}

	@Test
	public void testPrettyRating() {
		// given
		// when
		// then
		assertEquals("3.2", RankResult.prettyRating(3.2f));
		assertEquals("-1.8", RankResult.prettyRating(0.2f));
		assertEquals("-2.4", RankResult.prettyRating(-.4f));
	}

	@Test
	public void testCompareTo() {
		// given
		// when
		// then
		assertEquals(3, f_rankResult1.compareTo(new RankResult(0f,0f, Result.WIN, 0)));
		assertEquals(-3, f_rankResult1.compareTo(new RankResult(0f,0f, Result.LOSSANDWIN, 0)));
		assertEquals(1, f_rankResult1.compareTo(new RankResult(0f,0f, Result.TWOWINS, 1)));
		assertEquals(-1, f_rankResult1.compareTo(new RankResult(0f,0f, Result.TWOWINS, 7)));
		assertEquals(1, f_rankResult1.compareTo(new RankResult(1.3f,0f, Result.TWOWINS, 4)));
		assertEquals(-1, f_rankResult1.compareTo(new RankResult(4.5f,0f, Result.TWOWINS, 4)));
		assertEquals(1, f_rankResult1.compareTo(new RankResult(3.2f,4.1f, Result.TWOWINS, 4)));
		assertEquals(-1, f_rankResult1.compareTo(new RankResult(3.2f,4.9f, Result.TWOWINS, 4)));
		assertEquals(0, f_rankResult1.compareTo(new RankResult(3.2f,4.5f, Result.TWOWINS, 4)));
	}

	@Test
	public void testEqualsObject() {
		// given
		// when
		// then
		assertFalse(f_rankResult1.equals(new RankResult(0f,0f, Result.WIN, 0)));
		assertFalse(f_rankResult1.equals(new RankResult(0f,0f, Result.LOSSANDWIN, 0)));
		assertFalse(f_rankResult1.equals(new RankResult(0f,0f, Result.TWOWINS, 1)));
		assertFalse(f_rankResult1.equals(new RankResult(0f,0f, Result.TWOWINS, 7)));
		assertFalse(f_rankResult1.equals(new RankResult(1.3f,0f, Result.TWOWINS, 4)));
		assertFalse(f_rankResult1.equals(new RankResult(4.5f,0f, Result.TWOWINS, 4)));
		assertFalse(f_rankResult1.equals(new RankResult(3.2f,4.1f, Result.TWOWINS, 4)));
		assertFalse(f_rankResult1.equals(new RankResult(3.2f,4.9f, Result.TWOWINS, 4)));
		assertFalse(f_rankResult1.equals(new Object()));
		assertFalse(f_rankResult1.equals(new RankResult(3.2f,4.5f, Result.TWOLOSSES, 4)));
		assertFalse(f_rankResult1.equals(new RankResult(3.2f,4.5f, Result.TWOWINS, 3)));
		assertTrue(f_rankResult1.equals(new RankResult(3.2f,4.5f, Result.TWOWINS, 4)));
		assertTrue(f_rankResult1.equals(f_rankResult1));
	}

}
