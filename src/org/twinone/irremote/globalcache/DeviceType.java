package org.twinone.irremote.globalcache;

import org.twinone.irremote.Listable;

public class DeviceType extends Listable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7685932096276835276L;
	/** The key for API requests for this device type */
	public String Key;
	/** The name of the device type to display to the user */
	public String DeviceType;

	@Override
	public String getKey() {
		return Key;
	}

	@Override
	public int getType() {
		return UriData.TYPE_DEVICE_TYPE;
	}

	@Override
	public String getDisplayName() {
		return DeviceType;
	}
}
