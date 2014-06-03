package org.twinone.irremote.providers.globalcache;


public class Codeset extends GCBaseListable {
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
