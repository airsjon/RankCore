package com.airsltd.aga.ranking.core.engine;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.sql.Date;
import java.util.Arrays;
import java.util.HashSet;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.airsltd.aga.ranking.core.data.Color;
import com.airsltd.aga.ranking.core.data.GameState;
import com.airsltd.aga.ranking.core.data.RankGame;
import com.airsltd.aga.ranking.core.model.GameModel;

public class GameEngineTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	private GameEngine f_standardGame;

	@Before
	public void setUp() throws Exception {
		f_standardGame = new GameEngine(Date.valueOf(LocalDate.of(2010, 1,1))
				, Date.valueOf(LocalDate.of(2017, 12, 1)));
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testProcess() {
		// given
		GameModel.setInstance(mock(GameModel.class));
		RankGame l_game1 = new RankGame(10, 0, 7, Color.WHITE, 5);
		l_game1.setState(GameState.UNKNOWN);
		RankGame l_game2 = new RankGame(20, 0, 7, Color.WHITE, 6);
		l_game2.setState(GameState.PROCESSED);
		RankGame l_game3 = new RankGame(30, 0, 7, Color.WHITE, 7);
		l_game3.setState(GameState.REANALYZE);
		given(GameModel.getInstance().getContentAsList()).willReturn(new HashSet<>(Arrays.asList(l_game1, l_game2, l_game3)));
		// when
		// then
		f_standardGame.process();
	}

	@Test
	public void testLoadRules() {
		// given
		// when
		// then
		assertEquals(7, f_standardGame.getRules().size());
	}
	
	@Test
	public void testProcessBoolean() {
		// given
		GameModel.setInstance(mock(GameModel.class));
		// when
		// then
		f_standardGame.process(false);
	}

}
