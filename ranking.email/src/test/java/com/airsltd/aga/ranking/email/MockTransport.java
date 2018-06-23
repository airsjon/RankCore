/**
 * Copyright (c) 2013 Jon Boley
 */
package com.airsltd.aga.ranking.email;

import static org.mockito.BDDMockito.*;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.URLName;
import javax.mail.event.TransportListener;

/**
 * @author Jon Boley
 *
 */
public class MockTransport extends Transport {

	static Transport f_debugTranport;
	
	public MockTransport(Session p_session, URLName p_urlname) {
		super(p_session, p_urlname);
		if (f_debugTranport==null) f_debugTranport = mock(Transport.class);
	}

	/* (non-Javadoc)
	 * @see javax.mail.Transport#sendMessage(javax.mail.Message, javax.mail.Address[])
	 */
	@Override
	public void sendMessage(Message p_arg0, Address[] p_arg1)
			throws MessagingException {
		f_debugTranport.sendMessage(p_arg0, p_arg1);
	}

	/* (non-Javadoc)
	 * @see javax.mail.Transport#addTransportListener(javax.mail.event.TransportListener)
	 */
	@Override
	public synchronized void addTransportListener(TransportListener p_l) {
		f_debugTranport.addTransportListener(p_l);
	}

	/* (non-Javadoc)
	 * @see javax.mail.Transport#removeTransportListener(javax.mail.event.TransportListener)
	 */
	@Override
	public synchronized void removeTransportListener(TransportListener p_l) {
		f_debugTranport.removeTransportListener(p_l);
	}

	/* (non-Javadoc)
	 * @see javax.mail.Service#close()
	 */
	@Override
	public synchronized void close() throws MessagingException {
		f_debugTranport.close();
	}

	/* (non-Javadoc)
	 * @see javax.mail.Service#connect()
	 */
	@Override
	public void connect() throws MessagingException {
		f_debugTranport.connect();
	}

	/* (non-Javadoc)
	 * @see javax.mail.Service#connect(java.lang.String, int, java.lang.String, java.lang.String)
	 */
	@Override
	public synchronized void connect(String p_arg0, int p_arg1, String p_arg2,
			String p_arg3) throws MessagingException {
		f_debugTranport.connect(p_arg0, p_arg1, p_arg2, p_arg3);
	}

	/* (non-Javadoc)
	 * @see javax.mail.Service#connect(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void connect(String p_host, String p_user, String p_password)
			throws MessagingException {
		f_debugTranport.connect(p_host, p_user, p_password);
	}

	/**
	 * @return the debugTranport
	 */
	public static Transport getDebugTranport() {
		return f_debugTranport;
	}

}
