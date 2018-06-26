package com.airsltd.aga.ranking.dump;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.airsltd.aga.ranking.core.data.ViewCertificate;
import com.airsltd.aga.ranking.core.model.RankConnection;
import com.airsltd.aga.ranking.core.model.ViewCertificateModel;
import com.airsltd.core.data.AirsJavaDatabaseApp;
import com.airsltd.core.data.CoreInterface;

/**
 * Hello world!
 *
 */
public class CertificateList extends AirsJavaDatabaseApp
{
	
	private static final Log LOGGER = LogFactory.getLog(CertificateList.class);
	private static final int FILESTART = 1;
	
    public CertificateList(String[] p_args) {
    	super(p_args);
	}

	public static void main( String[] p_args ) throws FileNotFoundException, IOException
    {
		if (p_args.length!=FILESTART) {
			LOGGER.error("Ouput file needs to be specified.");
		} else {
			CertificateList l_app = new CertificateList(p_args);
			String l_outputFile = p_args[0];
			try (FileOutputStream l_output = new FileOutputStream(l_outputFile);
				 OutputStreamWriter l_writer = new OutputStreamWriter(l_output)) {
				l_app.initializeDatabase(RankConnection.getInstance());
				l_app.dumpData(l_writer);
			}
		}
    }

	public void dumpData(OutputStreamWriter p_outputStreamWriter) throws IOException {
		
		ViewCertificateModel l_model = new ViewCertificateModel();
		for (ViewCertificate l_cert : l_model.getContentAsList(null)) {
			dumpCertificate(l_cert, p_outputStreamWriter);
		}
	}
	
	/**
	 * Dump date to output stream for the rank obtained.
	 * 
	 * @param p_obtained
	 * @param p_outputStreamWriter
	 * @throws IOException 
	 */
	public void dumpCertificate(ViewCertificate p_obtained, OutputStreamWriter p_outputStreamWriter) throws IOException {
		int l_rank = p_obtained.getRank();
		l_rank = (l_rank<1)?l_rank-1:0;
		String l_inFile =  (l_rank<0)?"GoKyuCertificate.pdf,":"GoDanCertificate.pdf,";
		p_outputStreamWriter.write(l_inFile);
		p_outputStreamWriter.write(p_obtained.pdfFile()+",");
		p_outputStreamWriter.write("393,250,30,"+p_obtained.fixName()+" ["+p_obtained.getPinPlayer()+"],Times-Roman,CENTER,");
		p_outputStreamWriter.write("417,190,16,"+Math.abs(l_rank)+",Times-Roman,LEFT,");
		p_outputStreamWriter.write("457,113,14,"+p_obtained.getRunDate()+",Times-Roman,LEFT"+CoreInterface.NEWLINE);
	}

}
