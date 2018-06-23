package com.airsltd.aga.ranking.core.model;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class RatingModelTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	private RatingModel f_model;

	@Before
	public void setUp() throws Exception {
		RatingModel.setInstance(null);
		f_model = RatingModel.getInstance();
		//cc
		assertEquals(f_model, RatingModel.getInstance());
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetSelectionQuery() {
		// given
		// when
		// then
		assertEquals("Select * From ratings order by Player_ID, Date", 
				f_model.getSelectionQuery(null));
	}

}
