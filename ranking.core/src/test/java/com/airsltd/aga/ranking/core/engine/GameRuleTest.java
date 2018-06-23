package com.airsltd.aga.ranking.core.engine;

import static org.mockito.BDDMockito.*;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.airsltd.aga.ranking.core.data.Color;
import com.airsltd.aga.ranking.core.data.GameState;
import com.airsltd.aga.ranking.core.data.RankGame;
import com.airsltd.aga.ranking.core.model.GameModel;

public class GameRuleTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		GameModel.setInstance(mock(GameModel.class));
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	private GameRule f_gameRule;

	@Before
	public void setUp() throws Exception {
		f_gameRule = new GameRule(3, "mockTest") {
			
			@Override
			protected GameState processGame(RankGame p_game) {
				GameState l_retVal = GameState.VALID;
				switch ((int)p_game.getId()) {
				case 512:
					l_retVal = GameState.REJECTED;
					break;
				case 513:
					l_retVal = GameState.UNKNOWN;
					break;
				}
				return l_retVal;
			}
		};
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testProcess() {
		RankGame l_gameOne = new RankGame(511, 0, 7, Color.WHITE, 56);
		RankGame l_gameTwo = new RankGame(513, 0, 7, Color.WHITE, 56);
		RankGame l_gameThree = new RankGame(512, 0, 7, Color.WHITE, 56);
		// given
		// when
		assertFalse(f_gameRule.process(l_gameTwo));
		assertTrue(f_gameRule.process(l_gameOne));
		assertTrue(f_gameRule.process(l_gameThree));
		// then
		assertEquals(1, GameState.VALID.getCount());
		assertEquals(1, GameState.REJECTED.getCount());
	}

}
