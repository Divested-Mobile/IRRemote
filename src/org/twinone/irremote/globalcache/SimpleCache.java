/*
 * Copyright 2014 Luuk Willemsen (Twinone)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.twinone.irremote.globalcache;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.content.Context;
import android.util.Log;

public class SimpleCache {

	private static final String TAG = "SimpleCache";

	/**
	 * True if the file was removed (or it didn't exist)
	 */
	public static boolean remove(Context c, String filename) {
		File file = new File(c.getCacheDir(), filename);
		if (file.exists())
			return file.delete();
		else
			return true;
	}

	public static boolean isAvailable(Context c, String filename) {
		File file = new File(c.getCacheDir(), filename);
		return file.exists() && file.isFile();
	}

	public static String get(Context c, String filename) {
		File file = new File(c.getCacheDir(), filename);
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
			Log.i(TAG, "File did not exist in cache");
		}
		return null;
	}

	public static void put(Context c, String filename, String data) {
		File file = new File(c.getCacheDir(), filename);
		try {
			FileOutputStream fos = new FileOutputStream(file);
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fos);
			outputStreamWriter.write(data);
			outputStreamWriter.close();
		} catch (Exception e) {
			Log.w(TAG, "Error writing file: ", e);
		}
	}

}
