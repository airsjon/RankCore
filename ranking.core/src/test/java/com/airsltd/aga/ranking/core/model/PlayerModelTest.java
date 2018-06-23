package com.airsltd.aga.ranking.core.model;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.airsltd.aga.ranking.core.data.RankPlayer;

public class PlayerModelTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	private PlayerModel f_model;

	@Before
	public void setUp() throws Exception {
		f_model = new PlayerModel();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetSelectionQueryObject() {
		// given
		// when
		// then
		assertEquals("Select * From players", f_model.getSelectionQuery(""));
	}

	@Test
	public void testAddModelDataListOfRankPlayer() {
		// given
		RankPlayer l_player1 = new RankPlayer();
		RankPlayer l_player2 = new RankPlayer();
		l_player1.setPin_Player(523);
		l_player2.setPin_Player(1022);
		List<RankPlayer> l_players = Arrays.asList(l_player1, l_player2);
		// when
		f_model.addModelData(l_players);
		// then
		assertNotNull(f_model.getElement(523));
		assertNotNull(f_model.getElement(1022));
		assertNull(f_model.getElement(524));
	}

	@Test
	public void testRemModelDataListOfRankPlayer() {
		// given
		testAddModelDataListOfRankPlayer();
		// when
		f_model.remModelData(Arrays.asList(f_model.getElement(1022)));
		// then
		assertNotNull(f_model.getElement(523));
		assertNull(f_model.getElement(1022));
	}

	@Test
	public void testContains() {
		// given
		// when
		testAddModelDataListOfRankPlayer();
		// then
	}

}
