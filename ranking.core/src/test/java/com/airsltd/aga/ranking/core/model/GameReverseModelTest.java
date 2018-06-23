package com.airsltd.aga.ranking.core.model;


import static org.mockito.BDDMockito.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.airsltd.aga.ranking.core.data.RankGameLink;
import com.airsltd.core.data.BlockProvider;

public class GameReverseModelTest {

	GameReverseModel f_reverseModel;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		GameReverseModel.setInstance(null);
		f_reverseModel = GameReverseModel.getInstance();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testLoadModelObject() {
		@SuppressWarnings("unchecked")
		BlockProvider<RankGameLink> l_mockProvider = mock(BlockProvider.class);
		// given
		f_reverseModel.setBlockProvider(l_mockProvider);
		// when
		// then
		f_reverseModel.loadModel(null);
		verify(l_mockProvider).loadDataBase("Select * From gamelink", null);
		f_reverseModel.loadModel(null);
		verify(l_mockProvider,times(1)).loadDataBase("Select * From gamelink", null);
	}

}
