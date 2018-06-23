/**
 *
 */
package com.airsltd.aga.ranking.core.engine;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Jon Boley
 *
 */
public class ApplyRulesEngine<T extends Enum<T>, U> {
	private List<IRule<T, U>> f_rules;
	private Iterator<U> f_iterator;
	private int[] f_counts;
	private int f_missed;

	public ApplyRulesEngine() {
	}

	public ApplyRulesEngine(List<IRule<T, U>> p_rules, Iterator<U> p_iterator) {
		f_rules = p_rules;
		f_iterator = p_iterator;
		f_counts = new int[f_rules.size()];
	}

	/**
	 * @return the rules
	 */
	public List<IRule<T, U>> getRules() {
		return f_rules;
	}

	/**
	 * @param p_rules
	 *            the rules to set
	 */
	public void setRules(List<IRule<T, U>> p_rules) {
		f_counts = new int[p_rules.size()];
		f_rules = p_rules;
	}

	/**
	 * @return the iterator
	 */
	public Iterator<U> getIterator() {
		return f_iterator;
	}

	/**
	 * @param p_iterator
	 *            the iterator to set
	 */
	public void setIterator(Iterator<U> p_iterator) {
		f_iterator = p_iterator;
	}

	public void process() {
		int l_count = 0;
		int l_line = 0;
		final List<IRule<T, U>> l_rules = getRules();
		Collections.sort(l_rules);
		while (f_iterator.hasNext()) {
			U l_game = f_iterator.next();
			l_count++;
			if (l_count >= 100) {
				System.out.print(".");
				l_line++;
				if (l_line >= 80) {
					System.out.println();
					l_line=0;
				}
				l_count=0;
			}
			int l_index = 0;
			for (final IRule<T, U> l_rule : l_rules) {
				if (l_rule.process(l_game)) {
					f_counts[l_index]++;
					break;
				}
				l_index++;
			}
			if (l_index == l_rules.size()) {
				f_missed++;
			}
		}
	}

	public String dumpCounts() {
		int l_index = 0;
		final StringBuilder l_sb = new StringBuilder();
		l_sb.append("Missed: ");
		l_sb.append(f_missed);
		l_sb.append(System.getProperty("line.separator"));
		for (final IRule<T,U> l_rule : getRules()) {
			l_sb.append(l_rule.getCategory()+"["+l_rule.getIndex()+"]");
			l_sb.append(": ");
			l_sb.append(f_counts[l_index++]);
			l_sb.append(System.getProperty("line.separator"));
		}
		return l_sb.toString();
	}

}
