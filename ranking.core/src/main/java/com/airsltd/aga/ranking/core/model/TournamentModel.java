/**
 *
 */
package com.airsltd.aga.ranking.core.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.airsltd.aga.ranking.core.data.RankTournament;
import com.airsltd.core.model.PersistentIdListModel;

/**
 * @author Jon Boley
 *
 */
public class TournamentModel extends PersistentIdListModel<RankTournament> {

	private static TournamentModel s_instance;
	private final Map<String, RankTournament> f_byId = new HashMap<String, RankTournament>();

	public TournamentModel() {
		super(RankTournament.class);
		setInstance(this);
	}

	public static TournamentModel getInstance() {
		if (s_instance == null) {
			new TournamentModel();
		}
		return s_instance;
	}

	public static void setInstance(TournamentModel p_model) {
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
		return "Select * From tournament order by Tournament_ID";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.airsltd.core.model.PersistentIdListModel#addModelData(java.util.List)
	 */
	@Override
	public Set<RankTournament> addModelData(Set<RankTournament> p_addData) {
		for (final RankTournament l_tournament : p_addData) {
			f_byId.put(l_tournament.getCode(), l_tournament);
		}
		return super.addModelData(p_addData);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.airsltd.core.model.PersistentIdListModel#remModelData(java.util.List)
	 */
	@Override
	public void remModelData(Set<RankTournament> p_remData) {
		for (final RankTournament l_tournament : p_remData) {
			f_byId.remove(l_tournament.getCode());
		}
		super.remModelData(p_remData);
	}

	public boolean contains(String p_code) {
		return f_byId.containsKey(p_code);
	}

}
