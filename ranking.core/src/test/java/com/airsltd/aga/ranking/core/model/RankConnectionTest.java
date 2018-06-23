package com.airsltd.aga.ranking.core.model;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class RankConnectionTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	private RankConnection f_connection;

	@Before
	public void setUp() throws Exception {
		RankConnection.setInstance(null);
		f_connection = RankConnection.getInstance();
		// cc
		assertEquals(f_connection, RankConnection.getInstance());
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testTeardownConnection() {
		// given
		// when
		// then
		// cc
		f_connection.teardownConnection();
	}

	@Test
	public void testInitializeConnection() {
		// given
		// when
		// then
		// cc
		f_connection.initializeConnection();
	}

	@Test
	public void testGetUser() {
		// given
		// when
		// then
		assertEquals("agaRankUser", f_connection.getUser());
	}

	@Test
	public void testGetUrl() {
		// given
		// when
		// then
		assertEquals("jdbc:mysql://localhost/usgo_agarank?serverTimezone=America/Los_Angeles&zeroDateTimeBehavior=convertToNull" +
				"&characterEncoding=utf-8", f_connection.getUrl());
	}

	@Test
	public void testGetPassword() {
		// given
		// when
		// then
		assertEquals("agarank34", f_connection.getPassword());
	}

	@Test
	public void testGetMaxWait() {
		// given
		// when
		// then
		assertEquals(30, f_connection.getMaxWait());
	}

	@Test
	public void testGetMaxActive() {
		// given
		// when
		// then
		assertEquals(10, f_connection.getMaxActive());
	}

}
