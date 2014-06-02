package org.twinone.irremote.globalcache;

import org.twinone.irremote.Listable;

public class Codeset extends Listable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5767029490368222741L;
	/** The key for API requests for this codeset */
	public String Key;
	/** The name of the codeset to display to the user */
	public String Codeset;

	@Override
	public String getDisplayName() {
		return Codeset;
	}

	@Override
	public String getKey() {
		return Key;
	}

	@Override
	public int getType() {
		return UriData.TYPE_CODESET;
	}
}
