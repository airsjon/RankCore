package com.airsltd.aga.ranking.core.model;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.airsltd.aga.ranking.core.data.RankTournament;

public class TournamentModelTest {

	TournamentModel f_tournamentModel;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		TournamentModel.setInstance(null);
		f_tournamentModel = TournamentModel.getInstance();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetSelectionQueryObject() {
		// given
		Object l_obj = new Object();
		// when
		String l_retVal = f_tournamentModel.getSelectionQuery(l_obj);
		// then
		assertEquals("Select * From tournament order by Tournament_ID", l_retVal);
	}

	@Test
	public void testAddModelDataListOfRankTournament() {
		// given
		RankTournament l_tourn1 = new RankTournament();
		l_tourn1.setId(1l);
		l_tourn1.setCode("tourn1");
		RankTournament l_tourn2 = new RankTournament();
		l_tourn1.setId(2l);
		l_tourn2.setCode("tourn2");
		RankTournament l_tourn3 = new RankTournament();
		l_tourn1.setId(3l);
		l_tourn3.setCode("tourn3");
		List<RankTournament> l_data = Arrays.asList(l_tourn1, l_tourn2, l_tourn3);
		// when
		assertFalse(f_tournamentModel.contains("tourn1"));
		assertFalse(f_tournamentModel.contains("tourn2"));
		assertFalse(f_tournamentModel.contains("tourn3"));
		f_tournamentModel.addModelData(l_data);
		// then
		assertTrue(f_tournamentModel.contains("tourn1"));
		assertTrue(f_tournamentModel.contains("tourn2"));
		assertTrue(f_tournamentModel.contains("tourn3"));
	}

	@Test
	public void testRemModelDataListOfRankTournament() {
		// given
		testAddModelDataListOfRankTournament();
		RankTournament l_tournament = new RankTournament();
		l_tournament.setCode("tourn2");
		l_tournament.setId(2);
		// when
		f_tournamentModel.remModelData(Arrays.asList(l_tournament));
		// then
		assertTrue(f_tournamentModel.contains("tourn1"));
		assertFalse(f_tournamentModel.contains("tourn2"));
		assertTrue(f_tournamentModel.contains("tourn3"));
	}

	@Test
	public void testContains() {
		// given
		testAddModelDataListOfRankTournament();
		// when
		// then
		assertTrue(f_tournamentModel.contains("tourn1"));
		assertFalse(f_tournamentModel.contains("tourn5"));
	}

}
