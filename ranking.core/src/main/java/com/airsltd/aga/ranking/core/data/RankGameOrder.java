/**
 * 
 */
package com.airsltd.aga.ranking.core.data;

import java.util.Comparator;

/**
 * @author Jon Boley
 *
 */
public class RankGameOrder implements Comparator<RankGame> {

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(RankGame p_o1, RankGame p_o2) {
		int l_retVal = p_o1.getDate().compareTo(p_o2.getDate());
		if (l_retVal == 0) {
			l_retVal = tournamentTest(p_o1, p_o2);
		}
		return l_retVal;
	}

	private int tournamentTest(RankGame p_o1, RankGame p_o2) {
		int l_retVal = Long.compare(tournamentId(p_o1), tournamentId(p_o2));
		if (l_retVal==0) {
			l_retVal = roundTest(p_o1, p_o2);
		}
		return l_retVal;
	}

	private long tournamentId(RankGame p_o1) {
		return p_o1.getTournament()==null?0:p_o1.getTournament().getId();
	}

	private int roundTest(RankGame p_o1, RankGame p_o2) {
		int l_retVal = Integer.compare(p_o1.getRound(),p_o2.getRound());
		if (l_retVal == 0) {
			l_retVal = Integer.compare(p_o1.getAgaGameId(),p_o2.getAgaGameId());
		}
		return l_retVal;
	}

}
