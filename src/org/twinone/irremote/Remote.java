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
package org.twinone.irremote;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

public class Remote implements Serializable {
	public static final int VERSION_0 = 0;

	/**
	 * 
	 */
	private static final long serialVersionUID = 2984007269058624013L;

	public Remote() {
		buttons = new ArrayList<Button>();
	}

	/**
	 * All buttons should have a version, if we change the serialization
	 * mechanism to one that's not backwards compatible, we can use the version
	 * to determine how to deserialize it
	 */
	public int v = VERSION_0;

	public String name;

	public List<Button> buttons;

	private String serialize() {
		return new Gson().toJson(this);
	}

	private static Remote deserialize(String string) {
		return new Gson().fromJson(string, Remote.class);
	}

	private static File getRemotesDir(Context c) {
		final File file = new File(c.getFilesDir(), "remotes");
		if (!file.exists() || !file.isDirectory()) {
			file.mkdirs();
		}
		return file;
	}

	private static final String EXTENSION = ".remote.json";

	private static File getRemoteFile(Context c, String name) {
		return new File(getRemotesDir(c), name + EXTENSION);
	}

	/** Load this remote from the file system */
	public static Remote load(Context c, String name) {
		return deserialize(FileUtils.get(getRemoteFile(c, name)));
	}

	/** Save this remote to the file system */
	public void save(Context c) {
		FileUtils.put(getRemoteFile(c, name), serialize());
	}

	public static List<String> getNames(Context c) {
		List<String> result = new ArrayList<String>();
		File dir = getRemotesDir(c);
		for (String s : dir.list()) {
			Log.d("", "name: " + s);
			if (s.endsWith(EXTENSION)) {
				result.add(s);
			}
		}
		return result;
	}
}
