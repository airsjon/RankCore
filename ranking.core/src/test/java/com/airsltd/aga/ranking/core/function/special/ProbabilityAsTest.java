package com.airsltd.aga.ranking.core.function.special;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

import java.util.Arrays;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.airsltd.aga.ranking.core.data.Color;
import com.airsltd.aga.ranking.core.data.RankGame;
import com.airsltd.aga.ranking.core.data.RankGameExt;
import com.airsltd.aga.ranking.core.model.GameExtModel;
import com.airsltd.aga.ranking.core.model.PlayerModel;
import com.airsltd.aga.ranking.core.model.TournamentModel;
import com.airsltd.aga.ranking.core.states.GameResult;

public class ProbabilityAsTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		PlayerModel.setInstance(mock(PlayerModel.class));
		TournamentModel.setInstance(mock(TournamentModel.class));
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	private ProbabilityAs f_probabilityAs;

	@Before
	public void setUp() throws Exception {
		f_probabilityAs = new ProbabilityAs();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testEvaluate() {
		// given
		new GameExtModel();
		Map<RankGame, RankGameExt> l_map = GameExtModel.getInstance().getData();
		RankGame m_rankGame[] = new RankGame[10];
		RankGameExt m_rankGameExt[] = new RankGameExt[10];
		double[] l_ranks = new double[] { 3.2f, 3.7f, 3.5f, 3.2f, 2.9f, 3.7f, 3.8f, 4.1f, 5.2f};
		m_rankGame[0] = new RankGame(32, 0, 6, Color.WHITE,67);
		m_rankGame[1] = new RankGame(33, 0, 6, Color.WHITE,68);
		m_rankGame[2] = new RankGame(34, 0, 6, Color.BLACK,69);
		m_rankGame[3] = new RankGame(35, 0, 6, Color.WHITE,70);
		m_rankGame[4] = new RankGame(36, 0, 6, Color.WHITE,71);
		m_rankGame[5] = new RankGame(37, 0, 6, Color.WHITE,72);
		m_rankGame[6] = new RankGame(38, 0, 6, Color.BLACK,73);
		m_rankGame[7] = new RankGame(39, 0, 6, Color.WHITE,74);
		m_rankGame[8] = new RankGame(40, 0, 6, Color.WHITE,75);
		m_rankGame[9] = new RankGame(41, 0, 6, Color.WHITE,76);
		MovingAverageValue<RankGame> l_games = new MovingAverageValue<RankGame>(6);
		for (int setupCnt = 0; setupCnt!=6; setupCnt++) {
			m_rankGameExt[setupCnt] = new RankGameExt(m_rankGame[setupCnt]);
			l_map.put(m_rankGame[setupCnt], m_rankGameExt[setupCnt]);
			m_rankGame[setupCnt].setResult(GameResult.PLAYERWON);
			m_rankGameExt[setupCnt].setOpponentsTrailingRating(l_ranks[setupCnt]);
			l_games.addValue(m_rankGame[setupCnt]);
		}
		m_rankGame[5].setResult(GameResult.PLAYERLOST);
		// when
		int l_retVal = f_probabilityAs.evaluate(l_games);
		assertEquals(379, l_retVal);
		// then
		// when
		
		// given
		new GameExtModel();
		l_map = GameExtModel.getInstance().getData();
		RankGame[] m_gameRecords = new RankGame[] {
				new RankGame(32,0,7,Color.WHITE,100),
				new RankGame(33,0,7,Color.BLACK,101),
				new RankGame(34,0,7,Color.WHITE,102),
				new RankGame(35,0,7,Color.BLACK,103),
				new RankGame(36,0,7,Color.BLACK,104),
				new RankGame(37,0,7,Color.WHITE,105),

				new RankGame(42,0,7,Color.WHITE,106),
				new RankGame(43,0,7,Color.BLACK,107),
				new RankGame(44,0,7,Color.WHITE,108),
				new RankGame(45,0,7,Color.BLACK,109),
				new RankGame(46,0,7,Color.BLACK,110),
				new RankGame(47,0,7,Color.WHITE,111),

				new RankGame(52,0,7,Color.WHITE,112),
				new RankGame(53,0,7,Color.BLACK,113),
				new RankGame(54,0,7,Color.WHITE,114),
				new RankGame(55,0,7,Color.BLACK,115),
				new RankGame(56,0,7,Color.BLACK,116),
				new RankGame(57,0,7,Color.WHITE,117),

				new RankGame(62,0,7,Color.WHITE,118),
				new RankGame(63,0,7,Color.BLACK,119),
				new RankGame(64,0,7,Color.WHITE,120),
				new RankGame(65,0,7,Color.BLACK,121),
				new RankGame(66,0,7,Color.BLACK,122),
				new RankGame(67,0,7,Color.WHITE,123),
		};
		RankGameExt m_gameExtRecords[] = new RankGameExt[m_gameRecords.length];
		for (int cnt = 0; cnt != m_gameRecords.length; cnt++) {
			m_gameExtRecords[cnt] = new RankGameExt(m_gameRecords[cnt]);
			l_map.put(m_gameRecords[cnt], m_gameExtRecords[cnt]);
		}
		boolean[] l_gameResults = new boolean[] {
				true, false, true, true, false, false,
				false, false, false, true, true, false,
				false, true, true, true, false, false,
				true, true, true, true, false, true
		};
		double[] l_gamePlayedAgainst = new double[] {
				2.1f, 2.3f, 2.7f, 2.5f, 2.4f, 2.3f,
				2.1f, 2.3f, 2.7f, 2.5f, 2.4f, 2.3f,
				2.1f, 2.3f, 2.7f, 2.5f, 2.4f, 2.3f,
				2.1f, 2.3f, 2.7f, 2.5f, 3.9f, 2.3f,
		};
		int l_index = 0;
		for (RankGame l_game : m_gameRecords) {
			l_game.setResult(l_gameResults[l_index++]?GameResult.PLAYERWON:GameResult.PLAYERLOST);
		}
		//			int[] l_probResult = new int[] {
		//					-20, -20, -20, -20, -20, 170,
		//					121, 121, 39, 39, 116, 116,
		//					116, 167, 215, 215, 170, 170,
		//					215, 215, 215, 215, 238, 306
		//			};
		int[] l_probResult = new int[] {
				-2000, -2000, -2000, -2000, -2000, 169, 
				120, 120, 39, 39, 115, 115,
				115, 167, 214, 214, 169, 169,
				215, 215, 215, 215, 237, 305
		};
		// when
		ProbabilityAs l_pa = new ProbabilityAs();
		MovingAverageValue<RankGame> l_gamesAverage = new MovingAverageValue<RankGame>(6);
		// then
		int l_retVals[] = new int[24];
		for (int cnt = 0; cnt != 24; cnt++) {
			l_gamesAverage.addValue(m_gameRecords[cnt]);
			m_gameExtRecords[cnt].setOpponentsTrailingRating(l_gamePlayedAgainst[cnt]);
			l_retVals[cnt] = l_pa.evaluate(l_gamesAverage);
		}
		System.out.print(Arrays.toString(l_retVals));
		assertArrayEquals(l_probResult, l_retVals);

	}

	@Test
	public void testEvaluate2() {
		// given
		RankGame[] m_gameRecords = new RankGame[] {
				new RankGame(57,0,7,Color.WHITE,101),
				new RankGame(62,0,7,Color.WHITE,102),
				new RankGame(63,0,7,Color.BLACK,103),
				new RankGame(64,0,7,Color.WHITE,104),
				new RankGame(65,0,7,Color.BLACK,105),
				new RankGame(66,0,7,Color.BLACK,106),
		};
		RankGameExt[] m_gameExtRecords = setupExtGame(m_gameRecords);
		boolean[] l_gameResults = new boolean[] {
				false, true, true, true, true, false
		};
		float[] l_gamePlayedAgainst = new float[] {
				2.3f, 2.1f, 2.3f, 2.7f, 2.5f, 3.9f
		};
		int l_index = 0;
		for (RankGame l_game : m_gameRecords) {
			l_game.setResult(l_gameResults[l_index++]?GameResult.PLAYERWON:GameResult.PLAYERLOST);
		}
		//			int[] l_probResult = new int[] {
		//					-20, -20, -20, -20, -20, 170,
		//					121, 121, 39, 39, 116, 116,
		//					116, 167, 215, 215, 170, 170,
		//					215, 215, 215, 215, 238, 306
		//			};
		int[] l_probResult = new int[] {
				-2000, -2000, -2000, -2000, -2000, 238
		};
		// when
		ProbabilityAs l_pa = new ProbabilityAs();
		MovingAverageValue<RankGame> l_gamesAverage = new MovingAverageValue<RankGame>(6);
		// then
		for (int cnt = 0; cnt != 6; cnt++) {
			l_gamesAverage.addValue(m_gameRecords[cnt]);
			m_gameExtRecords[cnt].setOpponentsTrailingRating(m_gameRecords[cnt].realRating(l_gamePlayedAgainst[cnt]));
			assertEquals("@"+cnt, l_probResult[cnt], l_pa.evaluate(l_gamesAverage));
		}

	}

	private RankGameExt[] setupExtGame(RankGame[] m_gameRecords) {
		RankGameExt[] m_gameExtRecords = new RankGameExt[m_gameRecords.length];
		GameExtModel.setInstance(mock(GameExtModel.class));
		for (int cnt = 0; cnt != m_gameRecords.length; cnt++) {
			m_gameExtRecords[cnt] = new RankGameExt(m_gameRecords[cnt]);
			given(GameExtModel.getInstance().getElement(m_gameRecords[cnt])).willReturn(m_gameExtRecords[cnt]);
		}
		return m_gameExtRecords;
	}

	@Test
	public void testDetermineOffset() {
		// given
		// when
		assertEquals(0, f_probabilityAs.determineOffset(-2.2f, 3.2f));
		// then
	}

	@Test
	public void test2015USOpenQuestion() {
		// given
		RankGame[] m_gameRecords = new RankGame[] {
				new RankGame(57,0,7,Color.BLACK,101),
				new RankGame(62,0,7,Color.BLACK,102),
				new RankGame(63,0,7,Color.WHITE,103),
				new RankGame(64,0,7,Color.BLACK,104),
				new RankGame(65,0,7,Color.WHITE,105),
				new RankGame(66,0,7,Color.BLACK,106),
				new RankGame(67,0,7,Color.WHITE,107),
				new RankGame(68,0,7,Color.BLACK,108),
				new RankGame(69,0,7,Color.BLACK,109),
				new RankGame(70,0,0,Color.BLACK,110),
		};
		RankGameExt[] m_gameExtRecords = setupExtGame(m_gameRecords);
		boolean[] l_gameResults = new boolean[] {
				true, true, true, true, true, true, true, true, true, false
		};
		double[] l_gamePlayedAgainst = new double[] {
				-9.02f, -8.5f, -7.75f, -7.29f, -8.5f, -7.03f, -9.37f, -9.93f, -8.75f, -7.50f
		};
		int l_index = 0;
		for (RankGame l_game : m_gameRecords) {
			l_game.setResult(l_gameResults[l_index++]?GameResult.PLAYERWON:GameResult.PLAYERLOST);
		}
		//			int[] l_probResult = new int[] {
		//					-20, -20, -20, -20, -20, 170,
		//					121, 121, 39, 39, 116, 116,
		//					116, 167, 215, 215, 170, 170,
		//					215, 215, 215, 215, 238, 306
		//			};
		int[] l_probResult = new int[] {
				-2000, -2000, -2000, -2000, -2000, -2000, -2000, -2000, -2000, -742
		};
		// when
		ProbabilityAs l_pa = new ProbabilityAs();
		MovingAverageValue<RankGame> l_gamesAverage = new MovingAverageValue<RankGame>(10);
		// then
		for (int cnt = 0; cnt != 10; cnt++) {
			l_gamesAverage.addValue(m_gameRecords[cnt]);
			m_gameExtRecords[cnt].setOpponentsTrailingRating(l_gamePlayedAgainst[cnt]);
			assertEquals("@"+cnt, l_probResult[cnt], l_pa.evaluate(l_gamesAverage));
		}

	}


}
