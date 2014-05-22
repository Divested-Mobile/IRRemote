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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.twinone.irremote.globalcache.SimpleStorage;

import android.content.Context;

import com.google.gson.Gson;

public class Remote implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2984007269058624013L;

	public Remote() {
		buttons = new ArrayList<Button>();
	}

	public String name;

	public List<Button> buttons;

	private String serialize() {
		return new Gson().toJson(this);
	}

	private Remote deserialize(String string) {
		return new Gson().fromJson(string, Remote.class);
	}

	/** Load this remote from the file system */
	public Remote load(Context c, String buttonName) {
		return deserialize(SimpleStorage.get(c, buttonName));
	}

	/** Save this remote to the file system */
	public void save(Context c) {
		SimpleStorage.put(c, name + ".twinoneirremote", serialize());
	}
	
	

}
