/**
 *
 */
package com.airsltd.aga.ranking.core.model;

import com.airsltd.aga.ranking.core.data.LiveRankObtained;
import com.airsltd.aga.ranking.core.data.RankPlayer;
import com.airsltd.core.model.SegmentedListModel;

/**
 * Model of all Rank's obtained segmented by the Player obtaining the rank.
 *
 * @author Jon Boley
 *
 */
public class LiveRankObtainedModel extends SegmentedListModel<LiveRankObtained, RankPlayer> {

	private static LiveRankObtainedModel s_instance;

	public LiveRankObtainedModel() {
		super(LiveRankObtained.class);
		setInstance(this);
	}

	public static LiveRankObtainedModel getInstance() {
		if (s_instance == null) {
			new LiveRankObtainedModel();
		}
		return s_instance;
	}

	public static void setInstance(LiveRankObtainedModel p_instance) {
		s_instance = p_instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.airsltd.core.model.BlockModel#getSelectionQuery(java.lang.Object)
	 */
	@Override
	protected String getSelectionQuery(RankPlayer p_qualifier) {
		String l_retVal = "Select * FROM liverankobtained";
		String l_retVal2 = (!isCompleteLoad() && p_qualifier!=null)?" Where Player_ID="+p_qualifier.getId():"";
		String l_retVal3 = " order by Player_ID, RunDate";
		return l_retVal+l_retVal2+l_retVal3;
	}

}
