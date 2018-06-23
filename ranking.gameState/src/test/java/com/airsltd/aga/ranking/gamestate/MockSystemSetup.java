package com.airsltd.aga.ranking.gamestate;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.airsltd.core.ICoreInterface;
import com.airsltd.core.NotificationStatus;
import com.airsltd.core.data.CoreInterface;

/**
 * Mock {@link ICoreInterface} for testing purposes.  Also adds a special handler for exception processing.
 * 
 * @author Jon Boley
 *
 */
public class MockSystemSetup {

	protected static final boolean SHOWTRACEALWAYS = false;

	private ICoreInterface f_oldSystem;

	private static Answer<Boolean> s_exceptionAnswer = new Answer<Boolean>() {

		@Override
		public Boolean answer(InvocationOnMock p_invocation)
				throws Throwable {
			String l_reason = ((String) p_invocation.getArguments()[0]);
			Throwable l_throw = ((Throwable)p_invocation.getArguments()[1]);
			Exception l_excpetion = new RuntimeException(l_reason, l_throw);
			if (SHOWTRACEALWAYS) {
				if (l_throw != null) {
					l_throw.printStackTrace();
				} else {
					l_excpetion.printStackTrace();
				}
			}
			throw(l_excpetion);
		}
	};

	public void setUp() throws Exception {
		// given
		f_oldSystem = CoreInterface.getSystem();
		CoreInterface.setSystem(mock(ICoreInterface.class));
		given(CoreInterface.getSystem().handleException(anyString(), any(Throwable.class), any(NotificationStatus.class))).
				willAnswer(s_exceptionAnswer);
	} 
	
	public void tearDown() throws Exception {
		CoreInterface.setSystem(f_oldSystem);
	}

}
