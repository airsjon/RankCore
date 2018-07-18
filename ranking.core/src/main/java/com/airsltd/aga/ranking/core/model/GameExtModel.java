/**
 * 
 */
package com.airsltd.aga.ranking.core.model;

import java.util.HashMap;
import java.util.Map;

import com.airsltd.aga.ranking.core.data.RankGame;
import com.airsltd.aga.ranking.core.data.RankGameExt;
import com.airsltd.core.model.LinkedModel;

/**
 * Every possible game for ranking has extended data stored in this model.
 * <p>
 * There is a one-to-one relationship between RankGame and RankGameExt.
 * 
 * @author Jon Boley
 *
 */
public class GameExtModel extends LinkedModel<RankGame, RankGameExt> {

	private static GameExtModel s_instance;
	private boolean f_completeLoad;
	private Map<RankGameExt, RankGameExt> f_modified = new HashMap<RankGameExt, RankGameExt>();

	public GameExtModel() {
		super(RankGame.class, RankGameExt.class);
		setInstance(this);
		getBlockProvider().setNoDuplicateCheck(true);
		setCompleteLoad(true);
	}

	public static GameExtModel getInstance() {
		if (s_instance == null) {
			new GameExtModel();
		}
		return s_instance;
	}

	public static void setInstance(GameExtModel p_model) {
		s_instance = p_model;
	}

	/**
	 * @return the completeLoad
	 */
	public boolean isCompleteLoad() {
		return f_completeLoad;
	}

	/**
	 * @param p_completeLoad the completeLoad to set
	 */
	public void setCompleteLoad(boolean p_completeLoad) {
		f_completeLoad = p_completeLoad;
	}

	/* (non-Javadoc)
	 * @see com.airsltd.core.model.BlockModel#getSelectionQuery(java.lang.Object)
	 */
	@Override
	protected String getSelectionQuery(RankGame p_qualifier) {
		String l_retVal1 = "Select * From gamesext";
		String l_retVal2 = "";
		String l_retVal3 = " order by Game_ID";
		if (!isCompleteLoad() && p_qualifier != null) {
			l_retVal2 += " WHERE Game_ID="+p_qualifier.getId();
		}
		return l_retVal1+l_retVal2+l_retVal3;
	}

	@Override
	protected RankGameExt extendData(RankGame p_element) {
		RankGameExt l_retVal = new RankGameExt(p_element);
		addContent(l_retVal);
		return l_retVal;
	}

	@Override
	protected RankGameExt modifyData(RankGameExt p_element) {
		RankGameExt l_retVal = f_modified.get(p_element);
		if (l_retVal == null) {
			l_retVal = new RankGameExt(p_element);
			f_modified.put(p_element, l_retVal);
		}
		return l_retVal;
	}

	/* (non-Javadoc)
	 * @see com.airsltd.core.model.BlockModel#startBlock()
	 */
	@Override
	public void startBlock() {
		f_modified.clear();
		super.startBlock();
	}

	/* (non-Javadoc)
	 * @see com.airsltd.core.model.BlockModel#cancelBlock()
	 */
	@Override
	public void cancelBlock() {
		f_modified.clear();
		super.cancelBlock();
	}

	/* (non-Javadoc)
	 * @see com.airsltd.core.model.BlockModel#endBlock()
	 */
	@Override
	public boolean endBlock(boolean p_useDeleteClause) {
		f_modified.forEach((l_old, l_new) -> {
			updateContent(l_old, l_new);
		});
		return super.endBlock(p_useDeleteClause);
	}
	
	

}
