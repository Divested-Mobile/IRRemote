package org.twinone.irremote.providers.globalcache;


public class DeviceType extends GCBaseListable {
    /**
     *
     */
    private static final long serialVersionUID = 7685932096276835276L;
    /**
     * The key for API requests for this device type
     */
    public String Key;
    /**
     * The name of the device type to display to the user
     */
    public String DeviceType;

    @Override
    public String getKey() {
        return Key;
    }

    @Override
    public int getType() {
        return GlobalCacheProviderData.TYPE_DEVICE_TYPE;
    }

    @Override
    public String toString() {
        return DeviceType;
    }
}
