/**
 *
 */
package com.airsltd.aga.ranking.core.model;

import java.util.Arrays;

import com.airsltd.aga.ranking.core.data.RankPlayer;
import com.airsltd.core.model.PersistentIdListModel;

/**
 * @author Jon Boley
 *
 */
public class PlayerModel extends PersistentIdListModel<RankPlayer> {

	private static PlayerModel s_instance;

	public PlayerModel() {
		super(RankPlayer.class);
		setInstance(this);
	}

	public static PlayerModel getInstance() {
		if (s_instance == null) {
			new PlayerModel();
		}
		return s_instance;
	}

	public static void setInstance(PlayerModel p_model) {
		s_instance = p_model;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.airsltd.core.model.PersistentIdListModel#getSelectionQuery(java.lang.
	 * Object)
	 */
	@Override
	protected String getSelectionQuery(Object p_qualifier) {
		return "Select * From players";
	}

	public RankPlayer fromAGAId(long p_agaId) {
		RankPlayer l_retVal = getElement(p_agaId);
		if (l_retVal == null) {
			l_retVal = new RankPlayer();
			l_retVal.setId(p_agaId);
			addModelData(Arrays.asList(l_retVal));
		}
		return l_retVal;
	}
}
