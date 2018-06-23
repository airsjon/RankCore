/**
 *
 */
package com.airsltd.aga.ranking.core.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.airsltd.aga.ranking.core.data.RankGame;
import com.airsltd.aga.ranking.core.data.RankPlayer;
import com.airsltd.core.collections.AirsCollections;
import com.airsltd.core.model.PersistentIdSegmentedListModel;

/**
 * @author jon_000
 *
 */
public class GameModel extends PersistentIdSegmentedListModel<RankGame, RankPlayer> {

	private static GameModel s_instance;

	public GameModel() {
		super(RankGame.class);
		setInstance(this);
		getBlockProvider().setNoDuplicateCheck(true);
		setCompleteLoad(true);
	}

	public static GameModel getInstance() {
		if (s_instance == null) {
			new GameModel();
		}
		return s_instance;
	}

	public static void setInstance(GameModel p_model) {
		s_instance = p_model;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.airsltd.core.model.PersistentIdSegmentedListModel#addModelData(java.
	 * util.List)
	 */
	@Override
	public Set<RankGame> addModelData(Set<RankGame> p_addData) {
		Set<RankGame> l_copy = new HashSet<RankGame>(p_addData);
		l_copy.removeIf(p -> getAGAElement(p.getPlayer(), p.getAgaGameId())!=null);
		return super.addModelData(l_copy);
	}

	/**
	 * Find all the games played between two players in the same tournament.
	 * <p>
	 * This list will include the original game that is passed.
	 *
	 * @param p_game
	 *            not null, the game to look for similar games.
	 * @return
	 */
	public List<RankGame> findSimilarGames(final RankGame p_game) {
		return p_game.getTournament() != null
				? AirsCollections.findAll(p_other -> p_game.similiar(p_other), getContentAsList(p_game.getPlayer()))
				: Arrays.asList(p_game);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.airsltd.core.model.BlockModel#getSelectionQuery(java.lang.Object)
	 */
	@Override
	protected String getSelectionQuery(RankPlayer p_qualifier) {
		String l_retVal1 = "Select * From games";
		String l_retVal2 = "";
		String l_retVal3 = " order by Player_ID, Date, Tournament_ID, Round";
		if (!isCompleteLoad() && p_qualifier != null) {
			l_retVal2 += " WHERE Player_ID="+p_qualifier.getId();
		}
		return l_retVal1+l_retVal2+l_retVal3;
	}

	/**
	 * Return the RankGame element for player p_player with AGA Game Id p_id
	 * 
	 * @param p_player  not null, RankPlayer to locate AGA Game for
	 * @param p_id  int, the AGA Game Id for the game
	 * @return RankGame for the player associated with AGA Game id p_id.
	 */
	public RankGame getAGAElement(RankPlayer p_player, long p_id) {
		RankGame l_retVal = null;
		for (final RankGame l_current : getContentAsList(p_player)) {
			if (l_current.getAgaGameId() == p_id) {
				l_retVal = l_current;
				break;
			}
		}
		return l_retVal;
	}

}
