package org.twinone.irremote;

public abstract class Constants {

	public static final boolean DEBUG = BuildConfig.DEBUG;
	public static final boolean SHOW_ADS = !DEBUG;
	/** Allow using the app when there is no transmitter detected on device */
	public static final boolean ALLOW_NO_TRANSMITTER = DEBUG;

	public static final String UPLOAD_URL = "https://www.twinone.org/apps/irremote/upload.php";
	public static final String DOWNLOAD_URL = "https://www.twinone.org/apps/irremote/download.php";

}
