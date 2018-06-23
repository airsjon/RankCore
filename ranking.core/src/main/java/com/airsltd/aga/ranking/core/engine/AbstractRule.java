/**
 *
 */
package com.airsltd.aga.ranking.core.engine;

import com.airsltd.core.IPrettyObject;

/**
 * Framework for sorting Rules.
 * <p>
 * Predefined categories are {@link #PRIMARYRULE} and {@link #TERTIARYRULE}.
 *
 * @author Jon Boley
 *
 */
public abstract class AbstractRule<T extends Enum<T>, U> implements IPrettyObject, IRule<T, U> {

	public static final int PRIMARYRULE = 5;
	public static final int TERTIARYRULE = 15;
	/**
	 * Unless otherwise specified all Rules are placed in category 10.
	 */
	public static final int DEFAULTRULE = 10;
	/**
	 * Each new rule is assigned an index one higher than the previous rule.
	 */
	private static int s_index = 1;
	/**
	 * The category of the rule
	 */
	private int f_category = DEFAULTRULE;
	/**
	 * Default implementation is to allocate index based on time of creation.
	 */
	private int f_index = s_index++;
	/**
	 * Name of the rule.
	 * <p>
	 * Used primarily for logging.
	 */
	private String f_name;

	public AbstractRule(int p_category, String p_name) {
		super();
		f_category = p_category;
		f_name = p_name;
	}

	public AbstractRule(String p_name) {
		this(DEFAULTRULE, p_name);
	}

	public AbstractRule() {
		this("");
	}

	@Override
	public int compareTo(IRule<T, U> p_arg0) {
		final int l_retVal = Integer.compare(f_category, p_arg0.getCategory());
		return l_retVal == 0 ? Integer.compare(f_index, p_arg0.getIndex()) : l_retVal;
	}

	/**
	 * @return the category
	 */
	@Override
	public int getCategory() {
		return f_category;
	}

	/**
	 * @param p_category
	 *            the category to set
	 */
	public void setCategory(int p_category) {
		f_category = p_category;
	}

	/**
	 * @return the index
	 */
	@Override
	public int getIndex() {
		return f_index;
	}

	/**
	 * @param p_index
	 *            the index to set
	 */
	public void setIndex(int p_index) {
		f_index = p_index;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return f_name;
	}

	/**
	 * @param p_name
	 *            the name to set
	 */
	public void setName(String p_name) {
		f_name = p_name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return (f_name.isEmpty()?"":f_name+" ") + "[" + getCategory() + ":" + getIndex() + "]";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.airsltd.core.IPrettyObject#niceString()
	 */
	@Override
	public String niceString() {
		return "Rule " + (f_name.isEmpty() ? toString() : f_name);
	}

}
