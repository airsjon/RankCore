/**
 *
 */
package com.airsltd.aga.ranking.core.engine;

import com.airsltd.aga.ranking.core.data.RankGame;

/**
 * Rules are processed to determine if a state needs to be set.
 * <p>
 * Rules can be written for {@link RankGame} and {@link RankObtained}.
 * <p>
 * Rules are ordered by the category and index of the rule.
 *
 * @author Jon Boley
 *
 */
public interface IRule<T extends Enum<T>, U> extends Comparable<IRule<T, U>> {

	/**
	 * Apply the rule on p_input to determine the new status.
	 * <p>
	 * It is the Rules responsibility to update the record with it's new status.
	 *
	 * @param p_input  the data to be analyzed
	 * @return true if the rule was applied.
	 */
	boolean process(U p_input);

	/**
	 * Rules are placed in categories that provide a group level sorting.
	 * 
	 * @return an <code>int</code> representing the level of this rule's
	 *         category.
	 */
	int getCategory();

	/**
	 * Index of the rule in the rule's category.
	 * 
	 * @return an <code>int</code> representing the location in the rule's
	 *         category.
	 */
	int getIndex();

}
