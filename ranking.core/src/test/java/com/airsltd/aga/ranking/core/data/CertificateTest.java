package com.airsltd.aga.ranking.core.data;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.airsltd.aga.ranking.core.model.PlayerModel;

public class CertificateTest {

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
		String[] l_stringIn = new String[] {
				"2802",
				"4",
				"2012-05-04",
				"1"
		};
		String[] l_stringOut = new String[] {
				"2802",
				"4",
				"'2012-05-04'",
				"1"
		};
		PlayerModel.setInstance(mock(PlayerModel.class));
		RankPlayer m_player = mock(RankPlayer.class);
		given(PlayerModel.getInstance().getElement(2802l)).willReturn(m_player);
		given(m_player.getPersistentID()).willReturn(5l);
		Certificate l_certificate = new Certificate(0, 1, null, false);
		// when
		l_certificate.fromStringCsv(l_stringIn);
		// then
		assertArrayEquals(l_stringOut, l_certificate.toStringCsv());
	}

}
