/**
 *
 */
package com.airsltd.aga.ranking.core.model;

import java.sql.Date;

import com.airsltd.aga.ranking.core.data.RankPlayer;
import com.airsltd.aga.ranking.core.data.RankRating;
import com.airsltd.core.data.BlockData;
import com.airsltd.core.data.BlockProvider;
import com.airsltd.core.model.SegmentedListModel;

/**
 * @author jon_000
 *
 */
public class RatingModel extends SegmentedListModel<RankRating, RankPlayer> {

	private static RatingModel s_instance;

	public RatingModel() {
		super(new BlockProvider<RankRating>(RankRating.class));
		setInstance(this);
		setCompleteLoad(true);
	}

	public static RatingModel getInstance() {
		if (s_instance == null) {
			new RatingModel();
		}
		return s_instance;
	}

	public static void setInstance(RatingModel p_model) {
		s_instance = p_model;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.airsltd.core.model.BlockModel#getSelectionQuery(java.lang.Object)
	 */
	@Override
	protected String getSelectionQuery(RankPlayer p_qualifier) {
		return "Select * From ratings order by Player_ID, Date";
	}

	public boolean contains(RankPlayer p_player, Date p_date) {
		boolean l_retVal = false;
		for (final RankRating l_rating : getContentAsList(p_player)) {
			if (BlockData.objectCompare(l_rating.getDate(), p_date)) {
				l_retVal = true;
				break;
			}
		}
		return l_retVal;
	}

}
