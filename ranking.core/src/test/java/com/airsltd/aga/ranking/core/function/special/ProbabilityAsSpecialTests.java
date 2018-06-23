package com.airsltd.aga.ranking.core.function.special;

import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.airsltd.aga.ranking.core.data.Color;
import com.airsltd.aga.ranking.core.data.RankGame;
import com.airsltd.aga.ranking.core.data.RankGameExt;
import com.airsltd.aga.ranking.core.data.RankPlayer;
import com.airsltd.aga.ranking.core.data.RankTournament;
import com.airsltd.aga.ranking.core.model.GameExtModel;
import com.airsltd.aga.ranking.core.model.PlayerModel;
import com.airsltd.aga.ranking.core.model.TournamentModel;
import com.airsltd.aga.ranking.core.states.GameResult;
import com.airsltd.core.model.BlockModel;

public class ProbabilityAsSpecialTests {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		PlayerModel.setInstance(mock(PlayerModel.class));
		TournamentModel.setInstance(mock(TournamentModel.class));
		BlockModel.setFromIdModel(RankPlayer.class, PlayerModel.getInstance());
		BlockModel.setFromIdModel(RankTournament.class, TournamentModel.getInstance());
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

	public List<Integer> calculateNeededRating() {
		double l_float = -20f;
		List<Integer> l_retVal = new ArrayList<>();
		new GameExtModel();
		while (l_float < 10) {
			RankGame m_rankGame[] = new RankGame[10];
			RankGameExt m_rankGameExt[] = new RankGameExt[10];
			m_rankGame[0] = new RankGame(32, 0, 6, Color.WHITE,0);
			m_rankGame[1] = new RankGame(33, 0, 6, Color.BLACK,0);
			m_rankGame[2] = new RankGame(34, 0, 6, Color.WHITE,0);
			m_rankGame[3] = new RankGame(35, 0, 6, Color.BLACK,0);
			m_rankGame[4] = new RankGame(36, 0, 6, Color.WHITE,0);
			m_rankGame[5] = new RankGame(37, 0, 6, Color.BLACK,0);
			m_rankGame[6] = new RankGame(38, 0, 6, Color.BLACK,0);
			MovingAverageValue<RankGame> l_games = new MovingAverageValue<RankGame>(7);
			Map<RankGame, RankGameExt> l_map = GameExtModel.getInstance().getData();
			for (int setupCnt = 0; setupCnt!=7; setupCnt++) {
				m_rankGame[setupCnt].setResult(setupCnt>=6?GameResult.PLAYERLOST:GameResult.PLAYERWON);
				m_rankGameExt[setupCnt] = new RankGameExt(m_rankGame[setupCnt]);
				m_rankGameExt[setupCnt].setOpponentsTrailingRating(l_float);
				l_map.put(m_rankGame[setupCnt], m_rankGameExt[setupCnt]);
				l_games.addValue(m_rankGame[setupCnt]);
			}
			// when
			int l_retRank = f_probabilityAs.evaluate(l_games);
			l_retVal.add(l_retRank);
			l_float += .01;
		}
		return l_retVal;
	}
	
	@Test
	public void testCheckRatingsNeeded() {
		// given
		// when
		List<Integer> l_ranks = calculateNeededRating();
		// then
		double l_float = -20;
		for (Integer l_rank : l_ranks) {
//			System.out.format("%f -> %d\n", l_float, l_rank);
			l_float += .01;
		}
	}
}
