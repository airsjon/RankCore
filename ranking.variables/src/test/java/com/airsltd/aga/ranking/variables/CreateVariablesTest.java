package com.airsltd.aga.ranking.variables;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Deque;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.airsltd.aga.ranking.core.data.Color;
import com.airsltd.aga.ranking.core.data.GoRank;
import com.airsltd.aga.ranking.core.data.RankGame;
import com.airsltd.aga.ranking.core.data.RankGameExt;
import com.airsltd.aga.ranking.core.data.RankGameLink;
import com.airsltd.aga.ranking.core.data.RankPlayer;
import com.airsltd.aga.ranking.core.data.RankRating;
import com.airsltd.aga.ranking.core.data.RankTournament;
import com.airsltd.aga.ranking.core.model.GameExtModel;
import com.airsltd.aga.ranking.core.model.GameModel;
import com.airsltd.aga.ranking.core.model.GameReverseModel;
import com.airsltd.aga.ranking.core.model.PlayerModel;
import com.airsltd.aga.ranking.core.model.RatingModel;
import com.airsltd.aga.ranking.core.model.TournamentModel;
import com.airsltd.core.data.Tuple;
import com.airsltd.core.model.BlockModel;

public class CreateVariablesTest extends ConnectionSetupTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	private CreateVariables f_variable;
	private RankGameLink f_reversedGame;
	private RankGame f_gameTwo;
	private RankGame f_gameOne;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		PlayerModel.setInstance(mock(PlayerModel.class));
		TournamentModel.setInstance(mock(TournamentModel.class));
		BlockModel.setFromIdModel(RankPlayer.class, PlayerModel.getInstance());
		BlockModel.setFromIdModel(RankTournament.class, TournamentModel.getInstance());
		new GameExtModel();
		GameModel.setInstance(mock(GameModel.class));
		RatingModel.setInstance(mock(RatingModel.class));
		GameReverseModel.setInstance(mock(GameReverseModel.class));
		f_gameOne = new RankGame(32,0,7,Color.WHITE,101);
		f_gameTwo = new RankGame(33,0,7,Color.BLACK,101);
		f_reversedGame = new RankGameLink(f_gameOne, f_gameTwo);
		given(GameReverseModel.getInstance().getElement(anyLong())).willReturn(f_reversedGame);
		f_variable = new CreateVariables();
	}

	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	public void testGetNextRating() {
		// given
		@SuppressWarnings("unchecked")
		Iterator<RankRating> m_iterator = mock(Iterator.class);
		given(m_iterator.hasNext()).willReturn(false, true);
		RankRating m_rankRating = mock(RankRating.class);
		given(m_rankRating.getDate()).willReturn(null, new Date(500000));
		given(m_iterator.next()).willReturn(m_rankRating);
		// when
		assertEquals(null, f_variable.getNextRating(m_iterator));
		assertEquals(m_rankRating, f_variable.getNextRating(m_iterator));
		// then
		verify(m_iterator, times(3)).hasNext();
		verify(m_iterator, times(2)).next();
	}

	@Test
	public void testLoadModels() {
		// given
		// when
		f_variable.loadModels();
		// then
		verify(PlayerModel.getInstance()).loadModel("");
		verify(TournamentModel.getInstance()).loadModel("");
		verify(GameModel.getInstance()).loadModel(null);
		verify(RatingModel.getInstance()).loadModel(null);
	}

	@Test
	public void testProcessRating() throws Exception {
		//given
		RankGame l_game1 = new RankGame(5, 0, 7, Color.BLACK, 1);
		RankGame l_game2 = new RankGame(6, 0, 7, Color.WHITE, 2);
		RankGame l_game3 = new RankGame(7, 0, 7, Color.WHITE, 3);
		List<RankGame> l_games = Arrays.asList(l_game1 , l_game2, l_game3);
		RankPlayer l_player = mock(RankPlayer.class);
		l_game1.setRank(GoRank.getInstance(55));
		l_game2.setRank(GoRank.getInstance(56));
		l_game3.setRank(GoRank.getInstance(57));
		l_game1.setOpponentRank(GoRank.getInstance(51));
		l_game2.setOpponentRank(GoRank.getInstance(52));
		l_game3.setOpponentRank(GoRank.getInstance(53));
		// when
		f_variable.processRating(l_games, l_player);
		// then
	}
	
	@Test
	public void testProcessRatingWithRatings() throws Exception {
		//given
		long[] l_indexExpected = new long[] {
				5, 8, 8, 8, 8, 8, 8,
				6, 8, 8, 8, 8, 8, 8,
				7, 8, 8, 8, 8, 8, 8
		};
		Object[] l_valueExpected = new Object[] {
				5.5f, 0.0f, 5.5f, 5.5501f, 7.12f, .22f, 7.1700997f, 
				6.5f,  0.0f, 6.5f, 6.5501f, 7.12f, .22f, 7.1700997f, 
				7.12f, .22f, 7.12f, 7.1700997f, 7.34f, .18f, 7.3901f, 
		};
		RankGame l_game1 = new RankGame(5, 0, 7, Color.BLACK, 1);
		RankGame l_game2 = new RankGame(6, 0, 7, Color.BLACK, 2);
		RankGame l_game3 = new RankGame(7, 0, 7, Color.BLACK, 3);
		RankGame l_gameReversed = new RankGame(8, 0, 7, Color.WHITE, 1);
		List<RankGame> l_games = Arrays.asList(l_game1 , l_game2, l_game3);
		RankPlayer l_player = mock(RankPlayer.class);
		l_game1.setRank(GoRank.getInstance(55));
		l_game2.setRank(GoRank.getInstance(56));
		l_game3.setRank(GoRank.getInstance(57));
		l_game1.setOpponentRank(GoRank.getInstance(51));
		l_game2.setOpponentRank(GoRank.getInstance(52));
		l_game3.setOpponentRank(GoRank.getInstance(53));
		l_game1.setDate(new Date(new GregorianCalendar(2015,1,15).getTimeInMillis()));
		l_game2.setDate(new Date(new GregorianCalendar(2015,2,15).getTimeInMillis()));
		l_game3.setDate(new Date(new GregorianCalendar(2015,3,15).getTimeInMillis()));
		RankRating l_rank1 = new RankRating();
		l_rank1.setDate(new Date(new GregorianCalendar(2015,2,17).getTimeInMillis()));
		l_rank1.setRating(7.12f);
		l_rank1.setSigma(.22f);
		RankRating l_rank2 = new RankRating();
		l_rank2.setDate(new Date(new GregorianCalendar(2015,3,17).getTimeInMillis()));
		l_rank2.setRating(7.34f);
		l_rank2.setSigma(.18f);
		given(RatingModel.getInstance().getContentAsList(any(RankPlayer.class))).willReturn(
				Arrays.asList(l_rank1, l_rank2)
				);
		RankGameLink m_reverse = mock(RankGameLink.class);
		given(m_reverse.getReverse()).willReturn(l_gameReversed);
		given(GameReverseModel.getInstance().getElement(anyLong())).willReturn(m_reverse);
		// when
		f_variable.processRating(l_games, l_player);
		// then
	}
	
	@Test
	public void testProcessRatingWithRatingsHandicapped() throws Exception {
		//given
		long[] l_indexExpected = new long[] {
				5, 8, 8, 8, 8, 8, 8,
				6, 8, 8, 8, 8, 8, 8,
				7, 8, 8, 8, 8, 8, 8
		};
		Object[] l_valueExpected = new Object[] {
				5.5f, 0.0f, 5.5f, 7.5f, 7.12f, .22f, 9.12f, 
				6.5f,  0.0f, 6.5f, 8.5f, 7.12f, .22f, 9.12f, 
				7.12f, .22f, 7.12f, 9.12f, 7.34f, .18f, 9.34f, 
		};
		RankGame l_game1 = new RankGame(5, 2, 0, Color.BLACK, 1);
		RankGame l_game2 = new RankGame(6, 3, 0, Color.WHITE, 2);
		RankGame l_game3 = new RankGame(7, 4, 0, Color.BLACK, 3);
		RankGame l_gameReversed = new RankGame(8, 2, 0, Color.WHITE, 1);
		List<RankGame> l_games = Arrays.asList(l_game1 , l_game2, l_game3);
		RankPlayer l_player = mock(RankPlayer.class);
		l_game1.setRank(GoRank.getInstance(55));
		l_game2.setRank(GoRank.getInstance(56));
		l_game3.setRank(GoRank.getInstance(57));
		l_game1.setOpponentRank(GoRank.getInstance(51));
		l_game2.setOpponentRank(GoRank.getInstance(52));
		l_game3.setOpponentRank(GoRank.getInstance(53));
		l_game1.setDate(new Date(new GregorianCalendar(2015,1,15).getTimeInMillis()));
		l_game2.setDate(new Date(new GregorianCalendar(2015,2,15).getTimeInMillis()));
		l_game3.setDate(new Date(new GregorianCalendar(2015,3,15).getTimeInMillis()));
		RankRating l_rank1 = new RankRating();
		l_rank1.setDate(new Date(new GregorianCalendar(2015,2,17).getTimeInMillis()));
		l_rank1.setRating(7.12f);
		l_rank1.setSigma(.22f);
		RankRating l_rank2 = new RankRating();
		l_rank2.setDate(new Date(new GregorianCalendar(2015,3,17).getTimeInMillis()));
		l_rank2.setRating(7.34f);
		l_rank2.setSigma(.18f);
		given(RatingModel.getInstance().getContentAsList(any(RankPlayer.class))).willReturn(
				Arrays.asList(l_rank1, l_rank2)
				);
		RankGameLink m_reverse = mock(RankGameLink.class);
		given(m_reverse.getReverse()).willReturn(l_gameReversed);
		given(GameReverseModel.getInstance().getElement(anyLong())).willReturn(m_reverse);
		// when
		f_variable.processRating(l_games, l_player);
		// then
	}
	
	@Test
	public void testProcessRatingVariable() throws Exception {
		//given
		RankGame l_game1 = new RankGame(5, 0, 7, Color.BLACK, 1);
		RankGame l_game2 = new RankGame(6, 0, 7, Color.BLACK, 2);
		RankGame l_game3 = new RankGame(7, 0, 7, Color.BLACK, 3);
		List<RankGame> l_games = Arrays.asList(l_game1 , l_game2, l_game3);
		RankPlayer l_player = mock(RankPlayer.class);
		l_game1.setRank(GoRank.getInstance(55));
		l_game2.setRank(GoRank.getInstance(56));
		l_game3.setRank(GoRank.getInstance(57));
		l_game1.setOpponentRank(GoRank.getInstance(51));
		l_game2.setOpponentRank(GoRank.getInstance(52));
		l_game3.setOpponentRank(GoRank.getInstance(53));
		given(PlayerModel.getInstance().getElements()).willReturn(Arrays.asList(l_player));
		given(GameModel.getInstance().getContentAsList(l_player)).willReturn(l_games);
		// when
		f_variable.processRatingVariable();
		// then
	}
	
	@Test
	public void testGetGameRating() throws Exception {
		//given
		Tuple<Double, Double> l_current = new Tuple<Double, Double>(3.2d, .23d);
		RankGame l_game = mock(RankGame.class);
		@SuppressWarnings("unchecked")
		Deque<RankRating> l_deque = mock(Deque.class);
		given(l_game.getRank()).willReturn(GoRank.getInstance(52));
		given(l_game.getDate()).willReturn(null, null,
				new Date(new GregorianCalendar(2014, 5, 15).getTime().getTime()));
		given(l_deque.isEmpty()).willReturn(true, false);
		RankRating l_next = mock(RankRating.class);
		RankRating l_next2 = mock(RankRating.class);
		given(l_deque.peekFirst()).willReturn(l_next, l_next2);
		given(l_deque.pollFirst()).willReturn(l_next, l_next2);
		given(l_next.getDate()).willReturn(
				new Date(new GregorianCalendar(2014, 5, 13).getTime().getTime()));
		given(l_next2.getDate()).willReturn(
				new Date(new GregorianCalendar(2014, 5, 17).getTime().getTime()));
		given(l_next.toTupleDouble()).willReturn(new Tuple<Double,Double>(2.9d, .29d));
		given(l_next2.toTupleDouble()).willReturn(new Tuple<Double,Double>(3.3d, .25d));
		// when
		// game has no date and it can not be determined which rating to use
		assertEquals(new Tuple<Float, Float>(null, null), f_variable.getGameRating(new Tuple<Double, Double>(null, null), l_game, l_deque));
		// no ratings found
		assertEquals(new Tuple<Float, Float>(null, null), f_variable.getGameRating(new Tuple<Double, Double>(null, null), l_game, null));
		// no ratings left on list (returns current)
		assertEquals(new Tuple<Double, Double>(3.2d, .23d), f_variable.getGameRating(l_current, l_game, l_deque));
		// peel off one from deque
		assertEquals(new Tuple<Double, Double>(2.9d, .29d), f_variable.getGameRating(l_current, l_game, l_deque));
		// then
	}
	
	@Test
	public void testMain() throws Exception {
		// given
		// when
		CreateVariables.main(new String[] {});
		// then
	}
	
	@Test
	public void testIsBefore() throws Exception {
		//given
		Date l_date = new Date(new GregorianCalendar(2015, 8, 20).getTimeInMillis());
		Date l_dateBefore = new Date(new GregorianCalendar(2015, 7, 20).getTimeInMillis());
		Date l_dateAfter = new Date(new GregorianCalendar(2015, 9, 20).getTimeInMillis());
		// when
		assertTrue(f_variable.isBefore(null, l_date));
		assertTrue(f_variable.isBefore(l_dateBefore, l_date));
		assertFalse(f_variable.isBefore(l_dateAfter, l_date));
		// then
	}
	
	/**
	 * A test for bug #158
	 * 
	 * @throws Exception
	 */
	@Test
	public void testWhiteHandicapFail() throws Exception {
		// given
		RankPlayer[] m_player = new RankPlayer[] {
				new RankPlayer(345),
				new RankPlayer(346)
		};
		m_player[0].setId(1);
		m_player[1].setId(2);
		RankGame[] m_rankGames = new RankGame[] {
				new RankGame(1, 0, 7, Color.BLACK, 1),
				new RankGame(2, 0, 7, Color.WHITE, 1),
				new RankGame(3, 0, 7, Color.BLACK, 2),
				new RankGame(4, 0, 7, Color.WHITE, 2),
				new RankGame(5, 2, 0, Color.BLACK, 3),
				new RankGame(6, 2, 0, Color.WHITE, 3),
				new RankGame(7, 2, 0, Color.WHITE, 4),
				new RankGame(8, 2, 0, Color.BLACK, 4)
		};
		m_rankGames[0].setPlayer(m_player[0]);
		m_rankGames[0].setOpponent(m_player[1]);
		m_rankGames[1].setPlayer(m_player[1]);
		m_rankGames[1].setOpponent(m_player[0]);
		m_rankGames[2].setPlayer(m_player[0]);
		m_rankGames[2].setOpponent(m_player[1]);
		m_rankGames[3].setPlayer(m_player[1]);
		m_rankGames[3].setOpponent(m_player[0]);
		m_rankGames[4].setPlayer(m_player[0]);
		m_rankGames[4].setOpponent(m_player[1]);
		m_rankGames[5].setPlayer(m_player[1]);
		m_rankGames[5].setOpponent(m_player[0]);
		m_rankGames[6].setPlayer(m_player[1]);
		m_rankGames[6].setOpponent(m_player[0]);
		m_rankGames[7].setPlayer(m_player[0]);
		m_rankGames[7].setOpponent(m_player[1]);
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		m_rankGames[0].setDate(new Date(sdf.parse("08/10/2017").getTime()));
		m_rankGames[1].setDate(new Date(sdf.parse("08/10/2017").getTime()));
		m_rankGames[2].setDate(new Date(sdf.parse("08/12/2017").getTime()));
		m_rankGames[3].setDate(new Date(sdf.parse("08/12/2017").getTime()));
		m_rankGames[4].setDate(new Date(sdf.parse("08/14/2017").getTime()));
		m_rankGames[5].setDate(new Date(sdf.parse("08/14/2017").getTime()));
		m_rankGames[6].setDate(new Date(sdf.parse("08/16/2017").getTime()));
		m_rankGames[7].setDate(new Date(sdf.parse("08/16/2017").getTime()));
		
		RankRating[] l_rating = new RankRating[] {
				new RankRating(),
				new RankRating(),
				new RankRating(),
				new RankRating(),
				new RankRating(),
				new RankRating(),
				new RankRating(),
				new RankRating()
		};
		for (int cntRating = 0; cntRating!=8; cntRating++) {
			l_rating[cntRating] = loadRating(cntRating, m_player[0], m_player[1]);
		}
		List<RankRating> l_ratingList1 = Arrays.asList(l_rating[0], l_rating[1], l_rating[2], l_rating[3]);
		List<RankRating> l_ratingList2 = Arrays.asList(l_rating[4], l_rating[5], l_rating[6], l_rating[7]);
		given(RatingModel.getInstance().getContentAsList(m_player[0])).willReturn(l_ratingList1);
		given(RatingModel.getInstance().getContentAsList(m_player[1])).willReturn(l_ratingList2);
		
		RankGameLink[] l_links = new RankGameLink[] {
				new RankGameLink(m_rankGames[0], m_rankGames[1]),
				new RankGameLink(m_rankGames[1], m_rankGames[0]),
				new RankGameLink(m_rankGames[2], m_rankGames[3]),
				new RankGameLink(m_rankGames[3], m_rankGames[2]),
				new RankGameLink(m_rankGames[4], m_rankGames[5]),
				new RankGameLink(m_rankGames[5], m_rankGames[4]),
				new RankGameLink(m_rankGames[6], m_rankGames[7]),
				new RankGameLink(m_rankGames[7], m_rankGames[6])
		};
		for (int cnt = 0; cnt != 8; cnt++) {
			given(GameReverseModel.getInstance().getElement(cnt+1)).willReturn(l_links[cnt]);
		}
		List<RankGame> m_player0Games = Arrays.asList(m_rankGames[0],
				m_rankGames[2],
				m_rankGames[4],
				m_rankGames[7]);
		List<RankGame> m_player1Games = Arrays.asList(m_rankGames[1],
				m_rankGames[3],
				m_rankGames[5],
				m_rankGames[6]);
		// when - Step one process games for each player
		f_variable.processRating(m_player0Games , m_player[0]);
		f_variable.processRating(m_player1Games , m_player[1]);
		// then
		float[] l_expectedValue = new float[] {
				.37029383f,
				.6297062f,
				.059245903f,
				.9407541f,
				.60407805f,
				.39592195f,
		};
	}

	private RankRating loadRating(int p_cntRating, RankPlayer p_player1, RankPlayer p_player2) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		RankRating l_retVal = new RankRating();
		Date l_date[] = new Date[] {
				new Date(sdf.parse("08/09/2017").getTime()),
				new Date(sdf.parse("08/11/2017").getTime()),
				new Date(sdf.parse("08/13/2017").getTime()),
				new Date(sdf.parse("08/15/2017").getTime())
		};
		float l_rating[] = new float[] {
				2.3f,
				2.4f,
				2.7f,
				2.6f,
				2.7f,
				2.8f,
				4.4f,
				4.3f
		};
		int l_cnt = p_cntRating<4?p_cntRating:p_cntRating-4;
		l_retVal.setPlayer(p_cntRating<4?p_player1:p_player2);
		l_retVal.setDate(l_date[l_cnt]);
		l_retVal.setRating(l_rating[p_cntRating]);
		l_retVal.setSigma(.123f + (p_cntRating/16));
		return l_retVal;
	}
	
	@Test
	public void testSetIncrementalBoolean() {
		// given
		// when
		CreateVariables.setIncremental(true);
		// then
		assertTrue(CreateVariables.isIncremental());
	}
	
	@Test
	public void testCheckRankGame() {
		// given
		// when
		f_variable.check(f_gameOne);
		// then
		assertNull(extGame(f_gameOne).getProbability());
		// when
		extGame(f_gameOne).setPlayersRating(3.2d);
		f_variable.check(f_gameOne);
		// then
		assertNull(extGame(f_gameOne).getProbability());
		// when
		extGame(f_gameOne).setOpponentRating(3.5d);
		f_variable.check(f_gameOne);
		// then
		assertEquals(.37f, extGame(f_gameOne).getProbability(), .001f);
	}

	private RankGameExt extGame(RankGame p_game) {
		Map<RankGame, RankGameExt> l_map = GameExtModel.getInstance().getData();
		RankGameExt l_retVal = l_map.get(p_game);
		if (l_retVal == null) {
			l_retVal = new RankGameExt(p_game);
			l_map.put(p_game, l_retVal);
		}
		return l_retVal;
	}

	@Test
	public void testCheckTrailingRankGame() {
		// given
		// when
		f_variable.checkTrailing(f_gameOne);
		// then
		assertNull(extGame(f_gameOne).getTrailingProbability());
		// when
		extGame(f_gameOne).setPlayersTrailingRating(3.2d);
		f_variable.checkTrailing(f_gameOne);
		// then
		assertNull(extGame(f_gameOne).getTrailingProbability());
		// when
		extGame(f_gameOne).setOpponentsTrailingRating(3.5d);
		f_variable.checkTrailing(f_gameOne);
		// then
		assertEquals(.37f, extGame(f_gameOne).getTrailingProbability(), .001f);
	}
	
	/**
	 * Mainly for code coverage.
	 */
	@Test
	public void testDumpCounts() {
		// given
		// when
		f_variable.dumpCounts();
		// then
	}
	
}
