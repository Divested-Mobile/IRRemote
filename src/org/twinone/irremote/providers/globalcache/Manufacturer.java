package org.twinone.irremote.providers.globalcache;

import org.twinone.irremote.ui.Listable;

public class Manufacturer extends Listable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3223300406074730286L;
	/** The key for API requests for this manufacturer */
	public String Key;
	/** The manufacturer name of this device type */
	public String Manufacturer;

	@Override
	public String getKey() {
		return Key;
	}

	@Override
	public int getType() {
		return UriData.TYPE_MANUFACTURER;
	}

	@Override
	public String getDisplayName() {
		return Manufacturer;
	}

}
