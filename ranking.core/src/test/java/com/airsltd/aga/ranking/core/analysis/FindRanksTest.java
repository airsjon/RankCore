package com.airsltd.aga.ranking.core.analysis;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.airsltd.aga.ranking.core.model.PlayerModel;
import com.airsltd.core.ICoreInterface;
import com.airsltd.core.data.CoreInterface;

public class FindRanksTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	private ArrayList<RankGameInput> f_games;
	private FindRanks f_findRanks;
	private float[] f_ratings = new float[] { 3.2f, 3.6f, 3.4f, 3.8f };
	private boolean[] f_color = new boolean[] { false, true, false, true };
	private int[] f_handicap = new int[] { 0, 0, 0, 0 };
	private int[] f_komi = new int[] { 7, 7, 7, 7};
	private Result[] f_result = new Result[] { Result.WIN, Result.LOSS, Result.WIN, Result. WIN};

	@Before
	public void setUp() throws Exception {
		// given
		CoreInterface.setSystem(mock(ICoreInterface.class));
		PlayerModel.getInstance();
		f_games = new ArrayList<RankGameInput>();
		for (int cnt = 0; cnt != 4; cnt++) {
			addGame(cnt);
		}
		f_findRanks = new FindRanks();
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
	public void testEvaluate() {
		// given
		// when
		List<RankResult> l_values = f_findRanks.evaluate(f_games, 2);
		// then
		for (RankResult l_result : l_values) {
//			System.out.println(l_result);
		}
	}

	@Test
	public void testGetPercentDone() {
		// given
		// when
		assertEquals(0, f_findRanks.getPercentDone());
		// then
	}
}
