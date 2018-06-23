package com.airsltd.aga.ranking.core.data;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GoRankTest {

	@Before
	public void setUp() throws Exception {
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testGetInstance() {
		for (int cnt = 0; cnt != 300; cnt++) {
			assertEquals(cnt, GoRank.getInstance(cnt).getRankValue());
		}
	}

	@Test
	public final void testParseRank() {
		assertEquals(GoRank.getInstance(45), GoRank.parseRank("5k"));
		assertEquals(GoRank.getInstance(45), GoRank.parseRank("5K"));
		assertEquals(GoRank.getInstance(45), GoRank.parseRank("5 Kyu"));
		assertEquals(GoRank.getInstance(55), GoRank.parseRank("5d"));
		assertEquals(GoRank.getInstance(55), GoRank.parseRank("5D"));
		assertEquals(GoRank.getInstance(55), GoRank.parseRank("5 Dan"));
		assertEquals(GoRank.getInstance(74), GoRank.parseRank("5p"));
		assertEquals(GoRank.getInstance(74), GoRank.parseRank("5P"));
		assertEquals(GoRank.getInstance(74), GoRank.parseRank("5 Dan Proffesional"));
		assertEquals(GoRank.getInstance(70), GoRank.parseRank("一段"));
		assertEquals(GoRank.getInstance(71), GoRank.parseRank("二段"));
		assertEquals(GoRank.getInstance(72), GoRank.parseRank("三段"));
		assertEquals(GoRank.getInstance(73), GoRank.parseRank("四段"));
		assertEquals(GoRank.getInstance(74), GoRank.parseRank("五段"));
		assertEquals(GoRank.getInstance(75), GoRank.parseRank("六段"));
		assertEquals(GoRank.getInstance(76), GoRank.parseRank("七段"));
		assertEquals(GoRank.getInstance(77), GoRank.parseRank("八段"));
		assertEquals(GoRank.getInstance(78), GoRank.parseRank("九段"));
		assertEquals(GoRank.getInstance(50), GoRank.parseRank(null));
		assertEquals(GoRank.getInstance(50), GoRank.parseRank(""));
	}

	@Test 
	public final void testToString() {
		assertEquals("GoRank [rankValue=45] 5 kyu", GoRank.getInstance(45).toString());
		assertEquals("GoRank [rankValue=55] 5 dan", GoRank.getInstance(55).toString());
		assertEquals("GoRank [rankValue=74] 5 dan professional", GoRank.getInstance(74).toString());
	}

	@Test
	public final void testRank() {
		assertEquals("5 kyu", GoRank.getInstance(45).rank());
		assertEquals("5 dan", GoRank.getInstance(55).rank());
		assertEquals("5 dan professional", GoRank.getInstance(74).rank());
	}

	@Test
	public final void testSimpleRank() {
		assertEquals("5k", GoRank.getInstance(45).simpleRank());
		assertEquals("5d", GoRank.getInstance(55).simpleRank());
		assertEquals("5p", GoRank.getInstance(74).simpleRank());
	}

	@Test
	public final void testGetRankValue() {
		assertEquals(45, GoRank.getInstance(45).getRankValue());
		assertEquals(55, GoRank.getInstance(55).getRankValue());
		assertEquals(74, GoRank.getInstance(74).getRankValue());
	}

	@Test
	public final void testCompare() {
		// given
		// when
		// then
		assertEquals(-1, GoRank.getInstance(48).compare(GoRank.getInstance(49)));
		assertEquals(2, GoRank.getInstance(51).compare(GoRank.getInstance(49)));
	}
	
	@Test
	public final void testToRating() {
		// given
		// when
		// then
		assertEquals(3.5d, (double)GoRank.getInstance(53).toRating(), .0001d);
		assertEquals(.5d, (double)GoRank.getInstance(49).toRating(), .0001d);
		assertEquals(-.5d, (double)GoRank.getInstance(48).toRating(), .0001d);
		assertNull(GoRank.getInstance(50).toRating());
	}
}
