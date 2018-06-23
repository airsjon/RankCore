/**
 *
 */
package com.airsltd.aga.ranking.core.model;

import com.airsltd.aga.ranking.core.data.RankGameLink;
import com.airsltd.core.model.PersistentIdListModel;

/**
 * @author Jon Boley
 *
 */
public class GameReverseModel extends PersistentIdListModel<RankGameLink> {

	private static GameReverseModel s_instance;
	private boolean f_loaded;

	public GameReverseModel() {
		super(RankGameLink.class);
		setInstance(this);
		getBlockProvider().setNoDuplicateCheck(true);
	}

	public static GameReverseModel getInstance() {
		if (s_instance == null) {
			new GameReverseModel();
		}
		return s_instance;
	}

	public static void setInstance(GameReverseModel p_model) {
		s_instance = p_model;
	}

	@Override
	public boolean loadModel(Object p_qualifier) {
		if (f_loaded == false) {
			f_loaded = true;
			getBlockProvider().loadDataBase("Select * From gamelink", null);
		}
		return f_loaded;
	}

}
