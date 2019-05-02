package com.airsltd.aga.ranking.core.engine;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.airsltd.aga.ranking.core.data.Color;
import com.airsltd.aga.ranking.core.data.GoRank;
import com.airsltd.aga.ranking.core.data.LiveRankObtained;
import com.airsltd.aga.ranking.core.data.RankGame;
import com.airsltd.aga.ranking.core.data.RankGameExt;
import com.airsltd.aga.ranking.core.data.RankPlayer;
import com.airsltd.aga.ranking.core.data.RankTournament;
import com.airsltd.aga.ranking.core.model.GameExtModel;
import com.airsltd.aga.ranking.core.model.GameModel;
import com.airsltd.aga.ranking.core.model.LiveRankObtainedModel;
import com.airsltd.aga.ranking.core.model.PlayerModel;
import com.airsltd.aga.ranking.core.model.TournamentModel;
import com.airsltd.aga.ranking.core.states.GameResult;
import com.airsltd.core.ICoreInterface;
import com.airsltd.core.data.CoreInterface;

public class EngineTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		CoreInterface.setSystem(mock(ICoreInterface.class));
		new PlayerModel();
		new TournamentModel();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	private Engine f_engine;

	@Before
	public void setUp() throws Exception {
		PlayerModel.setInstance(mock(PlayerModel.class));
		LiveRankObtainedModel.setInstance(mock(LiveRankObtainedModel.class));
		TournamentModel.setInstance(mock(TournamentModel.class));
		GameModel.setInstance(mock(GameModel.class));
		f_engine = new Engine(null, 6);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testRun() {
		// given
		RankPlayer m_player = new RankPlayer();
		Set<RankPlayer> m_players = new HashSet<RankPlayer>();
		m_players.add(m_player);
		given(PlayerModel.getInstance().getData()).willReturn(m_players);
		// when
		f_engine.run();
		// then
	}

	@Test
	public void testProcessRank() {
		// given
		new GameExtModel();
		Map<RankGame, RankGameExt> l_map = GameExtModel.getInstance().getData();
		RankTournament m_tournament = new RankTournament();
		m_tournament.setId(52l);
		RankPlayer m_player = mock(RankPlayer.class);
		RankGame m_game[] = new RankGame[7];
		for (int cnt = 0; cnt != 7; cnt++) {
			m_game[cnt] = new RankGame(1,0,5,Color.WHITE,45+cnt);
			m_game[cnt].setOpponentRank(GoRank.getInstance(45));
			m_game[cnt].setRank(GoRank.getInstance(44));
			m_game[cnt].setResult(GameResult.PLAYERWON);
			m_game[cnt].setTournament(cnt==0?null:m_tournament);
			RankGameExt l_gameExt = new RankGameExt(m_game[cnt]);
			l_map.put(m_game[cnt], l_gameExt);
			l_gameExt.setTrailingQuality(3.2d);
		}
		List<RankGame> m_gameList = Arrays.asList(m_game);
		// when
		f_engine.processRank(3, m_player, m_gameList);
		// then
	}

	@Test
	public void testIncrementalRankObtained() throws Exception {
		//given
		f_engine.setRunDate(new Date(new GregorianCalendar(2015, 0, 15).getTimeInMillis()));
		new GameExtModel();
		RankPlayer l_player = new RankPlayer();
		RankGame l_game1 = new RankGame(5, 0, 7, Color.BLACK, 512);
		RankGame l_game2 = new RankGame(12, 0, 7, Color.WHITE, 723);
		RankGame l_game3 = new RankGame(18, 0, 7, Color.BLACK, 899);
		RankGame l_game4 = new RankGame(19, 0, 7, Color.BLACK, 900);
		RankGame l_game5 = new RankGame(50, 0, 7, Color.WHITE, 1034);
		RankGame l_game6 = new RankGame(102, 0, 7, Color.BLACK, 2034);
		RankGameExt l_gameExt1 = new RankGameExt(l_game1);
		RankGameExt l_gameExt2 = new RankGameExt(l_game2);
		RankGameExt l_gameExt3 = new RankGameExt(l_game3);
		RankGameExt l_gameExt4 = new RankGameExt(l_game4);
		RankGameExt l_gameExt5 = new RankGameExt(l_game5);
		RankGameExt l_gameExt6 = new RankGameExt(l_game6);
		Map<RankGame, RankGameExt> l_map = GameExtModel.getInstance().getData();
		l_map.put(l_game1, l_gameExt1);
		l_map.put(l_game2, l_gameExt2);
		l_map.put(l_game3, l_gameExt3);
		l_map.put(l_game4, l_gameExt4);
		l_map.put(l_game5, l_gameExt5);
		l_map.put(l_game6, l_gameExt6);
		l_game1.setResult(GameResult.PLAYERWON);
		l_game2.setResult(GameResult.PLAYERWON);
		l_game3.setResult(GameResult.PLAYERWON);
		l_game4.setResult(GameResult.PLAYERLOST);
		l_game5.setResult(GameResult.PLAYERWON);
		l_game6.setResult(GameResult.PLAYERWON);
		l_gameExt1.setTrailingQuality(5.62d);
		l_gameExt2.setTrailingQuality(5.74d);
		l_gameExt3.setTrailingQuality(5.01d);
		l_gameExt4.setTrailingQuality(5.012d);
		l_gameExt5.setTrailingQuality(5.55d);
		l_gameExt6.setTrailingQuality(5.98d);
		// when
		f_engine.gameSetup(l_game1);
		f_engine.gameSetup(l_game2);
		f_engine.gameSetup(l_game3);
		f_engine.gameSetup(l_game4);
		f_engine.gameSetup(l_game5);
		f_engine.gameSetup(l_game6);
		f_engine.incrementalRankObtained(5, l_player, 768);
		// cc
		f_engine.incrementalRankObtained(6, l_player, 769);
		// then
		assertEquals(2, f_engine.getLiveRanks().size());
		assertEquals(5.62f, f_engine.getLiveRanks().get(0).getStrength()[0], .001f);
	}
	
	
	@Test
	public void testProcessRanksIncremental() throws Exception {
		//given
		
		// when
		// then
	}
	
	@Test
	public void testProcessRanksModel() throws Exception {
		//given
		RankPlayer m_player1 = new RankPlayer();
		m_player1.setId(32l);
		RankPlayer m_player2 = new RankPlayer();
		m_player2.setId(65l);
		given(PlayerModel.getInstance().getElements()).willReturn(Arrays.asList(m_player1, m_player2));
		RankGame m_game11 = new RankGame(52, 0, 7, Color.BLACK, 582);
		RankGame m_game12 = new RankGame(53, 0, 7, Color.WHITE, 590);
		m_game11.setPlayer(m_player1);
		m_game11.setOpponentRank(GoRank.getInstance(42));
		m_game11.setRank(GoRank.getInstance(43));
		m_game11.setResult(GameResult.PLAYERWON);
		m_game12.setPlayer(m_player1);
		m_game12.setOpponentRank(GoRank.getInstance(42));
		m_game12.setRank(GoRank.getInstance(43));
		m_game12.setResult(GameResult.PLAYERLOST);
		RankGame m_game21 = new RankGame(54, 0, 7, Color.BLACK, 605);
		RankGame m_game22 = new RankGame(55, 0, 7, Color.WHITE, 681);
		m_game21.setPlayer(m_player2);
		m_game21.setOpponentRank(GoRank.getInstance(42));
		m_game21.setRank(GoRank.getInstance(43));
		m_game21.setResult(GameResult.PLAYERLOST);
		m_game22.setPlayer(m_player2);
		m_game22.setOpponentRank(GoRank.getInstance(42));
		m_game22.setRank(GoRank.getInstance(43));
		m_game22.setResult(GameResult.PLAYERWON);
		List<RankGame> l_games1 = Arrays.asList(m_game11, m_game12);
		List<RankGame> l_games2 = Arrays.asList(m_game21, m_game22);
		given(GameModel.getInstance().getContentAsList(m_player1)).willReturn(l_games1);
		given(GameModel.getInstance().getContentAsList(m_player2)).willReturn(l_games2);
		// when
		f_engine.processRanks();
		// then
	}
	
	@Test
	public void testMarkProcessGameEvaluationContext() throws Exception {
		//given
		RankGame l_game1 = new RankGame(5, 0, 7, Color.BLACK, 52);
		RankGame l_game2 = new RankGame(6, 0, 7, Color.BLACK, 53);
		// when
		f_engine.markProcessedGame();
		f_engine.gameSetup(l_game1);
		f_engine.gameSetup(l_game2);
		f_engine.gameSetup(l_game2);
		f_engine.gameSetup(l_game2);
		f_engine.gameSetup(l_game2);
		f_engine.gameSetup(l_game2);
		f_engine.markProcessedGame();
		// then
		verify(GameModel.getInstance()).updateContent(eq(l_game1), any(RankGame.class));
	}
	
	@Test
	public void testStoreCertificates() throws Exception {
		//given
		RankPlayer l_player = new RankPlayer(2802);
		@SuppressWarnings("deprecation")
		Date l_date = new Date(2018,03,01);
		LiveRankObtained l_lro1 = new LiveRankObtained(5, l_player, l_date, 32);
		LiveRankObtained l_lro2 = new LiveRankObtained(6, l_player, l_date, 44);
		List<LiveRankObtained> l_ranks = Arrays.asList(l_lro1, l_lro2);
		f_engine.setLiveRanks(l_ranks);
		
		// when
		f_engine.storeCertificates();
		// then
		verify(LiveRankObtainedModel.getInstance()).doBlockUpdate(any(Runnable.class));
	}
	
	@Test
	public void testRankLimit() {
		// given
		RankPlayer l_player = mock(RankPlayer.class);
		LiveRankObtained l_rank  = mock(LiveRankObtained.class);
		List<LiveRankObtained> l_ranks = Arrays.asList(l_rank);
		given(LiveRankObtainedModel.getInstance().getContentAsList(l_player)).willReturn(new ArrayList<LiveRankObtained>()).willReturn(l_ranks);
		given(l_rank.getRank()).willReturn(3,-14);
		// when
		// then
		// Incremental - no previous rank
		assertEquals(-9, f_engine.rankLimit(l_player));
		// Incremental - previous rank 3d
		assertEquals(4, f_engine.rankLimit(l_player));
		// Incremental - previous rank -14
		assertEquals(-9, f_engine.rankLimit(l_player));
		
	}
	
	@Test
	public void testGamesSufficientInt() {
		// given
		RankGame[] l_games = new RankGame[10];
		RankGameExt[] l_gamesExt = new RankGameExt[10];
		new GameExtModel();
		Map<RankGame, RankGameExt> l_map = GameExtModel.getInstance().getData();
		for (int cnt = 0; cnt != 10; cnt++) {
			l_games[cnt] = new RankGame(cnt+15,0,8,cnt<5?Color.BLACK:Color.WHITE,cnt+2900);
			l_gamesExt[cnt] = new RankGameExt(l_games[cnt]);
			l_map.put(l_games[cnt], l_gamesExt[cnt]);
			l_gamesExt[cnt].setTrailingQuality((510+(cnt*50))/100d);
			l_games[cnt].setResult((cnt&1)!=0?GameResult.PLAYERWON:GameResult.PLAYERLOST);
			if (cnt < 6) {
				f_engine.gameSetup(l_games[cnt]);
			}
		}
		// when
		// then
		assertFalse(f_engine.gamesSufficient(7));
		assertTrue(f_engine.gamesSufficient(6));
		assertTrue(f_engine.gamesSufficient(5));
	}
	
	@Test
	public void testProcessRankValuesRankPlayerList() {
		// given
		new GameExtModel();
		RankPlayer m_player1 = new RankPlayer();
		m_player1.setId(32l);
		RankPlayer m_player2 = new RankPlayer();
		m_player2.setId(65l);
		given(PlayerModel.getInstance().getElements()).willReturn(Arrays.asList(m_player1, m_player2));
		RankGame m_game11 = new RankGame(52, 0, 7, Color.BLACK, 582);
		RankGame m_game12 = new RankGame(53, 0, 7, Color.WHITE, 590);
		m_game11.setPlayer(m_player1);
		m_game11.setOpponentRank(GoRank.getInstance(42));
		m_game11.setRank(GoRank.getInstance(43));
		m_game11.setResult(GameResult.PLAYERWON);
		m_game12.setPlayer(m_player1);
		m_game12.setOpponentRank(GoRank.getInstance(42));
		m_game12.setRank(GoRank.getInstance(43));
		m_game12.setResult(GameResult.PLAYERLOST);
		RankGame m_game21 = new RankGame(54, 0, 7, Color.BLACK, 605);
		RankGame m_game22 = new RankGame(55, 0, 7, Color.WHITE, 681);
		m_game21.setPlayer(m_player2);
		m_game21.setOpponentRank(GoRank.getInstance(42));
		m_game21.setRank(GoRank.getInstance(43));
		m_game21.setResult(GameResult.PLAYERLOST);
		m_game22.setPlayer(m_player2);
		m_game22.setOpponentRank(GoRank.getInstance(42));
		m_game22.setRank(GoRank.getInstance(43));
		m_game22.setResult(GameResult.PLAYERWON);
		List<RankGame> l_games1 = Arrays.asList(m_game11, m_game12);
		List<RankGame> l_games2 = Arrays.asList(m_game21, m_game22);
		given(GameModel.getInstance().getContentAsList(m_player1)).willReturn(l_games1);
		given(GameModel.getInstance().getContentAsList(m_player2)).willReturn(l_games2);
		Map<RankGame, RankGameExt> l_map = GameExtModel.getInstance().getData();
		l_map.put(m_game11, new RankGameExt(m_game11));
		l_map.put(m_game12, new RankGameExt(m_game12));
		l_map.put(m_game21, new RankGameExt(m_game21));
		l_map.put(m_game22, new RankGameExt(m_game22));
		// when
		Engine.setTracing(true);
		f_engine.processRankValues(m_player1, Arrays.asList(m_game11, m_game12));
		// then
		
	}
}
