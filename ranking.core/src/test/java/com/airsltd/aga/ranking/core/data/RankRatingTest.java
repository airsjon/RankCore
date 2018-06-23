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
import com.airsltd.core.model.BlockModel;

public class RankRatingTest {


	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		PlayerModel.setInstance(mock(PlayerModel.class));
		BlockModel.setFromIdModel(RankPlayer.class, PlayerModel.getInstance());
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	private RankRating f_rankRating;

	@Before
	public void setUp() throws Exception {
		f_rankRating = new RankRating();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testTableName() {
		// given
		// when
		// then
		assertEquals("ratings", f_rankRating.tableName());
	}

	@Test
	public void testKeyFields() {
		// given
		// when
		// then
		assertEquals(3, f_rankRating.keyFields());
	}

	@Test
	public void testAutoIncrementField() {
		// given
		// when
		// then
		// code coverage
		f_rankRating.autoIncrementField(0, 0);
	}

	@Test
	public void testCopy() {
		// given
		// when
		IBlockData l_rating = f_rankRating.copy();
		// then
		assertEquals(f_rankRating, l_rating);
	}

	@Test
	public void testToStringCsv() {
		// given
		String [] l_inString = new String[] {
				"32", "2014-04-15", "3.2453", ".2342"
		};
		String [] l_outString = new String[] {
				"32", "'2014-04-15'", "3.2453", "0.2342"
		};
		RankPlayer m_player = mock(RankPlayer.class);
		given(PlayerModel.getInstance().getElement(32l)).willReturn(m_player);
		given(m_player.getPersistentID()).willReturn(32l);
		// when
		f_rankRating.fromStringCsv(l_inString);
		// then
		assertArrayEquals(l_outString, f_rankRating.toStringCsv());
		
	}

	@Test
	public final void testToSegment() {
		// given
		// when
		// then
		assertNull(f_rankRating.toSegment());
	}
	
	@Test
	public final void testToFixedRating() {
		// given
		// when
		f_rankRating.setRating(3.2f);
		// then
		assertEquals(3.2f, f_rankRating.toFixedRating(), .0001f);
		// when
		f_rankRating.setRating(-1.2f);
		// then
		assertEquals(.8f, f_rankRating.toFixedRating(), .0001f);
	}
}
