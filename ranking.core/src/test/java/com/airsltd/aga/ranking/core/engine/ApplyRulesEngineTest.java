package com.airsltd.aga.ranking.core.engine;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.airsltd.aga.ranking.core.data.GameState;
import com.airsltd.aga.ranking.core.data.RankGame;

public class ApplyRulesEngineTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	private ApplyRulesEngine<GameState,RankGame> f_engine;
	private List<IRule<GameState,RankGame>> f_rules;

	@Before
	@SuppressWarnings("unchecked")
	public void setUp() throws Exception {
		IRule<GameState,RankGame> m_rule1 = mock(IRule.class);
		IRule<GameState,RankGame> m_rule2 = mock(IRule.class);
		GameIterator l_iterator = mock(GameIterator.class);
		given(l_iterator.hasNext()).willReturn(true, true, true, true, true, false);
		RankGame l_game = new RankGame();
		Deque<RankGame> l_games = new ArrayDeque<RankGame>(Arrays.asList(l_game,l_game,l_game,l_game,l_game));
		given(l_iterator.getGames()).willReturn(l_games);
		given(m_rule1.process(any(RankGame.class))).willReturn(true, false, true, false, true);
		given(m_rule2.process(any(RankGame.class))).willReturn(false, true);
		given(m_rule1.getIndex()).willReturn(1);
		given(m_rule2.getIndex()).willReturn(2);
		f_rules = Arrays.asList(m_rule1, m_rule2);
		f_engine = new ApplyRulesEngine<GameState,RankGame>(f_rules, l_iterator);
	}

	@After
	public void tearDown() throws Exception {
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetterSetter() {
		// given
		List<IRule<GameState,RankGame>> l_rules = mock(List.class);
		// when
		f_engine.setRules(l_rules);
		// then
		assertEquals(l_rules, f_engine.getRules());
	}

	@Test
	public void testProcess() {
		// given
		// when
		f_engine.process();
		// then
		assertEquals("Missed: 1"+System.lineSeparator()+
					"0[1]: 3"+System.lineSeparator()+
					"0[2]: 1"+System.lineSeparator()+
					"", f_engine.dumpCounts());
		verify(f_rules.get(0),times(5)).process(any(RankGame.class));
		verify(f_rules.get(1),times(2)).process(any(RankGame.class));
	}

}
