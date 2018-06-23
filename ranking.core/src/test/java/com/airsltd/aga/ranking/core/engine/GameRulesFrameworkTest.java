package com.airsltd.aga.ranking.core.engine;

import static org.mockito.BDDMockito.*;
import static org.junit.Assert.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.airsltd.aga.ranking.core.data.Color;
import com.airsltd.aga.ranking.core.data.GameState;
import com.airsltd.aga.ranking.core.data.RankGame;
import com.airsltd.aga.ranking.core.data.RankGameExt;
import com.airsltd.aga.ranking.core.model.GameExtModel;


public class GameRulesFrameworkTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	private GameEngine f_engine;
	private List<IRule<GameState, RankGame>> f_rules;
	private Date f_startDate;
	private Date f_endDate;

	@Before
	public void setUp() throws Exception {
		f_startDate = Date.valueOf(LocalDate.of(2017, 1, 1));
		f_endDate = Date.valueOf(LocalDate.of(2017, 12, 30));
		f_engine = new GameEngine(f_startDate, f_endDate);
		f_rules = f_engine.getRules();
		Collections.sort(f_rules);
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testRuleOrder() throws Exception {
		//given
		String[] l_ruleNames = new String[] {
				"Collapsed",
				"OutOfOrder",
				"InvalidHandicap",
				"GameNotReady",
				"AfterDate",
				"BeforeDate",
				"Threshold"
		};
		// when
		// then
		assertEquals(l_ruleNames.length, f_rules.size());
		int l_index = 0;
		for (String l_expected : l_ruleNames) {
			assertEquals(l_expected, ((AbstractRule<GameState, RankGame>) f_rules.get(l_index++)).getName());
		}
	}
	
	@Test
	public void testInvalidHandicap() throws Exception {
		//given
		
		InvalidHandicapRule l_rule = new InvalidHandicapRule();
		RankGame[] l_pass = new RankGame[] { mock(RankGame.class) };
		RankGame[] l_fail = new RankGame[] { mock(RankGame.class) };
		given(l_pass[0].getHandicap()).willReturn(7);
		given(l_fail[0].getHandicap()).willReturn(10);
		// when
		// then
		ruleVerify(l_rule, l_pass, l_fail, GameState.UNKNOWN, GameState.REJECTED);
	}

	protected void ruleVerify(GameRule p_rule, RankGame[] p_pass,
			RankGame[] p_fail, GameState p_passState, GameState p_failState) {
		int l_index = 1;
		for (RankGame l_passTest : p_pass) {
			assertEquals("Fail on Pass test:" + l_index, p_passState, p_rule.processGame(l_passTest));
			l_index++;
		}
		l_index = 1;
		for (RankGame l_failTest : p_fail) {
			assertEquals("Fail on Fail test:" + l_index, p_failState, p_rule.processGame(l_failTest));
			l_index++;
		}
	}
	
	@Test
	public void testNoTrailingRating() throws Exception {
		//given
		GameExtModel.setInstance(mock(GameExtModel.class));
		GameRule l_rule = new GameNotReadyRule();
		RankGame l_game1 = new RankGame(5, 0, 7, Color.BLACK, 32);
		RankGame l_game2 = new RankGame(6, 0, 7, Color.WHITE, 34);
		RankGameExt l_gameExt1 = mock(RankGameExt.class);
		RankGameExt l_gameExt2 = mock(RankGameExt.class);
		given(l_gameExt1.getTrailingQuality()).willReturn(3.2d);
		given(l_gameExt2.getTrailingQuality()).willReturn(null);
		given(GameExtModel.getInstance().getElement(l_game1)).willReturn(l_gameExt1);
		given(GameExtModel.getInstance().getElement(l_game2)).willReturn(l_gameExt2);
		RankGame[] l_pass = new RankGame[] {l_game1};
		RankGame[] l_fail = new RankGame[] {l_game2};
		// when
		// then
		ruleVerify(l_rule, l_pass, l_fail, GameState.UNKNOWN, GameState.NOTREADY);
	}
	
	@Test
	public void testAfterDate() throws Exception {
		//given
		GameRule l_rule = new GameAfterDateRule(f_startDate);
		RankGame l_gamePass = mock(RankGame.class);
		RankGame l_gameFail = mock(RankGame.class);
		given(l_gamePass.getDate()).willReturn(Date.valueOf(LocalDate.of(2017, 1, 1)),
				Date.valueOf(LocalDate.of(2018, 5,5)));
		given(l_gameFail.getDate()).willReturn(Date.valueOf(LocalDate.of(2016, 12, 31)),
				Date.valueOf(LocalDate.of(2010, 5,5)));
		RankGame[] l_pass = new RankGame[] { l_gamePass, l_gamePass };
		RankGame[] l_fail = new RankGame[] { l_gameFail, l_gameFail };
		// when
		// then
		ruleVerify(l_rule, l_pass, l_fail, GameState.UNKNOWN, GameState.REJECTED);
	}
	
	@Test
	public void testProcessTill() throws Exception {
		//given
		GameRule l_rule = new GameBeforeDate(f_endDate);
		RankGame l_gamePass = mock(RankGame.class);
		RankGame l_gameFail = mock(RankGame.class);
		given(l_gamePass.getDate()).willReturn(Date.valueOf(LocalDate.of(2017, 1, 1)),
				Date.valueOf(LocalDate.of(2017, 5,5)));
		given(l_gameFail.getDate()).willReturn(Date.valueOf(LocalDate.of(2018, 1, 1)),
				Date.valueOf(LocalDate.of(2017, 12, 31)));
		RankGame[] l_pass = new RankGame[] { l_gamePass, l_gamePass };
		RankGame[] l_fail = new RankGame[] { l_gameFail, l_gameFail };
		// when
		// then
		ruleVerify(l_rule, l_pass, l_fail, GameState.UNKNOWN, GameState.NOTREADY);
	}
	
	@Test
	public void testQualityThreshold() throws Exception {
		//given
		GameExtModel.setInstance(mock(GameExtModel.class));
		GameRule l_rule = new GameThresholdRule(.05f);
		RankGame l_gamePass = new RankGame(5, 0, 7, Color.BLACK, 32);
		RankGame l_gameFail = new RankGame(6, 0, 7, Color.WHITE, 34);
		RankGameExt l_gamePassExt = mock(RankGameExt.class);
		RankGameExt l_gameFailExt = mock(RankGameExt.class);
		given(l_gamePassExt.getProbability()).willReturn(.051d, .949d, .03d);
		given(l_gamePassExt.getTrailingProbability()).willReturn(.52d);
		given(l_gameFailExt.getProbability()).willReturn(.03d, .97d);
		given(l_gameFailExt.getTrailingProbability()).willReturn(.02d, .99d);
		RankGame[] l_pass = new RankGame[] { l_gamePass, l_gamePass, l_gamePass };
		RankGame[] l_fail = new RankGame[] { l_gameFail, l_gameFail, l_gameFail, l_gameFail };
		given(GameExtModel.getInstance().getElement(l_gamePass)).willReturn(l_gamePassExt);
		given(GameExtModel.getInstance().getElement(l_gameFail)).willReturn(l_gameFailExt);
		// when
		// then
		ruleVerify(l_rule, l_pass, l_fail, GameState.VALID, GameState.REJECTED);
	}
}
