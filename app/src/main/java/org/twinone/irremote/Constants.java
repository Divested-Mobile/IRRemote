package org.twinone.irremote;

public abstract class Constants {

    public static final boolean DEBUG = BuildConfig.DEBUG;
    public static final boolean SHOW_ADS = !DEBUG;
    /**
     * Allow using the app when there is no transmitter detected on device
     */
    public static final boolean USE_DEBUG_TRANSMITTER = DEBUG;
    public static final boolean USE_DEBUG_RECEIVER = DEBUG;

    public static final String URL_REGISTER = "https://www.twinone.org/apps/irremote/register.php";
    public static final String URL_LOGIN = "https://www.twinone.org/apps/irremote/login.php";
    public static final String URL_VERIFY = "https://www.twinone.org/apps/irremote/verify.php";
    public static final String URL_UPLOAD = "https://www.twinone.org/apps/irremote/api/upload.php";
    public static final String URL_DOWNLOAD = "https://www.twinone.org/apps/irremote/api/download.php";


}
