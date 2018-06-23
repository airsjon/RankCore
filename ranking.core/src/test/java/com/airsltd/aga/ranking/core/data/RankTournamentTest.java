package com.airsltd.aga.ranking.core.data;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.airsltd.core.data.IBlockData;

public class RankTournamentTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	private RankTournament f_rankTournament;

	@Before
	public void setUp() throws Exception {
		f_rankTournament = new RankTournament();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAutoIncrements() {
		// given
		// when
		// then
		assertEquals(1, f_rankTournament.autoIncrements());
	}

	@Test
	public void testTableName() {
		// given
		// when
		// then
		assertEquals("tournament", f_rankTournament.tableName());
	}

	@Test
	public void testKeyFields() {
		// given
		// when
		// then
		assertEquals(1, f_rankTournament.keyFields());
	}

	@Test
	public void testAutoIncrementField() {
		// given
		// when
		f_rankTournament.autoIncrementField(0, 52l);
		f_rankTournament.autoIncrementField(1, 32l);
		// then
		assertEquals(52l, f_rankTournament.getPersistentID());
	}

	@Test
	public void testCopy() {
		// given
		String [] l_inString = new String [] {
				"32", "2014-02-18", "5"
		};
		// when
		f_rankTournament.fromStringCsv(l_inString);
		RankTournament l_tourn1 = new RankTournament(f_rankTournament);
		IBlockData l_tourn2 = f_rankTournament.copy();
		// then
		assertEquals(f_rankTournament, l_tourn1);
		assertEquals(f_rankTournament, l_tourn2);
		assertEquals(l_tourn1, l_tourn2);
		assertTrue(f_rankTournament!= l_tourn1);
		assertTrue(f_rankTournament!= l_tourn2);
		assertTrue(l_tourn1!= l_tourn2);
		
		
	}

	@Test
	public void testPrimaryKeyFields() {
		// given
		// when
		// then
		assertEquals(1, f_rankTournament.primaryKeyFields());
	}

	@Test
	public void testToStringCsv() {
		// given
		String [] l_inString = new String [] {
				"32", "2014-02-18", "5"
		};
		String [] l_outString = new String [] {
				"32", "'2014-02-18'", "5", "''", "''"
		};
		// when
		f_rankTournament.fromStringCsv(l_inString);
		// then
		assertArrayEquals(l_outString, f_rankTournament.toStringCsv());

	}

}
