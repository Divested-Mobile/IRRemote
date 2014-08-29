package org.twinone.irremote.providers.twinone.upload;

import java.net.MalformedURLException;
import java.net.URL;

public abstract class UploadInterface {

	private static boolean DEBUG = true;

	public static String getKey() {
		return "af017ebbd416b3c22b82d1ef49b54270";
	}

	private static String getUrl() {
		if (DEBUG)
			return "http://192.168.42.240/index.php";
		return "https://twinone.org/apps/irremote/db/upload.php";
	}

	public static final URL getUploadUrl() throws MalformedURLException {
		return new URL(getUrl());
	}

}
