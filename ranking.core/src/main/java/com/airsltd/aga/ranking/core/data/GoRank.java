/**
 *
 */
package com.airsltd.aga.ranking.core.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.airsltd.core.IPrettyObject;

/**
 * Rank stored as an integer.<br>
 * 0 - 49 -> UNKNOWNRANKkyu (0) to 1kyu (49)<br>
 * 50 -> unknown<br>
 * 51 - PROFESSIONALRANK -> 1dan (51) to 19dan (PROFESSIONALRANK)<br>
 * PROFESSIONALRANK+1 - 89 -> 1pro (PROFESSIONALRANK+1) to 20dan (89)<br>
 * RANKSPAN -> named as stored in database
 *
 * @author Jon Boley
 *
 */
public final class GoRank implements IPrettyObject {

	private static final List<String> NAMEDRANKS = Arrays.asList("一段", "二段", "三段", "四段", "五段", "六段", "七段", "八段", "九段");
	private static final int RANKSPAN = 90;
	private static final int UNKNOWNRANK = 50;
	private static final int PROFESSIONALRANK = 69;
	private static final int DANMAX = 19;
	private static final double DANRATING = -50.5f;
	private static final double PROFESSIONALRATING = 8.5f;
	private static final int RANKGROUP = 2;
	/**
	 * Offset to create a continuous rating.
	 */
	private static final int KYUOFFSET = 2;
	private static final double ONESTONESTRENGTH = .58f;
	private static final double KOMIVALUE = 0.0757f;
	private static final int TWOSTONES = 2;
	private static List<GoRank> s_ranks = new ArrayList<GoRank>(RANKSPAN);

	static {
		for (int l_cnt = 0; l_cnt != RANKSPAN; l_cnt++) {
			s_ranks.add(new GoRank(l_cnt));
		}
	}

	private final int f_rankValue;

	private GoRank(int p_rankValue) {
		f_rankValue = p_rankValue;
	}

	public static GoRank getInstance(int p_rank) {
		if (p_rank == s_ranks.size()) {
			s_ranks.add(new GoRank(p_rank));
		} else if (p_rank > s_ranks.size()) {
			throw new RankException("Invalid rank value");
		}
		return s_ranks.get(p_rank);
	}

	public static GoRank parseRank(String p_string) {

		GoRank l_retVal;
		if (p_string == null || p_string.isEmpty()) {
			l_retVal = s_ranks.get(UNKNOWNRANK);
		} else if (NAMEDRANKS.contains(p_string)) {
			l_retVal = s_ranks.get(PROFESSIONALRANK + 1 + NAMEDRANKS.indexOf(p_string));
		} else {
			l_retVal = parsePatternRank(p_string);
		}
		return l_retVal;
	}

	private static GoRank parsePatternRank(String p_string) {
		final Pattern l_rankP = Pattern.compile("(\\d{1,2})([dD]|[pP]|[kK]| [dD]an| [kK]yu| [dD]an [pP]roffesional)");
		final Matcher l_m = l_rankP.matcher(p_string);
		GoRank l_retVal = getInstance(UNKNOWNRANK);
		if (l_m.matches()) {
			final int l_rankValue = Integer.parseInt(l_m.group(1));
			final String l_typeRank = l_m.group(RANKGROUP).trim().toUpperCase();
			if (l_typeRank.startsWith("DAN P") || l_typeRank.startsWith("P")) {
				l_retVal = getInstance(PROFESSIONALRANK + l_rankValue);
			} else if (l_typeRank.startsWith("D")) {
				l_retVal = l_rankValue > 0 && l_rankValue <= DANMAX ? getInstance(UNKNOWNRANK + l_rankValue)
						: getInstance(UNKNOWNRANK);
			} else {
				l_retVal = l_rankValue > 0 && l_rankValue <= UNKNOWNRANK - 1 ? getInstance(UNKNOWNRANK - l_rankValue)
						: getInstance(UNKNOWNRANK);
			}
		}
		return l_retVal;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "GoRank [rankValue=" + f_rankValue + "] " + rank();
	}

	public String rank() {
		String l_retVal = "";
		if (f_rankValue != UNKNOWNRANK) {
			if (f_rankValue < UNKNOWNRANK) {
				l_retVal = UNKNOWNRANK - f_rankValue + " kyu";
			} else if (f_rankValue < PROFESSIONALRANK + 1) {
				l_retVal = f_rankValue - UNKNOWNRANK + " dan";
			} else if (f_rankValue < RANKSPAN) {
				l_retVal = f_rankValue - PROFESSIONALRANK + " dan professional";
			}
		}
		return l_retVal;
	}

	public String simpleRank() {
		String l_retVal = "";
		if (f_rankValue != UNKNOWNRANK) {
			if (f_rankValue < UNKNOWNRANK) {
				l_retVal = UNKNOWNRANK - f_rankValue + "k";
			} else if (f_rankValue < PROFESSIONALRANK + 1) {
				l_retVal = f_rankValue - UNKNOWNRANK + "d";
			} else if (f_rankValue < RANKSPAN) {
				l_retVal = f_rankValue - PROFESSIONALRANK + "p";
			}
		}
		return l_retVal;
	}

	/**
	 * @return the rankValue
	 */
	public int getRankValue() {
		return f_rankValue;
	}

	public String toSqlString() {
		return f_rankValue + "";
	}

	public static GoRank fromSqlString(String p_string) {
		GoRank l_retVal;
		try {
			l_retVal = getInstance(Integer.parseInt(p_string));
		} catch (final NumberFormatException l_e) {
			l_retVal = getInstance(UNKNOWNRANK);
		}
		return l_retVal;
	}

	@Override
	public String niceString() {
		return simpleRank();
	}

	public int compare(GoRank p_otherRank) {
		return f_rankValue - p_otherRank.f_rankValue;

	}

	/**
	 * Return the rank as a continuous rating.
	 * <p>
	 * Ratings that would be Kyu ratings are modified by
	 * {@link GoRank#KYUOFFSET} to create a continouse rating.
	 *
	 * @return the rank as a double
	 */
	public Double toRating() {
		Double l_retValue = DANRATING + f_rankValue + (f_rankValue > UNKNOWNRANK ? 1 : KYUOFFSET);
		if (f_rankValue > PROFESSIONALRANK) {
			l_retValue = PROFESSIONALRATING;
		} else if (f_rankValue == UNKNOWNRANK) {
			l_retValue = null;
		}
		return l_retValue;
	}

	public static double calculateGameStrength(int p_handicap, int p_komi) {
		return (p_handicap < TWOSTONES ? ONESTONESTRENGTH : p_handicap) - KOMIVALUE * p_komi;
	}

}
