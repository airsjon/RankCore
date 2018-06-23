package com.airsltd.aga.ranking.core.model;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.airsltd.aga.ranking.core.data.Color;
import com.airsltd.aga.ranking.core.data.RankGame;
import com.airsltd.aga.ranking.core.data.RankPlayer;
import com.airsltd.core.data.BlockProvider;
import com.airsltd.core.data.ISqlSelectorCallBack;


public class GameModelTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	private GameModel f_model;

	@Before
	public void setUp() throws Exception {
		GameModel.setInstance(null);
		f_model = GameModel.getInstance();
		assertEquals(f_model, GameModel.getInstance());
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testLoadModelRankPlayer() {
		// given
		
		@SuppressWarnings("unchecked")
		BlockProvider<RankGame> m_provider = mock(BlockProvider.class);
		f_model.setBlockProvider(m_provider);
		RankPlayer l_player = new RankPlayer();
		// when
		f_model.loadModel(l_player);
		// then
		verify(m_provider).loadDataBaseListExt(eq("Select * From games order by Player_ID, Date, Tournament_ID, Round"), (ISqlSelectorCallBack)eq(null), eq(-1l));
	}

	@Test
	public void testAddModelDataListOfRankGame() {
		// given
		List<RankGame> l_games = Arrays.asList(new RankGame(12, 3, 0, Color.BLACK, 5), new RankGame(13, 0, 7, Color.WHITE, 6));
		RankPlayer l_player = new RankPlayer();
		for (RankGame l_game : l_games) {
			l_game.setPlayer(l_player);
		}
		// when
		f_model.addModelData(l_games );
		// then
		List<RankGame> l_storedGames = f_model.getContentAsList(l_player);
		assertEquals(2, l_storedGames.size());
		assertEquals(3, f_model.getElement(12).getHandicap());
	}

	@Test
	public void testGetContentAsList() throws Exception {
		// given
		RankPlayer l_player = new RankPlayer();
		RankPlayer l_player2 = new RankPlayer();
		RankPlayer l_player3 = new RankPlayer();
		RankPlayer[] l_players = new RankPlayer[] { l_player, l_player2, l_player3 };
		List<RankGame> l_games = new ArrayList<RankGame>();
		int l_gameIndex = 24;
		for (int index = 0; index != 2; index++) {
			for (int index2 = index+1; index2 != 3; index2++ ) {
				RankGame l_game1 = new RankGame(l_gameIndex++, 0, 7, Color.WHITE, 5);
				RankGame l_game2 = new RankGame(l_gameIndex++, 0, 7, Color.BLACK, 6);
				l_game1.setPlayer(l_players[index]);
				l_game2.setOpponent(l_players[index]);
				l_game2.setPlayer(l_players[index2]);
				l_game1.setOpponent(l_players[index2]);
				l_games.add(l_game1);
				l_games.add(l_game2);
			}
		}
		// when
		f_model.addModelData(l_games );
		List<RankGame> l_gamesRet1 = f_model.getContentAsList(l_player);
		List<RankGame> l_gamesRet2 = f_model.getContentAsList(l_player2);
		List<RankGame> l_gamesRet3 = f_model.getContentAsList(l_player3);
		// then
		assertEquals(2, l_gamesRet1.size());
		assertTrue(l_gamesRet1.contains(l_games.get(0)));
		assertTrue(l_gamesRet1.contains(l_games.get(2)));
		assertEquals(2, l_gamesRet2.size());
		assertTrue(l_gamesRet2.contains(l_games.get(1)));
		assertTrue(l_gamesRet2.contains(l_games.get(4)));
		assertEquals(2, l_gamesRet3.size());
		assertTrue(l_gamesRet3.contains(l_games.get(3)));
		assertTrue(l_gamesRet3.contains(l_games.get(5)));
	}

	@Test
	public void testGetAGAElementRankPlayerlong() {
		// given
		RankPlayer l_player = new RankPlayer();
		RankPlayer l_player2 = new RankPlayer();
		RankPlayer l_player3 = new RankPlayer();
		RankPlayer[] l_players = new RankPlayer[] { l_player, l_player2, l_player3 };
		List<RankGame> l_games = new ArrayList<RankGame>();
		int l_gameIndex = 24;
		int l_agaGameIndex = 1500;
		for (int index = 0; index != 2; index++) {
			for (int index2 = index+1; index2 != 3; index2++ ) {
				RankGame l_game1 = new RankGame(l_gameIndex++, 0, 7, Color.WHITE, l_agaGameIndex);
				RankGame l_game2 = new RankGame(l_gameIndex++, 0, 7, Color.BLACK, l_agaGameIndex++);
				l_game1.setPlayer(l_players[index]);
				l_game2.setOpponent(l_players[index]);
				l_game2.setPlayer(l_players[index2]);
				l_game1.setOpponent(l_players[index2]);
				l_games.add(l_game1);
				l_games.add(l_game2);
			}
		}
		// when
		f_model.addModelData(l_games );
		// then
		assertEquals(l_games.get(0), f_model.getAGAElement(l_player, 1500));
		assertEquals(l_games.get(1), f_model.getAGAElement(l_player2, 1500));
		assertEquals(l_games.get(2), f_model.getAGAElement(l_player, 1501));
		assertEquals(l_games.get(3), f_model.getAGAElement(l_player3, 1501));
		assertEquals(l_games.get(4), f_model.getAGAElement(l_player2, 1502));
		assertEquals(l_games.get(5), f_model.getAGAElement(l_player3, 1502));
		assertNull(f_model.getAGAElement(l_player, 1502));
	}
}
