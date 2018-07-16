/**
 * 
 */
package com.airsltd.aga.ranking.core.model;

import com.airsltd.aga.ranking.core.data.ViewCertificate;
import com.airsltd.core.data.BlockProvider;
import com.airsltd.core.model.ListModel;

/**
 * @author jon_000
 *
 */
public class ViewCertificateModel extends ListModel<ViewCertificate,Object> {

	public ViewCertificateModel() {
		super(new BlockProvider<ViewCertificate>(ViewCertificate.class));
	}

	/* (non-Javadoc)
	 * @see com.airsltd.core.model.BlockModel#getSelectionQuery(java.lang.Object)
	 */
	@Override
	protected String getSelectionQuery(Object p_qualifier) {
		return "select Player_ID, full_name, email, email2, Rank, RunDate "
				+ "from liverankobtained natural join playerData where accepted=1 and `generated`=0;";
	}

	
}
