package com.airsltd.aga.ranking.core.engine;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.airsltd.aga.ranking.core.data.GameState;
import com.airsltd.aga.ranking.core.data.RankGame;

public class AbstractRuleTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	private AbstractRule<GameState, RankGame> f_mockRule;
	private MockRule f_defaultRule;
	private MockRule f_defaultRule2;

	@Before
	public void setUp() throws Exception {
		f_mockRule = new MockRule(3, "mockRule");
		f_defaultRule = new MockRule("DefaultRule");
		f_defaultRule.setIndex(2);
		f_defaultRule2 = new MockRule();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCompareTo() {
		// given
		MockRule[] l_rule = new MockRule[] {
				new MockRule(2, "MockOneRule"),
				new MockRule(2, "MockOneRule"),
				new MockRule(3, "MockOneRule"),
				new MockRule(3, "MockOneRule"),
				new MockRule(4, "MockOneRule"),
				new MockRule(4, "MockOneRule")
		};
		int[][] l_expected = new int[][] {
				{ 0, -1, -1, -1, -1, -1 },
				{ 1,  0, -1, -1, -1, -1 },
				{ 1,  1,  0, -1, -1, -1 },
				{ 1,  1,  1,  0, -1, -1 },
				{ 1,  1,  1,  1,  0, -1 },
				{ 1,  1,  1,  1,  1,  0 }
		};
		// when
		// then
		for (int cnt = 0; cnt != l_rule.length; cnt++) {
			for (int cnt2 = 0; cnt2 != l_rule.length; cnt2++) {
				assertEquals(l_expected[cnt][cnt2], l_rule[cnt].compareTo(l_rule[cnt2]));
				assertEquals(l_expected[cnt2][cnt], l_rule[cnt2].compareTo(l_rule[cnt]));
			}
		}
	}

	@Test
	public void testGetCategory() {
		// given
		// when
		// then
		assertEquals(3, f_mockRule.getCategory());
		assertEquals(10, f_defaultRule.getCategory());
	}

	@Test
	public void testSetCategory() {
		// given
		// when
		f_mockRule.setCategory(5);
		// then
		assertEquals(5, f_mockRule.getCategory());
	}

	@Test
	public void testSetIndex() {
		// given
		// when
		f_mockRule.setIndex(5);
		// then
		assertEquals(5, f_mockRule.getIndex());
	}

	@Test
	public void testGetName() {
		// given
		MockRule l_mockRule = new MockRule(20, "anotherRule");
		// when
		// then
		assertEquals("mockRule", f_mockRule.getName());
		assertEquals("anotherRule", l_mockRule.getName());
		assertEquals("DefaultRule", f_defaultRule.getName());
	}

	@Test
	public void testSetName() {
		// given
		// when
		f_mockRule.setName("mockRule2");
		// then
		assertEquals("mockRule2", f_mockRule.getName());
	}

	@Test
	public void testToString() {
		// given
		// when
		f_mockRule.setIndex(5);
		// then
		assertEquals("mockRule [3:5]", f_mockRule.toString());
	}

	@Test
	public void testNiceString() {
		// given
		// when
		// then
		assertEquals("Rule mockRule", f_mockRule.niceString());
		assertEquals("Rule DefaultRule", f_defaultRule.niceString());
		assertEquals("Rule [10:19]", f_defaultRule2.niceString());
	}

}
