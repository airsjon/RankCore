package com.airsltd.aga.ranking.core.engine;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.airsltd.aga.ranking.core.data.Color;
import com.airsltd.aga.ranking.core.data.GameState;
import com.airsltd.aga.ranking.core.data.RankGame;
import com.airsltd.aga.ranking.core.data.RankPlayer;
import com.airsltd.aga.ranking.core.model.GameModel;

public class OutOfOrderRuleTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	private OutOfOrderRule f_rule;

	@Before
	public void setUp() throws Exception {
		f_rule = new OutOfOrderRule();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testProcessGame() {
		// given
		GameModel.setInstance(mock(GameModel.class));
		RankGame l_game1 = new RankGame(52l, 0, 7, Color.WHITE, 52334);
		RankGame l_game2 = new RankGame(59l, 0, 7, Color.WHITE, 90000);
		RankGame l_game3 = new RankGame(67l, 0, 7, Color.BLACK, 898983);
		RankGame l_game4 = new RankGame(102l, 0, 7, Color.WHITE, 1000000);
		l_game1.setState(GameState.PROCESSED);
		l_game3.setState(GameState.PROCESSED);
		List<RankGame> l_games = Arrays.asList(l_game1, l_game2, l_game3, l_game4);
		given(GameModel.getInstance().getContentAsList(any(RankPlayer.class))).willReturn(l_games);
		// when
		// then
		assertEquals(GameState.OUTOFORDER, f_rule.processGame(l_game2));
		// when
		// then
		assertEquals(GameState.UNKNOWN, f_rule.processGame(l_game4));
		
	}

}
