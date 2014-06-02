package org.twinone.irremote;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.util.Log;

public class FileUtils {

	private static final String TAG = "StorageManager";

	/**
	 * True if the file was removed (or it didn't exist)
	 */
	public static boolean remove(File file) {
		if (file == null) {
			Log.w(TAG, "Cannot remove null file!");
			return false;
		}
		if (file.exists())
			return file.delete();
		else
			return true;
	}

	public static boolean exists(File file) {
		return file != null && file.exists() && file.isFile();
	}

	public static String get(File file) {
		try {
			InputStream is = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			is.close();
			return sb.toString();
		} catch (Exception e) {
			Log.i(TAG, "File did not exist" + file);
		}
		return null;
	}

	public static void put(File file, String data) {
		try {
			FileOutputStream fos = new FileOutputStream(file);
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fos);
			outputStreamWriter.write(data);
			outputStreamWriter.close();
		} catch (Exception e) {
			Log.w(TAG, "Error writing file: " + file.getAbsolutePath());
		}
	}

}
