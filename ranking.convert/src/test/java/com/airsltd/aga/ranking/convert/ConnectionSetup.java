package com.airsltd.aga.ranking.convert;

import static org.mockito.BDDMockito.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.mockito.Mockito;

import com.airsltd.core.data.CoreInterface;
import com.airsltd.core.data.Tuple3;

/**
 * Set up a mock database connection.  Also sets up a mock {@link ICoreInterface} by calling the
 * setup and teardown methods of {@link MockSystemSetup}
 * 
 * @author Jon Boley
 *
 */
public class ConnectionSetup extends MockSystemSetup {

	private static final int MAXTHROWNALLOWED = 50;
	protected Connection m_connection;
	protected PreparedStatement m_ps;
	protected ResultSet m_rs;
	protected Statement m_state;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		// given
		m_connection = mock(Connection.class);
		m_ps = mock(PreparedStatement.class);
		m_rs = mock(ResultSet.class);
		m_state = mock(Statement.class);
		given(m_connection.prepareStatement(anyString())).willReturn(m_ps);
		given(m_connection.prepareStatement(anyString(),anyInt())).willReturn(m_ps);
		given(m_connection.createStatement()).willReturn(m_state);
		given(m_ps.executeQuery()).willReturn(m_rs);
		given(m_state.executeUpdate(anyString())).willReturn(1);
		given(CoreInterface.getSystem().getConnection()).willReturn(m_connection);
	} 
	
	public void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * This test construct will attempt to get coverage by throwing from CoreInterface, m_connection, m_ps, and m_rs.
	 * <b>
	 * 
	 * @param l_whatToThrow  an integer whose bits do throws for CoreInterface (bit 1), m_connection (bit 2), m_ps (bit 3) and m_rs (bit 4)
	 * @param l_run  the code to execute with all the various throws
	 * @throws Throwable 
	 */
	public int testThrows(int l_whatToThrow, Callable<Boolean> l_run, Class<? extends Throwable> l_expected) throws Throwable {
		RuntimeException l_exception = new RuntimeException();
		if ((l_whatToThrow & 0x01) != 0) {
			given(CoreInterface.getSystem().getConnection()).willThrow(l_exception).willReturn(m_connection);
		}
		if ((l_whatToThrow & 0x02) != 0) {
			given(m_connection.prepareStatement(anyString())).willThrow(new SQLException()).willReturn(m_ps);
			given(m_connection.prepareStatement(anyString(), anyInt())).willThrow(new SQLException()).willReturn(m_ps);
			given(m_connection.createStatement()).willThrow(new SQLException()).willReturn(m_state);
			Mockito.doThrow(new SQLException()).doNothing().when(m_connection).close();
		}
		if ((l_whatToThrow & 0x04) != 0) {
			given(m_ps.executeQuery()).willThrow(new SQLException()).willReturn(m_rs);
			given(m_ps.executeQuery(anyString())).willThrow(new SQLException()).willReturn(m_rs);
			given(m_ps.executeUpdate()).willThrow(new SQLException()).willReturn(1);
			Mockito.doThrow(new SQLException()).doThrow(new SQLException()).doNothing().doThrow(new SQLException()).doNothing().when(m_ps).close();
		}
		if ((l_whatToThrow & 0x08) != 0) {
			Mockito.doThrow(new SQLException()).doThrow(new SQLException()).doThrow(new SQLException()).doNothing().when(m_rs).close();
		}
		int l_retVal = 0;
		boolean l_throw = true;
		while (l_throw) {
			try {
				l_run.call();
				l_throw = false;
			} catch (Throwable l_thrown) {
				if (l_thrown==l_exception || l_expected.isInstance(l_thrown)) {
					l_retVal++;
				} else {
					throw l_thrown;
				}
			}
			if (l_retVal > MAXTHROWNALLOWED) {
				l_throw = false;
			}
		}
		// attempt to get suppressed versions
		if ((l_whatToThrow & 0x08) != 0) {
			List<Tuple3<Boolean, Boolean, Boolean>> l_throws = 
					new ArrayList<Tuple3<Boolean,Boolean,Boolean>>();
			l_throws.add(new Tuple3<Boolean, Boolean, Boolean>(false, false, false));
			l_throws.add(new Tuple3<Boolean, Boolean, Boolean>(false, false, true));
			l_throws.add(new Tuple3<Boolean, Boolean, Boolean>(false, true, false));
			l_throws.add(new Tuple3<Boolean, Boolean, Boolean>(false, true, true));
			l_throws.add(new Tuple3<Boolean, Boolean, Boolean>(true, false, false));
			l_throws.add(new Tuple3<Boolean, Boolean, Boolean>(true, false, true));
			l_throws.add(new Tuple3<Boolean, Boolean, Boolean>(true, true, false));
			l_throws.add(new Tuple3<Boolean, Boolean, Boolean>(true, true, true));
			given(m_rs.next()).willThrow(new SQLException());
			for (Tuple3<Boolean, Boolean, Boolean> l_tm : l_throws) {
				if (l_tm.getPrimary()) {
					Mockito.doThrow(new SQLException()).when(m_connection).close();
				} else {
					Mockito.doNothing().when(m_connection).close();
				}
				if (l_tm.getSecondary()) {
					Mockito.doThrow(new SQLException()).when(m_ps).close();
				} else {
					Mockito.doNothing().when(m_ps).close();
				}
				if (l_tm.getTertiary()) {
					Mockito.doThrow(new SQLException()).when(m_rs).close();
				} else {
					Mockito.doNothing().when(m_rs).close();
				}
				try {
					l_run.call();
					l_throw = false;
				} catch (Throwable l_thrown) {
					if (l_thrown==l_exception || l_expected.isInstance(l_thrown)) {
						l_retVal++;
						if (l_thrown.getSuppressed().length==0) {
							l_throw = false;
						}
					} else {
						throw l_thrown;
					}
				}
				if (l_retVal > MAXTHROWNALLOWED) {
					l_throw = false;
				}
			}
		}
		return l_retVal;
	}
}
