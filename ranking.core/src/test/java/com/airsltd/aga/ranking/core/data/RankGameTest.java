package com.airsltd.aga.ranking.core.data;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;
import java.sql.Date;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.airsltd.aga.ranking.core.model.GameModel;
import com.airsltd.aga.ranking.core.model.PlayerModel;
import com.airsltd.aga.ranking.core.model.TournamentModel;
import com.airsltd.aga.ranking.core.states.GameResult;
import com.airsltd.core.data.IBlockData;
import com.airsltd.core.model.BlockModel;

public class RankGameTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		PlayerModel.setInstance(mock(PlayerModel.class));
		TournamentModel.setInstance(mock(TournamentModel.class));
		BlockModel.setFromIdModel(RankPlayer.class, PlayerModel.getInstance());
		BlockModel.setFromIdModel(RankTournament.class, TournamentModel.getInstance());
		new RankGame().loadFields();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		PlayerModel.setInstance(null);
		TournamentModel.setInstance(null);
		BlockModel.setFromIdModel(RankPlayer.class, null);
		BlockModel.setFromIdModel(RankTournament.class, null);
	}

	private RankGame f_rankGame;

	@Before
	public void setUp() throws Exception {
		f_rankGame = new RankGame();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testTableName() {
		// given
		// when
		// then
		assertEquals("games", f_rankGame.tableName());
	}
	
	@Test
	public void testAutoIncrements() {
		// given
		// when
		// then
		assertEquals(1l, f_rankGame.autoIncrements());
	}
	
	@Test
	public void testKeyFields() {
		// given
		// when
		// then
		assertEquals(1l, f_rankGame.keyFields());
	}
	
	@Test
	public void testAutoIncrementField() {
		// given
		// when
		f_rankGame.autoIncrementField(1, 32l);
		f_rankGame.autoIncrementField(0, 44l);
		// then
		assertEquals(44l, f_rankGame.getPersistentID());
	}

	@Test
	public void testCopy() {
		// given
		String[] l_stringIn = new String[] {
				"3",
				"5",
				"2012-05-04",
				"721",
				"3",
				"52",
				"0",
				"4",
				"5",
				"1",
				"47",
				"43"
		};
		RankPlayer m_player = mock(RankPlayer.class);
		RankTournament m_tournament = mock(RankTournament.class);
		given(PlayerModel.getInstance().getElement(5l)).willReturn(m_player);
		RankPlayer m_playerOpponent = mock(RankPlayer.class);
		given(PlayerModel.getInstance().getElement(52l)).willReturn(m_playerOpponent);
		given(TournamentModel.getInstance().getElement(721l)).willReturn(m_tournament);
		given(m_player.getPersistentID()).willReturn(5l);
		given(m_playerOpponent.getPersistentID()).willReturn(52l);
		given(m_tournament.getPersistentID()).willReturn(721l);
		f_rankGame.fromStringCsv(l_stringIn);
		// when
		RankGame l_game1 = new RankGame(f_rankGame);
		IBlockData l_game2 = f_rankGame.copy();
		// then
		assertEquals(f_rankGame, l_game1);
		assertEquals(f_rankGame, l_game2);
		assertEquals(l_game2, l_game1);
		assertFalse(f_rankGame==l_game1);
		assertFalse(f_rankGame==l_game2);
		assertFalse(l_game2==l_game1);
	}

	@Test
	public void testPrimaryKeyFields() {
		// given
		// when
		// then
		assertEquals(4098l, f_rankGame.primaryKeyFields());
	}

	@Test
	public void testGetPersistentID() {
		// given
		GameModel.setInstance(mock(GameModel.class));
		// when
		// then
		assertEquals(-1l, f_rankGame.getPersistentID());
		// when
		f_rankGame.setId(32l);
		// then
		assertEquals(32l, f_rankGame.getPersistentID());
	}

	@Test
	public void testCompareTo() {
		// given
		RankGame l_game = new RankGame();
		// when
		// then
		assertEquals(Integer.compare(f_rankGame.getInternalId().hashCode(), l_game.getInternalId().hashCode()), f_rankGame.compareTo(l_game));
		// when
		l_game.setId(32);
		f_rankGame.setId(20);
		// then
		assertTrue(l_game.compareTo(f_rankGame)>0);
		// when
		l_game.setDate(new Date(new java.util.Date().getTime()));
		// then
		assertEquals(1, l_game.compareTo(f_rankGame));
	}

	@Test
	public void testToRank() {
		// given
		// when
		// then
		assertEquals(3, RankGame.toRank(3.01f));
		assertEquals(3, RankGame.toRank(3.0f));
		assertEquals(2, RankGame.toRank(2.999f));
		assertEquals(2, RankGame.toRank(2.01f));
		assertEquals(2, RankGame.toRank(2.0f));
		assertEquals(1, RankGame.toRank(1.999f));
		assertEquals(1, RankGame.toRank(1.01f));
		assertEquals(1, RankGame.toRank(1.0f));
		assertEquals(0, RankGame.toRank(.999f));
		assertEquals(0, RankGame.toRank(.01f));
		assertEquals(0, RankGame.toRank(.0f));
		assertEquals(-1, RankGame.toRank(-.0001f));
		assertEquals(-1, RankGame.toRank(-.999f));
		assertEquals(-1, RankGame.toRank(-1.0f));
		assertEquals(-2, RankGame.toRank(-1.0001f));

	}
	
	@Test
	public void testToStrength() {
		// when
		f_rankGame.setHandicap(3);
		f_rankGame.setKomi(0);
		f_rankGame.setColor(Color.BLACK);
		// then
		assertEquals(.2d, (double)f_rankGame.toStrength(3.2f),.0001d);
		assertEquals(-6.2d, (double)f_rankGame.toStrength(-3.2f),.0001d);
		// when
		f_rankGame.setKomi(3);
		// then
		assertEquals(.4271d, (double)f_rankGame.toStrength(3.2f),.0001d);
		assertEquals(-5.9729, (double)f_rankGame.toStrength(-3.2f),.0001d);
		// when
		f_rankGame.setKomi(7);
		// then
		assertEquals(.7299d, (double)f_rankGame.toStrength(3.2f),.0001d);
		// when
		f_rankGame.setKomi(-7);
		// then
		assertEquals(-.3299d, (double)f_rankGame.toStrength(3.2f),.0001d);
		// when
		f_rankGame.setColor(Color.WHITE);
		// then
		assertEquals(6.7299d, (double)f_rankGame.toStrength(3.2f),.0001d);
		// when
		f_rankGame.setHandicap(0);
		f_rankGame.setKomi(7);
		// then
		assertEquals(3.2501d, (double)f_rankGame.toStrength(3.2f), .0001d);
	}

	@Test
	public void testToStringCsv() {
		// given
		String[] l_stringIn = new String[] {
				"3",
				"5",
				"2012-05-04",
				"721",
				"3",
				"52",
				"0",
				"4",
				"5",
				"1",
				"47",
				"43",
				"2",
				"3",
				"test"
		};
		String[] l_stringOut = new String[] {
				"3",
				"5",
				"'2012-05-04'",
				"721",
				"3",
				"52",
				"0",
				"4",
				"5",
				"1",
				"'47'",
				"'43'",
				"2",
				"3",
				"'test'"
		};
		RankPlayer m_player = mock(RankPlayer.class);
		RankTournament m_tournament = mock(RankTournament.class);
		given(PlayerModel.getInstance().getElement(5l)).willReturn(m_player);
		RankPlayer m_playerOpponent = mock(RankPlayer.class);
		given(PlayerModel.getInstance().getElement(52l)).willReturn(m_playerOpponent);
		given(TournamentModel.getInstance().getElement(721l)).willReturn(m_tournament);
		given(m_player.getPersistentID()).willReturn(5l);
		given(m_playerOpponent.getPersistentID()).willReturn(52l);
		given(m_tournament.getPersistentID()).willReturn(721l);
		f_rankGame.fromStringCsv(l_stringIn);
		// when
		// then
		assertArrayEquals(l_stringOut, f_rankGame.toStringCsv());
	}

	@Test
	public void testCalculateSigma() {
		// given
		// when
		// then
		f_rankGame.setHandicap(0);
		f_rankGame.setKomi(7);
		assertEquals(1.056859f, f_rankGame.calculateSigma(),.0001f);
		f_rankGame.setKomi(0);
		assertEquals(1.0649f, f_rankGame.calculateSigma(),.0001f);
		f_rankGame.setKomi(-6);
		assertEquals(1.0834599f, f_rankGame.calculateSigma(),.0001f);
		f_rankGame.setHandicap(1);
		f_rankGame.setKomi(0);
		assertEquals(1.0649f, f_rankGame.calculateSigma(),.0001f);
		f_rankGame.setHandicap(2);
		assertEquals(1.13672f, f_rankGame.calculateSigma(),.0001f);
		f_rankGame.setKomi(-6);
		assertEquals(1.1578213f, f_rankGame.calculateSigma(),.0001f);
		f_rankGame.setHandicap(3);
		assertEquals(1.2090514f, f_rankGame.calculateSigma(),.0001f);
		f_rankGame.setKomi(0);
		assertEquals(1.18795f, f_rankGame.calculateSigma(),.0001f);
		f_rankGame.setHandicap(8);
		assertEquals(1.39782f, f_rankGame.calculateSigma(),.0001f);
		f_rankGame.setHandicap(9);
		assertEquals(1.43614f, f_rankGame.calculateSigma(),.0001f);
		f_rankGame.setKomi(7);
		assertEquals(1.4115217f, f_rankGame.calculateSigma(),.0001f);
		// when
		f_rankGame.setHandicap(10);
		f_rankGame.setKomi(0);
		// then
		assertEquals(0, f_rankGame.calculateSigma(), .0001f);
	}

	@Test
	public void testCalculateHandicapEquiv() {
		// given
		// when
		// then
		f_rankGame.setHandicap(0);
		f_rankGame.setKomi(7);
		assertEquals(0.05f, f_rankGame.calculateHandicapEquiv(),.0001f);
		f_rankGame.setKomi(0);
		assertEquals(.58f, f_rankGame.calculateHandicapEquiv(),.0001f);
		f_rankGame.setHandicap(1);
		assertEquals(.58f, f_rankGame.calculateHandicapEquiv(),.0001f);
		f_rankGame.setKomi(-6);
		assertEquals(1.0342f, f_rankGame.calculateHandicapEquiv(),.0001f);
		f_rankGame.setHandicap(2);
		f_rankGame.setKomi(0);
		assertEquals(2f, f_rankGame.calculateHandicapEquiv(),.0001f);
		f_rankGame.setKomi(-6);
		assertEquals(2.4542f, f_rankGame.calculateHandicapEquiv(),.0001f);
		f_rankGame.setHandicap(3);
		f_rankGame.setKomi(0);
		assertEquals(3f, f_rankGame.calculateHandicapEquiv(),.0001f);
		f_rankGame.setKomi(-6);
		assertEquals(3.4542f, f_rankGame.calculateHandicapEquiv(),.0001f);
		f_rankGame.setKomi(0);
		f_rankGame.setHandicap(8);
		assertEquals(8f, f_rankGame.calculateHandicapEquiv(),.0001f);
		f_rankGame.setHandicap(9);
		assertEquals(9f, f_rankGame.calculateHandicapEquiv(),.0001f);
		f_rankGame.setKomi(7);
		assertEquals(8.4701f, f_rankGame.calculateHandicapEquiv(),.0001f);

		/** output for documentation
		for (int stones = 0; stones!= 10; stones++) {
			if (stones != 1) {
				for (int komi = 7; komi != -7; komi--) {
					RankGame l_game = new RankGame(stones*15+komi+7, stones, komi, Color.WHITE,0);
					System.out.println(stones+", "+komi+", "+l_game.calculateHandicapEquiv());
				}
			}
		}
		**/
	}

	@Test
	public void testCaluculateEventProbability() {
		try {
			// given
			RankGame[] m_gameRecords = new RankGame[] {
					new RankGame(32,0,7,Color.WHITE,0),
					new RankGame(33,0,7,Color.WHITE,0),
					new RankGame(34,0,7,Color.BLACK,0),
					new RankGame(35,0,7,Color.BLACK,0)
			};
			boolean[] l_gameResults = new boolean[] {
					true, false, true, false };
			double[] l_gamePlayedAgainst = new double[] { 2.5f, 2.5f, 2.5f, 2.5f };
			int l_index = 0;
			for (RankGame l_game : m_gameRecords) {
				l_game.setResult(l_gameResults[l_index++]?GameResult.PLAYERWON:GameResult.PLAYERLOST);
			}
			// when
			// then
			for (int cnt = 0; cnt != 4; cnt++) {
//				System.out.print("Game "+cnt+": ");
				for (int l_rating = -10; l_rating != 60; l_rating++ ) {
					double l_val = m_gameRecords[cnt].calculateEventProbability(l_rating/10d, l_gamePlayedAgainst[cnt]);
//					System.out.format("%.2f ", l_val);
				}
//				System.out.println();
			}
		} finally {
		}

	}
	
	@Test
	public final void testCalculateEventProbabilityFloat() {
		// given
		RankGame l_rankGame = new RankGame();
		// when
		f_rankGame.setResult(GameResult.PLAYERWON);
		f_rankGame.setColor(Color.WHITE);
		l_rankGame.setResult(GameResult.PLAYERLOST);
		l_rankGame.setColor(Color.BLACK);
		for (int l_handi = 0; l_handi != 10; l_handi++) {
			f_rankGame.setHandicap(l_handi);
			f_rankGame.setKomi(l_handi==0?7:0);
			l_rankGame.setHandicap(l_handi);
			l_rankGame.setKomi(l_handi==0?7:0);
//			System.out.println("Handicap: " + l_handi + "Komi: " + f_rankGame.getKomi());
			// then
			int l_rating = 0;
			for (double l_double : f_rankGame.calculateEventProbabilty()) {
//				System.out.println("  "+ (-l_rating/100f)+" probability "+l_double);
				l_rating ++;
			}
		}
	}
	
	@Test
	public final void testCalculateEventProbabilityArray() {
		// given
		f_rankGame.setKomi(7);
		// when
		double[] l_doubles = f_rankGame.calculateEventProbabilityArray();
		// then
		assertEquals(.0001d, l_doubles[147], .00001d);
		assertEquals(.0001d, l_doubles[933], 1);
		assertEquals(.5d, l_doubles[540], .0001d);
	}
	
	@Test
	public final void testFixRank() {
		// given
		// when
		// then
		assertEquals(3.2f, RankGame.fixedRating(3.2f), .0001f);
		assertEquals(1f, RankGame.fixedRating(-1), .0001f);
		assertEquals(.7f, RankGame.fixedRating(-1.3f), .0001f);
	}
	
	@Test
	public final void testRealRating() {
		// given
		// when
		f_rankGame.setColor(Color.BLACK);
		f_rankGame.setHandicap(3);
		f_rankGame.setKomi(0);
		// then
		assertEquals(.2f,f_rankGame.realRating(3.2f),.001f);
		// when
		f_rankGame.setColor(Color.WHITE);
		// then
		assertEquals(6.2f,f_rankGame.realRating(3.2f),.001f);
	}
	
	@Test
	public final void testSegment() {
		// given
		// when
		// then
		assertEquals(f_rankGame.getPlayer(), f_rankGame.toSegment());
	}
	
	@Test
	public final void testCalculateProbability() {
		// given
		// when
		f_rankGame.setColor(Color.WHITE);
		f_rankGame.setHandicap(0);
		f_rankGame.setKomi(7);
		// then
		assertEquals(.4810f, f_rankGame.calculateProbability(3.5d, 3.5d),.0001f);
		// given
		// when
		f_rankGame.setColor(Color.BLACK);
		f_rankGame.setHandicap(0);
		f_rankGame.setKomi(7);
		// then
		assertEquals(.5189f, f_rankGame.calculateProbability(3.5d, 3.5d),.0001f);
		// given
		// when
		f_rankGame.setColor(Color.WHITE);
		f_rankGame.setHandicap(2);
		f_rankGame.setKomi(0);
		// then
		assertEquals(.0392f, f_rankGame.calculateProbability(3.5d, 3.5d),.0001f);
		assertEquals(.1895f, f_rankGame.calculateProbability(4.5d, 3.5d),.0001f);
		assertEquals(.5f, f_rankGame.calculateProbability(5.5d, 3.5d),.0001f);
		assertEquals(.8104f, f_rankGame.calculateProbability(6.5d, 3.5d),.0001f);
		// given
		// when
		f_rankGame.setColor(Color.BLACK);
		f_rankGame.setHandicap(2);
		f_rankGame.setKomi(0);
		// then
		assertEquals(.9607f, f_rankGame.calculateProbability(3.5d, 3.5d),.0001f);
		assertEquals(.8104f, f_rankGame.calculateProbability(3.5d, 4.5d),.0001f);
		assertEquals(.5f, f_rankGame.calculateProbability(3.5d, 5.5d),.0001f);
		assertEquals(.1895f, f_rankGame.calculateProbability(3.5d, 6.5d),.0001f);
	}
	
	@Test
	public void testEqual() throws Exception {
		//given
		// when
		RankGame l_game1 = new RankGame(4, 3, 0, Color.BLACK, 546);
		RankGame l_game2 = new RankGame(5, 3, 0, Color.BLACK, 546);
		// then
		assertEquals(l_game1, l_game2);
		// when
		l_game2.setAgaGameId(547);
		// then
		assertNotEquals(l_game1, l_game2);
	}

	@Test
	public void testSimiliar() throws Exception {
		//given
		RankGame l_game = new RankGame(72, 2, -6, Color.BLACK, 523);
		RankTournament m_tourn1 = mock(RankTournament.class);
		RankTournament m_tourn2 = mock(RankTournament.class);
		RankPlayer l_player1 = mock(RankPlayer.class);
		RankPlayer l_player2 = mock(RankPlayer.class);
		l_game.setTournament(m_tourn1);
		l_game.setOpponent(l_player1);
		RankGame l_match1 = new RankGame(73, 2, -6, Color.BLACK, 524);
		l_match1.setTournament(m_tourn1);
		l_match1.setOpponent(l_player1);
		RankGame[] l_similar = new RankGame[] {
				l_match1
		};
		RankGame l_match2 = new RankGame(l_match1);
		RankGame l_match3 = new RankGame(l_match1);
		RankGame l_match4 = new RankGame(l_match1);
		RankGame l_match5 = new RankGame(l_match1);
		RankGame l_match6 = new RankGame(l_match1);
		l_match2.setTournament(m_tourn2);
		l_match3.setHandicap(3);
		l_match4.setKomi(0);
		l_match5.setColor(Color.WHITE);
		l_match6.setOpponent(l_player2);
		RankGame[] l_notSame = new RankGame[] {
				l_match2,
				l_match3,
				l_match4,
				l_match5,
				l_match6
		};
		// when
		// then
		int l_index = 1;
		for (RankGame l_positive : l_similar) {
			assertTrue("Failed at: "+l_index++, l_game.similiar(l_positive));
		}
		l_index = 1;
		for (RankGame l_negative : l_notSame) {
			assertFalse("Failed at: "+l_index++, l_game.similiar(l_negative));
		}
	}

}
