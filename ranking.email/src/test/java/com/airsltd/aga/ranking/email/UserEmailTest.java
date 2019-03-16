package com.airsltd.aga.ranking.email;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.GregorianCalendar;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.AddressException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.airsltd.aga.ranking.core.data.ViewCertificate;
import com.airsltd.aga.ranking.email.UserEmail;

public class UserEmailTest {

	private static final Object CURRENTEMAIL = "Dear<fullname>,Congratulations.YouhavebeenawardedtheRankof<rank>bytheAmericanGoAssociation.AttachedisadraftoftheRankCertificateyouwereawardedon<runDate>.Ifyouhaveanycommentspleaseemailthemtojon@airsltd.comSincerely,JonBoleyRankCoordinatorAmericanGoAssociation";
	private UserEmail f_userEmail;

	@Before
	public void setUp() throws Exception {
		f_userEmail = new UserEmail(new String[] {});
		f_userEmail.setWorkDir("src/test/resources/");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testMain() {
		fail("Not yet implemented");
	}

	@Test
	public void testLoadEmailString() {
		// given
		// when
		String l_email = f_userEmail.loadEmailString();
		// then
		assertEquals(CURRENTEMAIL, l_email.replaceAll("\\s", ""));
	}
	@Test
	public void testEmailCertificate() {
		// given
		PreparedStatement m_statement = mock(PreparedStatement.class);
		ViewCertificate m_cert = mock(ViewCertificate.class);
		given(m_cert.pdfFile()).willReturn("mock.pdf");
		given(m_cert.prettyRank()).willReturn("4 Dan");
		given(m_cert.getEmail()).willReturn("jon@airsltd.com");
		given(m_cert.getFullName()).willReturn("Jon Boley");
		given(m_cert.fixName()).willReturn("Jon Boley");
		given(m_cert.getRunDate()).willReturn(new Date(new GregorianCalendar(2015,10,4).getTime().getTime()));
		f_userEmail.setStatement(m_statement);
		f_userEmail.setEmailString("Email to: <fullname> for <rank> on <runDate>");
		// when
		f_userEmail.emailCertificate(m_cert);
		// then
	}

	@Test
	public void testGenerateActualMessage() {
		// given
		ViewCertificate m_cert = mock(ViewCertificate.class);
		given(m_cert.pdfFile()).willReturn("mock.pdf");
		given(m_cert.prettyRank()).willReturn("4 Dan");
		given(m_cert.getEmail()).willReturn("jon@airsltd.com");
		given(m_cert.getFullName()).willReturn("Jon Boley");
		given(m_cert.getRunDate()).willReturn(new Date(new GregorianCalendar(2015,10,4).getTime().getTime()));
		// when
		f_userEmail.setEmailString("Email to: <fullname> for <rank> on <runDate>");
		String l_message = f_userEmail.generateActualMessage(m_cert);
		// then
		assertEquals("Email to: Jon Boley for 4 Dan on 2015-11-04", l_message);
		// Receiving 
	}
	
	@Test
	public void testLoadMessage() throws AddressException, MessagingException, IOException {
		// given
		MimeMessage l_message = new MimeMessage(f_userEmail.getSession());
		ViewCertificate m_cert = mock(ViewCertificate.class);
		given(m_cert.pdfFile()).willReturn("mock.pdf");
		given(m_cert.prettyRank()).willReturn("4 Dan");
		given(m_cert.getEmail()).willReturn("jon@airsltd.com");
		// when
		f_userEmail.loadMessage(m_cert, "An email message", l_message);
		// then
		assertEquals("jon@airsltd.com", l_message.getRecipients(Message.RecipientType.TO)[0].toString());
		assertEquals("no-reply@usgo.org", l_message.getFrom()[0].toString());
		assertEquals("jon.rank@airsltd.com", l_message.getRecipients(Message.RecipientType.BCC)[0].toString());
		assertEquals("[AGA Rank Award] Congratulations on earning your 4 Dan Certificate", l_message.getSubject());
		assertEquals("An email message", ((Multipart)l_message.getContent()).getBodyPart(0).getContent());
		assertEquals("mock.pdf", ((Multipart)l_message.getContent()).getBodyPart(1).getFileName());
		assertTrue(FileInputStream.class.isInstance(((MimeBodyPart)((Multipart)l_message.getContent()).getBodyPart(1)).getContent()));
		
	}

}
