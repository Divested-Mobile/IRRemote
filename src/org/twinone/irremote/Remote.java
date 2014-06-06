package org.twinone.irremote;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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

	private static final String REMOTES_VERSION = "_v1";
	private static final String EXTENSION = ".remote";
	private static final String BUTTON_EXTENSION = ".button";
	private static final String BUTTON_PREFIX = "b_";

	// OK
	private static File getRemotesDir(Context c) {
		final File dir = new File(c.getFilesDir(), "remotes" + REMOTES_VERSION);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir;
	}

	/**
	 * Calls {@link #getRemoteDir(Context, String)} for this remote
	 * 
	 */
	private File getRemoteDir(Context c) {
		return getRemoteDir(c, name);
	}

	private static File getRemoteDir(Context c, String name) {
		File dir = new File(getRemotesDir(c), name + EXTENSION);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir;
	}

	public static boolean exists(Context c, String name) {
		return FileUtils.exists(getRemoteDir(c, name));
	}

	/** Load this remote from the file system */
	public static Remote load(Context c, String name) {
		if (name == null)
			return null;
		final Remote remote = new Remote();
		remote.name = name;
		final Gson gson = new Gson();
		for (final File f : getRemoteDir(c, name).listFiles()) {
			if (f.getName().endsWith(BUTTON_EXTENSION)) {
				Button b = buttonFromFile(gson, f);
				remote.addButton(b);
			}
		}
		return remote;
	}

	private static Button buttonFromFile(Gson gson, File f) {
		return gson.fromJson(FileUtils.get(f), Button.class);
	}

	private void buttonToFile(Gson gson, Context c, File file, Button b) {
		FileUtils.put(file, gson.toJson(b));
	}

	public static void remove(Context c, String name) {
		FileUtils.remove(getRemoteDir(c, name));
	}

	/** Save this remote to the file system */
	public void save(Context c) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		File dir = getRemoteDir(c);
		FileUtils.clear(dir);
		for (Button b : buttons) {
			if (b.id != 0) {
				// File f = getNextFile(dir, BUTTON_PREFIX, BUTTON_EXTENSION);
				File f = new File(dir, BUTTON_PREFIX + b.id + BUTTON_EXTENSION);
				buttonToFile(gson, c, f, b);
			}
		}
	}

	private static final File getNextFile(File dir, String prefix, String suffix) {
		List<String> list = Arrays.asList(dir.list());
		for (int i = 0;; i++) {
			if (!list.contains(prefix + i + suffix)) {
				return new File(dir, prefix + i + suffix);
			}
		}
	}

	public static List<String> getNames(Context c) {
		List<String> result = new ArrayList<String>();
		File dir = getRemotesDir(c);
		for (String s : dir.list()) {
			if (s.endsWith(EXTENSION)) {
				result.add(s.substring(0, s.length() - EXTENSION.length()));
			}
		}
		return result;
	}

	/** True if this remote contains the specified button */
	public boolean contains(boolean common, int id) {
		for (int i = 0; i < buttons.size(); i++) {
			if (buttons.get(i).id == id) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return The matching button or null if no such button found
	 */
	public Button getButton(boolean common, int id) {
		for (int i = 0; i < buttons.size(); i++) {
			if (buttons.get(i).id == id) {
				return buttons.get(i);
			}
		}
		return null;
	}

	public void addButton(Button b) {
		// Remove first, if already present
		// This will not affect other buttons
		buttons.remove(b);
		buttons.add(b);
	}

	/**
	 * Add a button to a remote and save it to disk If you call save on another
	 * remote with the same name which was loaded earlier, you will overwrite
	 * this changes<br>
	 * Please note that this is highly inefficient for adding multiple buttons.
	 * Load the remote, and add the buttons manually instead of using this.
	 * 
	 * @param c
	 * @param remote
	 * @param b
	 */
	public static void addButton(Context c, String remote, Button b) {
		Remote r = Remote.load(c, remote);
		r.addButton(b);
		r.save(c);
	}
}
