package com.airsltd.aga.ranking.core.model;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.airsltd.aga.ranking.core.data.RankPlayer;

public class LiveRankObtainedModelTest {

	private static final String COMPLETESELECT = "Select * FROM liverankobtained order by Player_ID, RunDate";
	private static final Object PLAYERSELECT = "Select * FROM liverankobtained Where Player_ID=356 order by Player_ID, RunDate";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	private LiveRankObtainedModel f_rankObtainedModel;

	@Before
	public void setUp() throws Exception {
		LiveRankObtainedModel.setInstance(null);
		f_rankObtainedModel = LiveRankObtainedModel.getInstance();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetSelectionQueryRankPlayer() {
		// given
		RankPlayer l_player = new RankPlayer(356);
		// when
		LiveRankObtainedModel.getInstance().setCompleteLoad(false);
		// then
		assertEquals(COMPLETESELECT, f_rankObtainedModel.getSelectionQuery(null));
		assertEquals(PLAYERSELECT, f_rankObtainedModel.getSelectionQuery(l_player));
		// when
		LiveRankObtainedModel.getInstance().setCompleteLoad(true);
		// then
		assertEquals(COMPLETESELECT, f_rankObtainedModel.getSelectionQuery(null));
		assertEquals(COMPLETESELECT, f_rankObtainedModel.getSelectionQuery(l_player));

		
	}

}
