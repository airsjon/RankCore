package com.airsltd.aga.ranking.core.data;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.airsltd.aga.ranking.core.model.PlayerModel;
import com.airsltd.core.data.IBlockData;

public class RankPlayerTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	private RankPlayer f_rankPlayer;

	@Before
	public void setUp() throws Exception {
		f_rankPlayer = new RankPlayer();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testTableName() {
		// given
		// when
		// then
		assertEquals("players", f_rankPlayer.tableName());
	}

	@Test
	public void testKeyFields() {
		// given
		// when
		// then
		assertEquals(1, f_rankPlayer.keyFields());
	}

	@Test
	public void testAutoIncrementField() {
		// given
		// when
		f_rankPlayer.autoIncrementField(1, 32L);
		// then
		assertEquals(-1, f_rankPlayer.getId());
		// when
		f_rankPlayer.autoIncrementField(0, 33L);
		// then
		assertEquals(33L, f_rankPlayer.getId());
		
	}

	@Test
	public void testGetFieldNames() {
		// given
		// when
		// then
		assertArrayEquals(new String[] { "Player_ID", "dob" }, f_rankPlayer.getFieldNames());
	}

	@Test
	public void testCopyIBlockData() {
		// given
		RankPlayer l_rankPlayer = new RankPlayer();
		l_rankPlayer.setId(32l);
		// when
		// cc - nothing happens
		f_rankPlayer.copy(mock(IBlockData.class));
		// then
		// when
		f_rankPlayer.copy(l_rankPlayer);
		// then
		assertEquals(32l, f_rankPlayer.getId());
	}

	@Test
	public void testCopy() {
		// given
		// when
		f_rankPlayer.setId(32l);
		IBlockData l_rankPlayer = f_rankPlayer.copy();
		// then
		assertTrue(l_rankPlayer instanceof RankPlayer);
		assertEquals(32l, ((RankPlayer) l_rankPlayer).getId());
	}

	@Test
	public void testPrimaryKeyFields() {
		// given
		// when
		// then
		assertEquals(1L, f_rankPlayer.primaryKeyFields());
	}

	@Test
	public void testGetPersistentID() {
		// given
		PlayerModel.setInstance(mock(PlayerModel.class));
		// when
		// then
		assertEquals(-1, f_rankPlayer.getPersistentID());
		assertEquals(-1, f_rankPlayer.getPersistentID());
		// when
		f_rankPlayer.setId(3L);
		// then
		assertEquals(3, f_rankPlayer.getPersistentID());
	}

	@Test
	public void testPlayedGames() {
		// given 
		// when
		// then
		assertFalse(f_rankPlayer.playedGames());
		// when
		f_rankPlayer.playedGame();
		// then
		assertTrue(f_rankPlayer.playedGames());
	}
	
}
