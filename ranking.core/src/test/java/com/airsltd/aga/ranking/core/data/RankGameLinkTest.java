package com.airsltd.aga.ranking.core.data;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.util.Arrays;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.airsltd.aga.ranking.core.model.GameModel;
import com.airsltd.aga.ranking.core.model.PlayerModel;

public class RankGameLinkTest {

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
	public void testToStringCsv() {
		// given
		new GameModel();
		String[] l_stringIn = new String[] {
				"15",
				"14"
		};
		String[] l_stringOut = new String[] {
				"15",
				"14"
		};
		RankGame l_game1 = new RankGame(2802,0,8,Color.BLACK,1502);
		l_game1.setId(15);
		RankGame l_game2 = new RankGame(2803,0,8,Color.WHITE,1502);
		l_game2.setId(14);
		GameModel.getInstance().addModelData(Arrays.asList(l_game1, l_game2));
		PlayerModel.setInstance(mock(PlayerModel.class));
		RankPlayer m_player = new RankPlayer(2802);
		given(PlayerModel.getInstance().getElement(2802l)).willReturn(m_player);
		RankPlayer m_player2 = new RankPlayer(2803);
		given(PlayerModel.getInstance().getElement(2803l)).willReturn(m_player2);
		RankGameLink l_link = new RankGameLink();
		// when
		l_link.fromStringCsv(l_stringIn);
		// then
		assertArrayEquals(l_stringOut, l_link.toStringCsv());
	}
	
	@Test
	public void testGetPersistentId() {
		// given
		RankGame l_game1 = new RankGame(2802,0,8,Color.BLACK,1502);
		l_game1.setId(15);
		RankGame l_game2 = new RankGame(2803,0,8,Color.WHITE,1502);
		l_game2.setId(14);
		PlayerModel.setInstance(mock(PlayerModel.class));
		RankPlayer m_player = new RankPlayer(2802);
		given(PlayerModel.getInstance().getElement(2802l)).willReturn(m_player);
		RankPlayer m_player2 = new RankPlayer(2803);
		given(PlayerModel.getInstance().getElement(2803l)).willReturn(m_player2);
		// when
		RankGameLink l_link = new RankGameLink(l_game1, l_game2);
		// then
		assertEquals(15l, l_link.getPersistentID());
	}
	@Test
	public void testPrimaryKeyFields() {
		// given
		RankGame l_game1 = new RankGame(2802,0,8,Color.BLACK,1502);
		l_game1.setId(15);
		RankGame l_game2 = new RankGame(2803,0,8,Color.WHITE,1502);
		l_game2.setId(14);
		PlayerModel.setInstance(mock(PlayerModel.class));
		RankPlayer m_player = new RankPlayer(2802);
		given(PlayerModel.getInstance().getElement(2802l)).willReturn(m_player);
		RankPlayer m_player2 = new RankPlayer(2803);
		given(PlayerModel.getInstance().getElement(2803l)).willReturn(m_player2);
		// when
		RankGameLink l_link = new RankGameLink(l_game1, l_game2);
		// then
		assertEquals(1, l_link.primaryKeyFields());
	}

	
}
