package org.twinone.irremote.globalcache;

import java.io.File;

import org.twinone.irremote.FileUtils;

import android.content.Context;

public class SimpleCache {

	private static File getFile(Context c, String name) {
		return new File(c.getCacheDir(), name);
	}

	/**
	 * True if the file was removed (or it didn't exist)
	 */
	public static boolean remove(Context c, String filename) {
		return FileUtils.remove(getFile(c, filename));
	}

	public static boolean isAvailable(Context c, String filename) {
		return FileUtils.exists(getFile(c, filename));
	}

	public static String get(Context c, String filename) {
		return FileUtils.get(getFile(c, filename));
	}

	public static void put(Context c, String filename, String data) {
		FileUtils.put(getFile(c, filename), data);
	}

}
