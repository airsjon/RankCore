package com.airsltd.aga.ranking.core.data;

import static org.junit.Assert.*;

import java.sql.Date;
import java.util.GregorianCalendar;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ViewCertificateTest {

	private ViewCertificate f_certificate;

	@Before
	public void setUp() throws Exception {
		f_certificate = new ViewCertificate();
		f_certificate.setEmail("email1@null.org");
		f_certificate.setEmail2("email2@null.org");
		f_certificate.setFullName("Family Name, Given Name");
		f_certificate.setPinPlayer(90003);
		f_certificate.setPlayerId(321321);
		f_certificate.setRank(5);
		f_certificate.setRunDate(new Date(new GregorianCalendar(2017,8,10).getTimeInMillis()));
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testPrettyRank() {
		// given
		// when
		// then
		assertEquals("5 Dan", f_certificate.prettyRank());
		// when
		f_certificate.setRank(0);
		// then
		assertEquals("1 Kyu", f_certificate.prettyRank());
		// when
		f_certificate.setRank(-5);
		assertEquals("6 Kyu", f_certificate.prettyRank());
	}

	@Test
	public void testPdfFile() {
		// given
		// when
		// then
		assertEquals("GivenNameFamilyName90003.pdf", f_certificate.pdfFile());
	}

	@Test
	public void testFixName() {
		// given
		// when
		// then
		assertEquals("Given Name Family Name", f_certificate.fixName());
	}

}
