/**
 *
 */
package com.airsltd.aga.ranking.core.data;

import com.airsltd.core.IPrettyObject;

/**
 * @author Jon Boley
 *
 */
public enum Color implements IPrettyObject {
	BLACK("Black"), WHITE("White");
	
	private String f_prettyName;

	private Color(String p_prettyName) {
		f_prettyName = p_prettyName;
	}
	
	@Override
	public String niceString() {
		return f_prettyName;
	}
}
