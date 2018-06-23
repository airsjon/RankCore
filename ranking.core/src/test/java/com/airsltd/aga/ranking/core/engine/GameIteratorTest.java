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
import com.airsltd.aga.ranking.core.data.GoRank;
import com.airsltd.aga.ranking.core.data.RankGame;
import com.airsltd.aga.ranking.core.data.RankPlayer;
import com.airsltd.aga.ranking.core.model.GameModel;
import com.airsltd.aga.ranking.core.states.GameResult;

public class GameIteratorTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	private GameIterator f_gameIterator;
	private GameIterator f_gameReanalyzeIterator;

	@Before
	public void setUp() throws Exception {
		RankGame m_game1 = new RankGame(10, 0, 7, Color.BLACK, 512);
		RankGame m_game2 = new RankGame(11, 0, 7, Color.WHITE, 512);
		RankGame m_game3 = new RankGame(12, 0, 7, Color.BLACK, 513);
		RankGame m_game4 = new RankGame(13, 0, 7, Color.BLACK, 514);
		RankGame m_game5 = new RankGame(14, 0, 7, Color.WHITE, 514);
		m_game1.setState(GameState.UNKNOWN);
		m_game2.setState(GameState.UNKNOWN);
		m_game3.setState(GameState.PROCESSED);
		m_game4.setState(GameState.REANALYZE);
		m_game5.setState(GameState.UNKNOWN);
		RankPlayer m_player1 = new RankPlayer();
		m_player1.setId(2802);
		RankPlayer m_player2 = new RankPlayer();
		m_player2.setId(4);
		RankPlayer m_player3 = new RankPlayer();
		m_player3.setId(155);
		m_game1.setPlayer(m_player1);
		m_game1.setRank(GoRank.getInstance(53));
		m_game1.setOpponentRank(GoRank.getInstance(54));
		m_game1.setResult(GameResult.PLAYERWON);
		m_game2.setPlayer(m_player2);
		m_game2.setRank(GoRank.getInstance(54));
		m_game2.setOpponentRank(GoRank.getInstance(53));
		m_game2.setResult(GameResult.PLAYERLOST);
		m_game3.setPlayer(m_player3);
		m_game3.setRank(GoRank.getInstance(45));
		m_game3.setOpponentRank(GoRank.getInstance(54));
		m_game3.setResult(GameResult.PLAYERWON);
		m_game4.setPlayer(m_player1);
		m_game4.setRank(GoRank.getInstance(53));
		m_game4.setOpponentRank(GoRank.getInstance(54));
		m_game4.setResult(GameResult.PLAYERWON);
		m_game5.setPlayer(m_player2);
		m_game5.setRank(GoRank.getInstance(54));
		m_game5.setOpponentRank(GoRank.getInstance(55));
		m_game5.setResult(GameResult.PLAYERLOST);
		List<RankGame> l_gameList = Arrays.asList(m_game1, m_game2, m_game3, m_game4, m_game5);
		GameModel.setInstance(mock(GameModel.class));
		given(GameModel.getInstance().getContentAsList()).willReturn(l_gameList);
		f_gameIterator = new GameIterator(false);
		f_gameReanalyzeIterator = new GameIterator(true);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSetupDeque() {
		// given
		// when
		// note: setupDeque called by new GameIterator in setup
		List<RankGame> l_games = GameModel.getInstance().getContentAsList();
		// then
		RankGame l_game = f_gameIterator.next();
		assertEquals(l_games.get(0), l_game);
		l_game = f_gameIterator.next();
		assertEquals(l_games.get(1), l_game);
		l_game = f_gameIterator.next();
		assertEquals(l_games.get(4), l_game);
		assertFalse(f_gameIterator.hasNext());
		l_game = f_gameReanalyzeIterator.next();
		assertEquals(l_games.get(3), l_game);
		assertFalse(f_gameReanalyzeIterator.hasNext());
	}

	@Test
	public void testRemove() {
		// given
		List<RankGame> l_games = GameModel.getInstance().getContentAsList();
		// when
		f_gameIterator.remove();
		// then
		RankGame l_game = f_gameIterator.next();
		assertEquals(l_games.get(1), l_game);
		
	}

	@Test
	public void testClose() throws Exception {
		// given
		GameModel.setInstance(mock(GameModel.class));
		// when
		f_gameIterator.close();
		// then
		verify(GameModel.getInstance()).endBlock();
		verify(GameModel.getInstance()).cancelBlock();
		
	}
	
}
