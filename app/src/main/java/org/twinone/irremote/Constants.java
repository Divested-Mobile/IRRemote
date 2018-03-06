package org.twinone.irremote;

import org.twinone.irremote.BuildConfig;

public abstract class Constants {

    public static final boolean DEBUG = BuildConfig.DEBUG;

    /**
     * Allow using the app when there is no transmitter detected on device
     */
    public static final boolean USE_DEBUG_TRANSMITTER = DEBUG;
    public static final boolean USE_DEBUG_RECEIVER = DEBUG;

}
