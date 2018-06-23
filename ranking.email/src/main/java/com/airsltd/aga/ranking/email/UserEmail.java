package com.airsltd.aga.ranking.email;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.airsltd.aga.ranking.core.data.ViewCertificate;
import com.airsltd.aga.ranking.core.model.RankConnection;
import com.airsltd.aga.ranking.core.model.ViewCertificateModel;
import com.airsltd.core.NotificationStatus;
import com.airsltd.core.data.AirsJavaDatabaseApp;
import com.airsltd.core.data.AirsPooledConnection;
import com.airsltd.core.data.CoreInterface;


/**
 * this Function reads all the users that need an email sent to them and
 * sends it.
 * 
 * This application also performs the following tests:
 * <ul><li>Verification expired? delete account</li>
 * <li>Password Reset expired? clear password reset</li>
 * </ul>
 * 
 * @author Jon
 *
 */
public class UserEmail extends AirsJavaDatabaseApp {

	private static Log f_logger = LogFactory.getLog(UserEmail.class);
	
	private Properties f_properties;

	private Session f_session;

	private String f_from = "no-reply@usgo.org";

	private String f_workDir = "";
	
	private String f_emailString = "";

	private String f_host = "email-smtp.us-east-1.amazonaws.com";

	private String f_user = "AKIAIJ67UT2UBCHD2KZQ";

	private String f_password = "Ao9Nh89ZtCt+uFBHMoa76DauZaJCLNm7b2JDDyK4AALf";

	private PreparedStatement f_statement;

	private int f_indexCount = 0;

	public static void main(String[] p_args) {
		int l_retVal = 0;
		UserEmail l_app = new UserEmail();
		try {
			l_app.initializeDatabase(RankConnection.getInstance());
    		if (p_args.length==1) {
    			l_app.setWorkDir(p_args[0]);
    		}
			l_app.sendEmails();
		} catch (Throwable e) {
			f_logger.error("Unexpected Error", e);
			l_retVal = 1;
		} finally {
			l_app.dumpStastics(System.out);
		}
		System.exit(l_retVal);
	}
	
	private void sendEmails() throws FileNotFoundException, SQLException {
		String l_emailString = loadEmailString();
		if (!l_emailString.isEmpty()) {
			setEmailString(l_emailString);
			ViewCertificateModel l_model = new ViewCertificateModel();
			f_statement = CoreInterface.getSystem().getConnection().prepareStatement("Update liverankobtained set generated=1 where Player_ID=? and Rank=?");
			for (ViewCertificate l_cert : l_model.getContentAsList(null)) {
				f_indexCount ++;
				emailCertificate(l_cert);
				if (f_indexCount>=15) break;
			}
		}
	}

	protected String loadEmailString() {
		InputStream l_stream = UserEmail.class.getResourceAsStream("/rankEmail.txt");
		String l_emailString = "";
		try {
			l_emailString = IOUtils.toString(l_stream);
		} catch (IOException e) {
			CoreInterface.getSystem().handleException("Unable to load email file", e, NotificationStatus.ERROR);
		}
		return l_emailString;
	}

	/**
	 * @return the workDir
	 */
	public String getWorkDir() {
		return f_workDir;
	}

	/**
	 * @param p_args the workDir to set
	 */
	public void setWorkDir(String p_args) {
		f_workDir = p_args;
	}

	
	/**
	 * @return the emailString
	 */
	public String getEmailString() {
		return f_emailString;
	}

	/**
	 * @param p_emailString the emailString to set
	 */
	public void setEmailString(String p_emailString) {
		f_emailString = p_emailString;
	}
	
	/**
	 * @return the session
	 */
	public Session getSession() {
		return f_session;
	}

	/**
	 * @param p_session the session to set
	 */
	public void setSession(Session p_session) {
		f_session = p_session;
	}

	/**
	 * @return the statement
	 */
	public PreparedStatement getStatement() {
		return f_statement;
	}

	/**
	 * @param p_statement the statement to set
	 */
	public void setStatement(PreparedStatement p_statement) {
		f_statement = p_statement;
	}

	public UserEmail() {
		super();
		f_properties = System.getProperties();
		f_properties.setProperty("mail.smtp.host", "email-smtp.us-east-1.amazonaws.com");
		f_session = Session.getDefaultInstance(f_properties);
	}

	protected void emailCertificate(ViewCertificate p_cert) {
		// create email
		String actualMessage = generateActualMessage(p_cert);
		MimeMessage message = new MimeMessage(f_session);
		try {
			// update student data
			f_statement.setInt(1, p_cert.getPlayerId());
			f_statement.setInt(2, 1);
			f_statement.execute();
			// email verification email
			loadMessage(p_cert, actualMessage, message);

			// send email
			Transport transport = f_session.getTransport("smtp");
			transport.connect(f_host,f_user,f_password);
			transport.sendMessage(message,message.getAllRecipients());
			transport.close();
			f_logger.info("Emailed certificate to: "+p_cert.getFullName()+" @ "+getEmailTo(p_cert));
		} catch (MessagingException | SQLException | IOException e) {
			f_logger.error("Unable to email registration to: "+p_cert.getFullName()+" @ "+getEmailTo(p_cert),e);
		}
	}

	protected void loadMessage(ViewCertificate p_cert, String actualMessage, MimeMessage message)
			throws MessagingException, AddressException, IOException {
		message.setFrom(new InternetAddress(f_from));
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(getEmailTo(p_cert)));
		message.addRecipient(Message.RecipientType.BCC, new InternetAddress("jon.rank@airsltd.com"));
		message.setReplyTo(new Address[] { new InternetAddress("jon.rank@airsltd.com") });
		message.setSubject("[AGA Rank Award] Congratulations on earning your "+p_cert.prettyRank()+" Certificate");
		 // Create the message part
		 BodyPart l_messageBodyPart = new MimeBodyPart();

		 // Now set the actual message
		 l_messageBodyPart.setText(actualMessage);

		 // Create a multipart message
		 Multipart l_multipart = new MimeMultipart();

		 // Set text message part
		 l_multipart.addBodyPart(l_messageBodyPart);

		 // Part two is attachment
		 MimeBodyPart attachmentBodyPart = new MimeBodyPart();
		 attachmentBodyPart.attachFile(new File(f_workDir+p_cert.pdfFile()), "application/pdf", null);
		 l_multipart.addBodyPart(attachmentBodyPart);
     
		 // Send the complete message parts
		 message.setContent(l_multipart);
	}

	protected String getEmailTo(ViewCertificate p_cert) {
//		return EMAILTEST[f_indexCount%3];
		return p_cert.getEmail();
	}

	protected String generateActualMessage(ViewCertificate p_cert) {
		String actualMessage = getEmailString()
				.replace("<fullname>", p_cert.fixName())
				.replace("<rank>", p_cert.prettyRank())
				.replace("<runDate>",  p_cert.getRunDate().toString())
				;
		return actualMessage;
	}

	/**
	 * Initialize the database connection.
	 */
	public void initializeDatabase(RankConnection p_product) {
		CoreInterface.setSystem(new CoreInterface());
		try {
			AirsPooledConnection.getInstance().initialize(p_product);
		} catch (ClassNotFoundException e) {
			CoreInterface.getSystem().handleException("Unable to connect to User DB", e, NotificationStatus.LOG);
		}

	};
	
}
