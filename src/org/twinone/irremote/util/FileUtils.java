package org.twinone.irremote.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.content.res.AssetManager;
import android.util.Log;

public class FileUtils {

	private static final String TAG = "StorageManager";

	/**
	 * Remove a file or directory recursively
	 * 
	 * @param file
	 */
	public static void remove(File file) {
		if (file.isDirectory())
			for (File f : file.listFiles())
				remove(f);
		file.delete();
	}

	public static void rename(File oldFile, File newFile) {
		oldFile.renameTo(newFile);
	}

	/**
	 * Clear the directory without removing it
	 * 
	 * @param directory
	 */
	public static void clear(File directory) {
		if (!directory.isDirectory()) {
			return;
		}
		for (File f : directory.listFiles()) {
			remove(f);
		}
	}

	public static boolean exists(File file) {
		return file != null && file.exists() && file.isFile();
	}

	public static String read(AssetManager assets, String filename) {
		try {
			return read(assets.open(filename));
		} catch (Exception e) {
			Log.d(TAG, "Error getting assets file: " + filename);
			return null;
		}
	}

	public static String read(InputStream is) {
		try {
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
			Log.w(TAG, "Error reading from inputstream");
		}
		return null;
	}

	public static String read(File file) {
		try {
			InputStream is = new FileInputStream(file);
			return read(is);
		} catch (Exception e) {
			Log.w(TAG, "Error reading file " + file.getName());
			return null;
		}
	}

	public static void write(File file, String data) {
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
