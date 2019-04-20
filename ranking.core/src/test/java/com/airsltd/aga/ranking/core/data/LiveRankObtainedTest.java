package com.airsltd.aga.ranking.core.data;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

import java.sql.Date;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.airsltd.aga.ranking.core.function.special.MovingAverageValue;
import com.airsltd.aga.ranking.core.model.GameExtModel;
import com.airsltd.aga.ranking.core.model.GameModel;
import com.airsltd.aga.ranking.core.model.PlayerModel;
import com.airsltd.aga.ranking.core.states.GameResult;
import com.airsltd.core.model.BlockModel;

public class LiveRankObtainedTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testCVSCreation() {
		// given
		LiveRankObtained l_obtained = generateRank();
		String[] l_expected = new String[] { "52", "5", "'2012-06-20'", "512", "31", "5", "12", 
				"18", "50", "102", "19", "3.62", "3.74", "3.01", "3.55", "3.98", "4.012" };
		// when
		String[] l_out = l_obtained.toStringCsv();
		// then
		assertArrayEquals(l_expected , l_out);
	}

	private LiveRankObtained generateRank() {
		RankPlayer l_player = new RankPlayer();
		l_player.setId(52l);
		PlayerModel m_playerModel = mock(PlayerModel.class);
		BlockModel.setFromIdModel(RankPlayer.class, m_playerModel);
		RankGame l_game1 = new RankGame(5, 0, 7, Color.BLACK, 512);
		RankGame l_game2 = new RankGame(12, 0, 7, Color.WHITE, 723);
		RankGame l_game3 = new RankGame(18, 0, 7, Color.BLACK, 899);
		RankGame l_game4 = new RankGame(19, 0, 7, Color.BLACK, 900);
		RankGame l_game5 = new RankGame(50, 0, 7, Color.WHITE, 1034);
		RankGame l_game6 = new RankGame(102, 0, 7, Color.BLACK, 2034);
		new GameModel();
		new GameExtModel();
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
		GameModel.getInstance().loadSegment(l_player, new HashSet<>(Arrays.asList(l_game1, l_game2, l_game3, l_game4, l_game5, l_game6)));
		l_game1.setResult(GameResult.PLAYERWON);
		l_game2.setResult(GameResult.PLAYERWON);
		l_game3.setResult(GameResult.PLAYERWON);
		l_game4.setResult(GameResult.PLAYERLOST);
		l_game5.setResult(GameResult.PLAYERWON);
		l_game6.setResult(GameResult.PLAYERWON);
		Date l_runDate = new Date(new GregorianCalendar(2012, 5, 20).getTimeInMillis());
		LiveRankObtained l_obtained = new LiveRankObtained(5, l_player, l_runDate, 512);
		MovingAverageValue<RankGame> l_games = new MovingAverageValue<RankGame>(6);
		l_games.addValue(l_game1);
		l_games.addValue(l_game2);
		l_games.addValue(l_game3);
		l_games.addValue(l_game4);
		l_games.addValue(l_game5);
		l_games.addValue(l_game6);
		l_gameExt1.setTrailingQuality(3.62d);
		l_gameExt2.setTrailingQuality(3.74d);
		l_gameExt3.setTrailingQuality(3.01d);
		l_gameExt4.setTrailingQuality(4.012d);
		l_gameExt5.setTrailingQuality(3.55d);
		l_gameExt6.setTrailingQuality(3.98d);
		l_obtained.fillState(l_games);
		return l_obtained;
	}
	
	@Test
	public void testCVSLoad() {
		// given
		generateRank();
		String[] l_expected = new String[] { "52", "5", "'2012-06-20'", "512", "31", "5", "12", 
				"18", "19", "50", "102", "3.62", "3.74", "3.01", "4.012", "3.55", "3.98" };
		// when
		LiveRankObtained l_read = new LiveRankObtained(l_expected);
		// then
		assertEquals(5,l_read.getRank());
		assertEquals("2012-06-20", l_read.getRunDate().toString());
		assertEquals(512, l_read.getMade());
		assertEquals(GamesWon.totalGames(5), l_read.getWonLost());
		
	}
	
	@Test
	public void testCopy() {
		// given
		LiveRankObtained l_obtained = generateRank();
		// when
		LiveRankObtained l_copy = new LiveRankObtained(l_obtained);
		// then
		assertFalse(l_obtained == l_copy);
		assertArrayEquals(l_obtained.toStringCsv(), l_copy.toStringCsv());
	}

	@Test
	public void testSetValues() {
		// given
		LiveRankObtained l_rank = generateRank();
		Double[] l_floats = new Double[] { 1.3d, 2.4d, 1.5d, 3.2d, 2.1d, 4.5d };
		RankGame l_game1 = new RankGame(9, 0, 7, Color.BLACK, 512);
		RankGame l_game2 = new RankGame(52, 0, 7, Color.WHITE, 723);
		RankGame l_game3 = new RankGame(74, 0, 7, Color.BLACK, 899);
		RankGame l_game4 = new RankGame(89, 0, 7, Color.BLACK, 900);
		RankGame l_game5 = new RankGame(100, 0, 7, Color.WHITE, 1034);
		RankGame l_game6 = new RankGame(105, 0, 7, Color.BLACK, 2034);
		RankGame[] l_games = new RankGame[] { l_game1, l_game2, l_game3, l_game4, l_game5, l_game6 };
		// when
		l_rank.setStrength(l_floats);
		l_rank.setGame(l_games);
		// then
		assertArrayEquals(l_floats, l_rank.getStrength());
		assertArrayEquals(l_games, l_rank.getGame());
		assertEquals(52l, l_rank.toSegment().getId());
	}
	
	@Test
	public void testGetPrettyRank() {
		// given
		LiveRankObtained l_rank = generateRank();
		// when
		// then
		assertEquals("5d", l_rank.getPrettyRank());
		// when
		l_rank.setRank(0);
		// then
		assertEquals("1k", l_rank.getPrettyRank());
	}
	
	@Test
	public void testGetMadeDate() {
		// given
		LiveRankObtained l_rank = generateRank();
		// when
		// then
		assertNull(l_rank.getMadeDate());
		// when
		int l_day = 20;
		for (RankGame l_game : l_rank.getGame()) {
			Date l_gameDate = new Date(new GregorianCalendar(2012, 5, l_day).getTimeInMillis());
			l_day =- 2;
			l_game.setDate(l_gameDate);
		}
		// then
		assertEquals("2012-06-20", l_rank.getMadeDate().toString());
		
	}
}
