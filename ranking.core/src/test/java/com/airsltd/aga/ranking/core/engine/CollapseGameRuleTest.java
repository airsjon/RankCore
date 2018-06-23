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
import com.airsltd.aga.ranking.core.model.GameModel;
import com.airsltd.aga.ranking.core.states.GameResult;

public class CollapseGameRuleTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	private CollapseGameRule f_collapseRule;

	@Before
	public void setUp() throws Exception {
		f_collapseRule = new CollapseGameRule();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testProcessGame() {
		// given
		RankGame l_game = new RankGame(42, 3, 0, Color.BLACK, 567);
		// when
		f_collapseRule.processGame(l_game);
	}
	
	@Test
	public void testCollapseSimilar() throws Exception {
		//given
		RankGame l_game = new RankGame(42, 3, 0, Color.BLACK, 567);
		l_game.setResult(GameResult.PLAYERLOST);
		l_game.setRound(1);
		RankGame l_game1 = new RankGame(43, 3, 0, Color.BLACK, 568);
		l_game1.setResult(GameResult.PLAYERLOST);
		l_game1.setRound(2);
		RankGame l_game2 = new RankGame(44, 3, 0, Color.BLACK, 569);
		l_game2.setResult(GameResult.PLAYERLOST);
		l_game2.setRound(3);
		RankGame l_game3 = new RankGame(45, 3, 0, Color.BLACK, 570);
		l_game3.setResult(GameResult.PLAYERFORFEIT);
		l_game3.setRound(4);
		GameModel l_model = mock(GameModel.class);
		List<RankGame> l_similar = Arrays.asList(l_game, l_game1, l_game2, l_game3);
		// when
		f_collapseRule.collapseSimilar(l_game, l_model, l_similar);
		// then
		testGameCollapsed(l_game, l_model);
		testGameCollapsed(l_game1, l_model);
		testGameCollapsed(l_game2, l_model);
		testGameCollapsed(l_game3, l_model);
		verify(l_model).addContent(any(RankGame.class));
	}

	@Test
	public void testCollapseSimilarEven() throws Exception {
		//given
		RankGame l_game = new RankGame(42, 3, 0, Color.BLACK, 567);
		l_game.setResult(GameResult.PLAYERLOST);
		l_game.setRound(1);
		RankGame l_game1 = new RankGame(43, 3, 0, Color.BLACK, 568);
		l_game1.setResult(GameResult.PLAYERWON);
		l_game1.setRound(2);
		RankGame l_game2 = new RankGame(44, 3, 0, Color.BLACK, 569);
		l_game2.setResult(GameResult.BOTHFORFEIT);
		l_game2.setRound(3);
		RankGame l_game3 = new RankGame(45, 3, 0, Color.BLACK, 570);
		l_game3.setResult(GameResult.PLAYERFORFEIT);
		l_game3.setRound(4);
		GameModel l_model = mock(GameModel.class);
		List<RankGame> l_similar = Arrays.asList(l_game, l_game1, l_game2, l_game3);
		// when
		f_collapseRule.collapseSimilar(l_game, l_model, l_similar);
		// then
		testGameCollapsed(l_game, l_model);
		testGameCollapsed(l_game1, l_model);
		testGameCollapsed(l_game2, l_model);
		testGameCollapsed(l_game3, l_model);
		verify(l_model, never()).addContent(any(RankGame.class));
	}

	private void testGameCollapsed(RankGame p_game, GameModel p_model) {
		verify(p_model).updateContent(eq(p_game), eq(p_game));
		assertEquals(GameState.COLLAPSED, p_game.getState());
		p_game.setState(GameState.UNKNOWN);
	}

}
