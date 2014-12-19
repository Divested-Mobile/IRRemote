package org.twinone.irremote;

public abstract class Constants {

	public static final boolean DEBUG = BuildConfig.DEBUG;
	public static final boolean SHOW_ADS = !DEBUG;
	/** Allow using the app when there is no transmitter detected on device */
	public static final boolean USE_DEBUG_TRANSMITTER = DEBUG;
	public static final boolean USE_DEBUG_RECEIVER = DEBUG;

	// public static final String URL_UPLOAD =
	// "https://www.twinone.org/apps/irremote/upload";
	// public static final String URL_REGISTER =
	// "https://www.twinone.org/apps/irremote/register";
	// public static final String URL_DOWNLOAD =
	// "https://www.twinone.org/apps/irremote/download";

	public static String URL_REGISTER = "https://www.twinone.org/apps/irremote/register/";
	public static String URL_VERIFY = "https://www.twinone.org/apps/irremote/verify/";
	public static String URL_UPLOAD = "https://www.twinone.org/apps/irremote/api/upload/";
	public static String URL_DOWNLOAD = "https://www.twinone.org/apps/irremote/api/download/";
	public static String URL_MANUFACTURERS = "https://www.twinone.org/apps/irremote/api/manufacturers/";

	static {
		boolean dbg = false;
		if (dbg) {
			String HOST = "http://192.168.1.100:9000";
			URL_UPLOAD = HOST;
			URL_REGISTER = HOST;
			URL_DOWNLOAD = HOST;

		}
	}

}
