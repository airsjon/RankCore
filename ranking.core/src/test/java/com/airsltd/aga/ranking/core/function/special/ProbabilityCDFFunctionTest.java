package com.airsltd.aga.ranking.core.function.special;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.airsltd.aga.ranking.core.data.Color;
import com.airsltd.aga.ranking.core.data.RankGame;
import com.airsltd.aga.ranking.core.data.RankGameExt;
import com.airsltd.aga.ranking.core.model.GameExtModel;
import com.airsltd.aga.ranking.core.model.GameModel;
import com.airsltd.aga.ranking.core.states.GameResult;
import com.airsltd.core.data.Tuple;

public class ProbabilityCDFFunctionTest {

	private ProbabilityCDFFunction f_probabilityCDF;

	@Before
	public void setUp() throws Exception {
		f_probabilityCDF = new ProbabilityCDFFunction() {
			
			@Override
			protected boolean checkThreshold(double p_gameRating) {
				return p_gameRating < -5f;
			}
			
			@Override
			protected int atThreshold() {
				return -1000;
			}

			@Override
			protected boolean getDirection() {
				return true;
			}
		};
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testEvaluate() {
		// given
		GameExtModel.setInstance(mock(GameExtModel.class));
		RankGame l_gameWon1 = new RankGame(5l, 0, 8, Color.WHITE, 52);
		RankGame l_gameWon2 = new RankGame(6l, 0, 8, Color.BLACK, 53);
		RankGame l_gameWon3 = new RankGame(7l, 0, 8, Color.WHITE, 54);
		RankGame l_gameWon4 = new RankGame(8l, 0, 8, Color.WHITE, 55);
		RankGameExt l_gameWonExt1 = new RankGameExt(l_gameWon1);
		RankGameExt l_gameWonExt2 = new RankGameExt(l_gameWon2);
		RankGameExt l_gameWonExt3 = new RankGameExt(l_gameWon3);
		RankGameExt l_gameWonExt4 = new RankGameExt(l_gameWon4);
		given(GameExtModel.getInstance().getElement(l_gameWon1)).willReturn(l_gameWonExt1);
		given(GameExtModel.getInstance().getElement(l_gameWon2)).willReturn(l_gameWonExt2);
		given(GameExtModel.getInstance().getElement(l_gameWon3)).willReturn(l_gameWonExt3);
		given(GameExtModel.getInstance().getElement(l_gameWon4)).willReturn(l_gameWonExt4);
		l_gameWon1.setResult(GameResult.PLAYERWON);
		l_gameWon2.setResult(GameResult.PLAYERWON);
		l_gameWon3.setResult(GameResult.PLAYERWON);
		l_gameWon4.setResult(GameResult.PLAYERWON);
		l_gameWonExt1.setOpponentsTrailingRating(4.5d);
		l_gameWonExt2.setOpponentsTrailingRating(4.6d);
		l_gameWonExt3.setOpponentsTrailingRating(4.3d);
		l_gameWonExt4.setOpponentsTrailingRating(4.2d);
		MovingAverageValue<RankGame> l_gamesAllWins = new MovingAverageValue<RankGame>(4);
		l_gamesAllWins.addValue(l_gameWon1);
		l_gamesAllWins.addValue(l_gameWon2);
		l_gamesAllWins.addValue(l_gameWon3);
		l_gamesAllWins.addValue(l_gameWon4);
		
		// when
		f_probabilityCDF.evaluate(l_gamesAllWins);
	}

	@Test
	public void testMaxRating() {
		// given
		// when
		// then
		assertEquals(3.2d, f_probabilityCDF.maxRating(new double[] { 1.2d, -4.5d, 3.1d, 3.2d, -5.6d, 7.9d }),.01d);
	}
	
	@Test(expected=ArrayIndexOutOfBoundsException.class)
	public void testMaxRatingFail() {
		// given
		// when
		// then
		f_probabilityCDF.maxRating(new double[] {});
	}

	@Test
	public void testMinRating() {
		// given
		// when
		// then
		assertEquals(-5.6d, f_probabilityCDF.minRating(new double[] { 1.2d, -4.5d, 3.1d, 3.2d, -5.6d, -7.2d }),.01d);
	}
	
	@Test(expected=ArrayIndexOutOfBoundsException.class)
	public void testMinRatingFail() {
		// given
		// when
		// then
		f_probabilityCDF.minRating(new double[] {});
	}

	@Test
	public void testNeedsLimit() {
		// given
		MovingAverageValue<RankGame> p_gamesEmpty = new MovingAverageValue<>(6);
		MovingAverageValue<RankGame> p_gamesWins = new MovingAverageValue<>(6);
		MovingAverageValue<RankGame> p_gamesLosses = new MovingAverageValue<>(6);
		MovingAverageValue<RankGame> p_gamesMixed = new MovingAverageValue<>(6);
		RankGame l_gameWon = new RankGame(3,0,7,Color.WHITE,450);
		l_gameWon.setResult(GameResult.PLAYERWON);
		RankGame l_gameLost = new RankGame(4,0,7,Color.WHITE,550);
		l_gameLost.setResult(GameResult.PLAYERLOST);
		p_gamesWins.addValue(l_gameWon);
		p_gamesWins.addValue(l_gameWon);
		p_gamesWins.addValue(l_gameWon);
		p_gamesWins.addValue(l_gameWon);
		p_gamesWins.addValue(l_gameWon);
		p_gamesWins.addValue(l_gameWon);
		p_gamesLosses.addValue(l_gameLost);
		p_gamesLosses.addValue(l_gameLost);
		p_gamesLosses.addValue(l_gameLost);
		p_gamesLosses.addValue(l_gameLost);
		p_gamesLosses.addValue(l_gameLost);
		p_gamesLosses.addValue(l_gameLost);
		p_gamesMixed.addValue(l_gameWon);
		p_gamesMixed.addValue(l_gameWon);
		p_gamesMixed.addValue(l_gameLost);
		p_gamesMixed.addValue(l_gameLost);
		p_gamesMixed.addValue(l_gameWon);
		p_gamesMixed.addValue(l_gameWon);
		// when
		// then
		assertNull(f_probabilityCDF.needsLimit(p_gamesEmpty));
		assertEquals(LimitSet.LOSTALL, f_probabilityCDF.needsLimit(p_gamesLosses));
		assertEquals(LimitSet.WONALL, f_probabilityCDF.needsLimit(p_gamesWins));
		assertEquals(LimitSet.MIXED, f_probabilityCDF.needsLimit(p_gamesMixed));
	}

	@Test
	public void testGetCDF() {
		fail("Not yet implemented");
	}

	@Test
	public void testLimitedProbability() {
		fail("Not yet implemented");
	}

	@Test
	public void testInvert() {
		// given
		double[] l_testDoubles = new double[20];
		for (int l_index = 1; l_index != 20; l_index++) {
			l_testDoubles[l_index] = l_index / 20.d;
		}
		// when
		f_probabilityCDF.invert(l_testDoubles);
		// then
		for (int l_index = 1; l_index != 20; l_index++) {
			assertEquals(l_testDoubles[l_index],1d - (l_index / 20.d), .001d);
		}
		
	}

	@Test
	public void testDetermineOffset() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testGetCDFForOneGame() {
		// given
		new GameModel();
		RankGame[] l_games = new RankGame[] { new RankGame(3, 2, 0, Color.BLACK, 52) };
		double[] l_rating = new double[] { 3.2d };
		double[] l_cdf = new double[2221];
		// when
		Tuple<Double, Double> l_retVal = f_probabilityCDF.getCDF(l_games, l_rating, l_cdf);
		// then
		assertEquals(-2.2d, l_retVal.getPrimary(), .0001d);
		assertEquals(20d, l_retVal.getSecondary(), .0001d);
	}

	@Test
	public void testGetCDFForTwoGameS() {
		// given
		new GameModel();
		RankGame[] l_games = new RankGame[] { new RankGame(3, 2, 0, Color.BLACK, 52),
				new RankGame(4, 0, 7, Color.BLACK, 53)};
		l_games[0].setResult(GameResult.PLAYERWON);
		l_games[1].setResult(GameResult.PLAYERLOST);
		double[] l_rating = new double[] { 3.2d, 1.5d };
		double[] l_cdf = new double[2221];
		// when
		Tuple<Double, Double> l_retVal = f_probabilityCDF.getCDF(l_games, l_rating, l_cdf);
		// then
		assertEquals(-3.9d, l_retVal.getPrimary(), .0001d);
		assertEquals(8.6d, l_retVal.getSecondary(), .0001d);
	}

	@Test
	public void testGetCDF3For3Even() {
		// given
		new GameModel();
		double[] l_primary = { -13.2d, -12.2d, -11.2d, -10.2d, -9.2d, -8.2d, -7.2d, -6.2d, -5.2d, -4.2d, -3.2d, -2.2d, -1.2d, -.2d, .8d, 1.8d} ;
		double[] l_secondary = { -2.9d, -1.9d, -.9d, .1d, 1.1d, 2.1d, 3.1d, 4.1d, 5.1d, 6.1d, 7.1d, 8.1d, 9.1d, 10.1d, 11.1d, 12.1d, 13.1d};
		RankGame[] l_games = new RankGame[] { new RankGame(3, 0, 7, Color.BLACK, 52),
				new RankGame(4, 0, 7, Color.BLACK, 53),
				new RankGame(5, 0, 7, Color.BLACK, 54),
				new RankGame(6, 0, 7, Color.WHITE, 55),
				new RankGame(7, 0, 7, Color.WHITE, 56),
				new RankGame(8, 0, 7, Color.WHITE, 57)
				};
		l_games[0].setResult(GameResult.PLAYERWON);
		l_games[1].setResult(GameResult.PLAYERLOST);
		l_games[2].setResult(GameResult.PLAYERWON);
		l_games[3].setResult(GameResult.PLAYERLOST);
		l_games[4].setResult(GameResult.PLAYERWON);
		l_games[5].setResult(GameResult.PLAYERLOST);
		double[] l_cdf = new double[2221];
		// when
		for (int cnt = -8; cnt != 8; cnt++) {
			double[] l_rating = new double[] { cnt - .3d, cnt - .2d, cnt - .1d,
					cnt + 0d, cnt + .1d, cnt + .2d };
			Tuple<Double, Double> l_retVal = f_probabilityCDF.getCDF(l_games, l_rating, l_cdf);
			// then
			assertEquals(l_primary[cnt+8], l_retVal.getPrimary(), .0001d);
			assertEquals(l_secondary[cnt+8], l_retVal.getSecondary(), .0001d);
		}
	}
	
}
