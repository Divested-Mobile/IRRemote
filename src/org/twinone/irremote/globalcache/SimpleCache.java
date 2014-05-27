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
