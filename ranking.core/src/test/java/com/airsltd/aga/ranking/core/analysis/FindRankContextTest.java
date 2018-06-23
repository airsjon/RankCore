package com.airsltd.aga.ranking.core.analysis;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.airsltd.aga.ranking.core.data.Color;
import com.airsltd.aga.ranking.core.data.RankGame;
import com.airsltd.aga.ranking.core.function.special.MovingAverageValue;
import com.airsltd.aga.ranking.core.model.GameExtModel;
import com.airsltd.aga.ranking.core.model.PlayerModel;
import com.airsltd.aga.ranking.core.states.GameResult;
import com.airsltd.core.ICoreInterface;
import com.airsltd.core.data.CoreInterface;

public class FindRankContextTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	private ArrayList<RankGameInput> f_games;
	private FindRankContext f_findRankContextWins;
	private float[] f_ratings = new float[] { 3.2f, 3.6f, 3.4f, 3.8f };
	private boolean[] f_color = new boolean[] { false, true, false, true };
	private int[] f_handicap = new int[] { 0, 0, 0, 0 };
	private int[] f_komi = new int[] { 7, 7, 7, 7};
	private Result[] f_result = new Result[] { Result.WIN, Result.LOSS, Result.WIN, Result. WIN};
	private FindRankContext f_findRankContextLosses;

	@Before
	public void setUp() throws Exception {
		// given
		CoreInterface.setSystem(mock(ICoreInterface.class));
		PlayerModel.getInstance();
		new GameExtModel();
		f_games = new ArrayList<RankGameInput>();
		for (int cnt = 0; cnt != 4; cnt++) {
			addGame(cnt);
		}
		f_findRankContextWins = new FindRankContext(f_games, 2);
		f_findRankContextLosses = new FindRankContext(f_games, 0);
	}

	private void addGame(int p_cnt) {
		RankGameInput l_game = new RankGameInput(p_cnt,f_ratings[p_cnt], f_color[p_cnt],
				f_handicap[p_cnt], f_komi[p_cnt], f_result[p_cnt]);
		f_games.add(l_game);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testProcess() {
		// given
		// when
		for (float l_gameOneRating = -1f; l_gameOneRating  < 10f; l_gameOneRating += .1f) {
			f_findRankContextWins.clearCurrent();;
			for (float l_gameTwoRating = l_gameOneRating; l_gameTwoRating < 10f; l_gameTwoRating += .1f) {
				f_findRankContextWins.process(l_gameOneRating, l_gameTwoRating);
			}
		}
		// then
		List<RankResult> l_results = f_findRankContextWins.getResults();
		for (RankResult l_result : l_results) {
			// TODO
		}
	}

	@Test
	public void testGetResults() {
		// given
		// when
		// then
		assertTrue(f_findRankContextWins.getResults().isEmpty());
	}

	@Test
	public void testSetResults() {
		// given
		// when
		f_findRankContextWins.setResults(null);
		// then
		assertNull(f_findRankContextWins.getResults());
	}
	
	@Test
	public void testClearCurrent() {
		// given
		// when
		f_findRankContextWins.clearCurrent();
		// then
	}
	
	@Test
	public void testGenerateGames() {
		// given
		Color[] l_expectedColors = new Color[] { 
				Color.BLACK, Color.WHITE, Color.BLACK, Color.WHITE, Color.BLACK, Color.WHITE };
		GameResult[] l_expectedResults = new GameResult[] {
				GameResult.PLAYERWON, GameResult.PLAYERLOST,
				GameResult.PLAYERWON, GameResult.PLAYERWON,
				GameResult.PLAYERWON, GameResult.PLAYERWON	};
		float[] l_expectedRatings = new float[] {
				3.2f, 3.6f, 3.4f, 3.8f, 0f, 0f };
		// when
		f_findRankContextWins.generateGames(f_games);
		// then
		MovingAverageValue<RankGame> l_gameAverage = f_findRankContextWins.getGameAverage();
		int l_index = 0;
		for (RankGame l_currentGame : l_gameAverage.getSeries()) {
			assertEquals(l_expectedColors[l_index], l_currentGame.getColor());
			assertEquals("@"+l_index, l_expectedResults[l_index], l_currentGame.getResult());
			l_index++;
		}
		l_index = 0;
	}
	
	@Test
	public void testSetGameAverageMovingAverageValue() {
		// given
		// when
		f_findRankContextLosses.setGameAverage(null);
		// then
		assertNull(f_findRankContextLosses.getGameAverage());
	}

}
