package com.airsltd.aga.ranking.convert;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.Callable;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import com.airsltd.aga.ranking.core.data.Color;
import com.airsltd.aga.ranking.core.data.GoRank;
import com.airsltd.aga.ranking.core.data.RankGame;
import com.airsltd.aga.ranking.core.data.RankGameLink;
import com.airsltd.aga.ranking.core.data.RankPlayer;
import com.airsltd.aga.ranking.core.data.RankRating;
import com.airsltd.aga.ranking.core.data.RankTournament;
import com.airsltd.aga.ranking.core.model.GameModel;
import com.airsltd.aga.ranking.core.model.GameReverseModel;
import com.airsltd.aga.ranking.core.model.PlayerModel;
import com.airsltd.aga.ranking.core.model.RankConnection;
import com.airsltd.aga.ranking.core.model.RatingModel;
import com.airsltd.aga.ranking.core.model.TournamentModel;
import com.airsltd.aga.ranking.core.states.GameResult;
import com.airsltd.core.collections.AirsCollections;
import com.airsltd.core.data.AirsPooledConnection;
import com.airsltd.core.data.BlockProvider;

public class ConvertTest extends ConnectionSetup {

	Convert f_convert;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		super.setUp();
		PlayerModel.setInstance(mock(PlayerModel.class));
		GameModel.setInstance(mock(GameModel.class));
		RatingModel.setInstance(mock(RatingModel.class));
		TournamentModel.setInstance(mock(TournamentModel.class));
		GameReverseModel.setInstance(mock(GameReverseModel.class));
		f_convert = new Convert(null);
		given(PlayerModel.getInstance().fromAGAId(3)).willReturn(new RankPlayer(3));
		given(PlayerModel.getInstance().fromAGAId(4)).willReturn(new RankPlayer(4));
		given(PlayerModel.getInstance().fromAGAId(5)).willReturn(new RankPlayer(5));
		given(PlayerModel.getInstance().fromAGAId(6)).willReturn(new RankPlayer(6));
		given(PlayerModel.getInstance().fromAGAId(7)).willReturn(new RankPlayer(7));
		given(PlayerModel.getInstance().fromAGAId(8)).willReturn(new RankPlayer(8));
	}

	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	public void testMain() throws SQLException, ParseException {
		// given
		RankConnection.setInstance(mock(RankConnection.class));
		AirsPooledConnection.setInstance(mock(AirsPooledConnection.class));
		given(AirsPooledConnection.getInstance().getConnection()).willReturn(m_connection);
		// when
		Convert.main(new String[] {});
		// then
	}

	@Test
	public void testTruncateDatabases() throws SQLException {
		// given
		// when
		f_convert.truncateDatabases();
		// then
		verify(m_state).execute("delete from `usgo_agarank`.`games`");
		verify(m_state).execute("delete from `usgo_agarank`.`ratings`");
		verify(m_state).execute("delete from `usgo_agarank`.`tournament`");
		verify(m_state).execute("delete from `usgo_agarank`.`players`");
		
	}

	@Test
	public void testLoadModels() {
		// given
		// when
		f_convert.loadModels();
		// then
		verify(PlayerModel.getInstance()).loadModel("");
		verify(TournamentModel.getInstance()).loadModel("");
		verify(GameModel.getInstance()).loadModel(null);
		verify(RatingModel.getInstance()).loadModel(null);
	}

	@Test
	public void testStoreGames() throws SQLException {
		// Given
		testConvertGames();
		// When
		f_convert.storeGames();
		// Then
		verify(GameModel.getInstance(),times(6)).addContent(any(RankGame.class));
	}
	
	@Test(expected=RuntimeException.class)
	public final void testStoreGames_exceptionFail() throws SQLException {
		// given
		testConvertGames();
		GameModel.setInstance(mock(GameModel.class));
		Mockito.doThrow(new RuntimeException()).when(GameModel.getInstance()).setInitialLoad(true);
		// then
		f_convert.storeGames();
	}

	@Test
	public void testStoreRatings() throws SQLException {
		// given
		testConvertRatings();
		f_convert.getPlayer(4).playedGame();
		// when
		f_convert.storeRatings();
		// then
		verify(RatingModel.getInstance(), times(1)).addContent(any(RankRating.class));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testStorePlayers() {
		// given
		new PlayerModel();
		PlayerModel.getInstance().setBlockProvider(mock(BlockProvider.class));
		// when
		List<RankPlayer> l_players = f_convert.getPlayers();
		l_players.add(new RankPlayer());
		l_players.add(new RankPlayer());
		l_players.add(new RankPlayer());
		for (int cnt = 0; cnt !=2; cnt++) {
			l_players.get(cnt).playedGame();
		}
		f_convert.storePlayers();
		// then
		verify(PlayerModel.getInstance().getBlockProvider(), times(2)).addContent(any(RankPlayer.class));
		
	}

	@Test
	public void testStoreTournaments() throws SQLException {
		// given
		testConvertTournaments();
		// when
		f_convert.storeTournaments();
		// then
	}

	@Test
	public void testConvertTournaments() throws SQLException {
		// given
		given(m_rs.next()).willReturn(true, true, true, false);
		given(m_rs.getString("Tournament_Code")).willReturn("t1", "2012 US Open", "blarg");
		// when
		f_convert.convertTournaments();
		// then
		assertEquals(3, f_convert.getTournaments().size());
		assertNotNull(f_convert.getTournaments().get("t1"));
		assertNotNull(f_convert.getTournaments().get("2012 US Open"));
		assertNotNull(f_convert.getTournaments().get("blarg"));
		assertNull(f_convert.getTournaments().get("t2"));
	}

	@Test
	public void testConvertTournamentsIncremental() throws SQLException {
		try {
			// given
			given(m_rs.next()).willReturn(true, true, true, false);
			given(m_rs.getString("Tournament_Code")).willReturn("t1", "2012 US Open", "blarg");
			given(TournamentModel.getInstance().contains("t1")).willReturn(true);
			// when
			Convert.setIncremental(true);
			f_convert.convertTournaments();
			// then
			assertEquals(2, f_convert.getTournaments().size());
			assertNotNull(f_convert.getTournaments().get("2012 US Open"));
			assertNotNull(f_convert.getTournaments().get("blarg"));
			assertNull(f_convert.getTournaments().get("t2"));
		} finally {
			Convert.setIncremental(false);
		}
	}

	@Test
	public void testConvertTournamentThrows() throws Throwable {
		// given
		// when
		int l_throws = testThrows(0xF, new Callable<Boolean>() {
			
			@Override
			public Boolean call() throws SQLException {
				f_convert.convertTournaments();
				return true;
			}
		}, SQLException.class);
		// then
		assertEquals(14, l_throws);
	}

	@Test
	public void testConvertRatings() throws SQLException {
		// given
		given(m_rs.next()).willReturn(false, true, true, true, false);
		given(m_rs.getInt("Pin_Player")).willReturn(3, 4, 5);
		given(m_rs.getFloat("Rating")).willReturn(0f, 3.2f, 3.3f);
		given(m_rs.getFloat("Sigma")).willReturn(.23f);
		// when
		f_convert.convertRatings();
		// then
		assertEquals(0, f_convert.getRatings().size());
		// when
		f_convert.convertRatings();
		// then
		assertEquals(2, f_convert.getRatings().size());
		assertEquals(4l, f_convert.getRatings().get(0).getPlayer().getPin_Player());
		assertEquals(5l, f_convert.getRatings().get(1).getPlayer().getPin_Player());
	}

	@Test
	public void testConvertRatingsIncremental() throws SQLException {
		try {
			// given
			given(m_rs.next()).willReturn(true, true, true, false);
			given(m_rs.getInt("Pin_Player")).willReturn(3, 4, 5);
			given(m_rs.getFloat("Rating")).willReturn(0f, 3.2f, 3.3f);
			given(m_rs.getFloat("Sigma")).willReturn(.23f);
			given(m_rs.getDate("Elab_Date")).willReturn(
					new Date(new GregorianCalendar(2015,10,4).getTime().getTime()),
					new Date(new GregorianCalendar(2014, 11, 5).getTime().getTime()),
					new Date(new GregorianCalendar(2012,10,4).getTime().getTime()));
			given(RatingModel.getInstance().contains(any(RankPlayer.class), eq(new Date(new GregorianCalendar(2012,10,4).getTime().getTime())))).
					willReturn(true);
			// when
			Convert.setIncremental(true);
			f_convert.convertRatings();
			// then
			assertEquals(1, f_convert.getRatings().size());
			assertEquals(4l, f_convert.getRatings().get(0).getPlayer().getPin_Player());
		} finally {
			Convert.setIncremental(false);
		}
	}

	@Test
	public void testConvertGames() throws SQLException {
		// given
		given(m_rs.next()).willReturn(true, true, true, false);
		given(m_rs.getString("Tournament_Code")).willReturn("x1", "x2", "x1");
		given(m_rs.getInt("Handicap")).willReturn(2,0,0);
		given(m_rs.getInt("Komi")).willReturn(0,7,7);
		given(m_rs.getInt("Pin_Player_1")).willReturn(4, 5, 6);
		given(m_rs.getInt("Pin_Player_2")).willReturn(5, 6, 5);
		given(m_rs.getString("Result")).willReturn("W","B","B");
		// when
		f_convert.convertGames(null);
		// then
		assertEquals(6, f_convert.getGames().size());
		assertEquals(GameResult.PLAYERWON, f_convert.getGames().get(0).getResult());
		assertEquals(2, f_convert.getGames().get(0).getHandicap());
		assertEquals(0, f_convert.getGames().get(0).getKomi());
		assertEquals(GameResult.PLAYERLOST, f_convert.getGames().get(1).getResult());
		assertEquals(2, f_convert.getGames().get(1).getHandicap());
		assertEquals(0, f_convert.getGames().get(1).getKomi());
		assertEquals(GameResult.PLAYERLOST, f_convert.getGames().get(2).getResult());
		assertEquals(0, f_convert.getGames().get(2).getHandicap());
		assertEquals(7, f_convert.getGames().get(2).getKomi());
		assertEquals(GameResult.PLAYERWON, f_convert.getGames().get(3).getResult());
		assertEquals(0, f_convert.getGames().get(3).getHandicap());
		assertEquals(7, f_convert.getGames().get(3).getKomi());
		assertEquals(GameResult.PLAYERLOST, f_convert.getGames().get(4).getResult());
		assertEquals(0, f_convert.getGames().get(4).getHandicap());
		assertEquals(7, f_convert.getGames().get(4).getKomi());
		assertEquals(GameResult.PLAYERWON, f_convert.getGames().get(5).getResult());
		assertEquals(0, f_convert.getGames().get(5).getHandicap());
		assertEquals(7, f_convert.getGames().get(5).getKomi());
	}

	@Test
	public void testConvertGamesIncremental() throws SQLException {
		try {
			// given
			given(m_rs.next()).willReturn(true, true, true, false);
			given(m_rs.getString("Tournament_Code")).willReturn("x2", "x1");
			given(m_rs.getInt("Handicap")).willReturn(0,0);
			given(m_rs.getInt("Komi")).willReturn(7,7);
			given(m_rs.getInt("Pin_Player_1")).willReturn(5, 6);
			given(m_rs.getInt("Pin_Player_2")).willReturn(6, 5);
			given(m_rs.getString("Result")).willReturn("B","B");
			given(m_rs.getInt("Game_ID")).willReturn(514, 515, 516);
			given(GameModel.getInstance().getAGAElement(any(RankPlayer.class), eq(514))).willReturn(mock(RankGame.class));
			// when
			Convert.setIncremental(true);
			f_convert.convertGames(null);
			// then
			assertEquals(6, f_convert.getGames().size());
			assertEquals(GameResult.PLAYERLOST, f_convert.getGames().get(0).getResult());
			assertEquals(0, f_convert.getGames().get(0).getHandicap());
			assertEquals(7, f_convert.getGames().get(0).getKomi());
			assertEquals(GameResult.PLAYERWON, f_convert.getGames().get(1).getResult());
			assertEquals(0, f_convert.getGames().get(1).getHandicap());
			assertEquals(7, f_convert.getGames().get(1).getKomi());
			assertEquals(GameResult.PLAYERLOST, f_convert.getGames().get(2).getResult());
			assertEquals(0, f_convert.getGames().get(2).getHandicap());
			assertEquals(7, f_convert.getGames().get(2).getKomi());
			assertEquals(GameResult.PLAYERWON, f_convert.getGames().get(3).getResult());
			assertEquals(0, f_convert.getGames().get(3).getHandicap());
			assertEquals(7, f_convert.getGames().get(3).getKomi());
		} finally {
			Convert.setIncremental(false);
		}
	}

	@Test
	public void testGetPlayer() {
		// given
		// when
		RankPlayer l_player = f_convert.getPlayer(32);
		// then
		assertEquals(l_player, f_convert.getPlayer(32));
	}

	@Test
	public void testCreateGame() {
		// given
		RankTournament m_tournament = mock(RankTournament.class);
		given(m_tournament.getPersistentID()).willReturn(32l);
		RankPlayer m_player = mock(RankPlayer.class);
		RankPlayer m_opponent = mock(RankPlayer.class);
		given(m_tournament.getPersistentID()).willReturn(32l);
		given(m_player.getPersistentID()).willReturn(82l);
		given(m_opponent.getPersistentID()).willReturn(123l);
		String[] l_outArray = new String[] {
				"-1", "82", "'2014-04-20'", "32", "3", "123", "1", "0", "7", "1", "'53'", "'54'", "0", "0", "''"
		};
		// when
		f_convert.createGame(m_tournament, 3, m_player, new Date(new GregorianCalendar(2014,3,20).getTime().getTime()), Color.WHITE, GameResult.PLAYERLOST, GoRank.getInstance(53), m_opponent, GoRank.getInstance(54), 0, 7, 0);
		// then
		assertArrayEquals(l_outArray, f_convert.getGames().get(f_convert.getGames().size()-1).toStringCsv());
	}
	
	@Test
	public void testStoreGameLinks() throws SQLException {
		// given
		GameReverseModel l_model = GameReverseModel.getInstance();
		// when
		testConvertGames();
		f_convert.storeGameLinks();
		// then
		verify(l_model, times(6)).addContent(any(RankGameLink.class));
	}
	
	@Test
	public void testConvertPlayers() throws Exception {
		//given
		new PlayerModel();
		given(m_rs.next()).willReturn(false, true, true, true, true, true, false);
		given(m_rs.getInt("Pin_Player")).willReturn(2803, 13241, 15, 7043, 555);
		given(m_rs.getDate("dob")).willReturn(null, null, null, new Date(new GregorianCalendar(1963,8,20).getTime().getTime()),null);
		PlayerModel.getInstance().addData(new RankPlayer(6));
		// when
		f_convert.convertPlayers();
		// then
		assertEquals(0, f_convert.getPlayers().size());
		// when
		f_convert.convertPlayers();
		// then
		assertEquals(5, f_convert.getPlayers().size());
		assertTrue(f_convert.getPlayers().contains(PlayerModel.getInstance().fromAGAId(2803)));
		assertTrue(f_convert.getPlayers().contains(PlayerModel.getInstance().fromAGAId(13241)));
		assertTrue(f_convert.getPlayers().contains(PlayerModel.getInstance().fromAGAId(15)));
		assertTrue(f_convert.getPlayers().contains(PlayerModel.getInstance().fromAGAId(7043)));
		assertTrue(f_convert.getPlayers().contains(PlayerModel.getInstance().fromAGAId(555)));
	}

	@Test
	public void testConvertPlayersIncremental() throws Exception {
		try {
			//given
			new PlayerModel();
			given(m_rs.next()).willReturn(true, true, true, true, true, false);
			given(m_rs.getInt("Pin_Player")).willReturn(2, 7, 6, 4, 4);
			given(m_rs.getDate("dob")).willReturn(null, null, null, new Date(new GregorianCalendar(1963,8,20).getTime().getTime()),null);
			Convert.setIncremental(true);
			PlayerModel.getInstance().addData(new RankPlayer(6));
			// when
			f_convert.convertPlayers();
			// then
			assertEquals(3, f_convert.getPlayers().size());
			assertTrue(AirsCollections.lookFor((o1)-> o1.getPin_Player()==2, f_convert.getPlayers())!=null);
			assertTrue(AirsCollections.lookFor((o1)-> o1.getPin_Player()==7, f_convert.getPlayers())!=null);
			assertTrue(AirsCollections.lookFor((o1)-> o1.getPin_Player()==4, f_convert.getPlayers())!=null);
		} finally {
			Convert.setIncremental(false);
		}
	}

}
