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
		options = new Options();
	}

	public String name;

	public List<Button> buttons;

	public static final int DEVICE_TYPE_TV = 0;
	public static final int DEVICE_TYPE_CABLE = 1;
	public static final int DEVICE_TYPE_BLURAY = 2;

	public Options options;

	public static class Options implements Serializable {
		/**
		 * The type of remote this is<br>
		 * one of {@link Remote#DEVICE_TYPE_BLURAY},
		 * {@link Remote#DEVICE_TYPE_CABLE} or {@link Remote#DEVICE_TYPE_TV}
		 */
		public int type;
	}

	private static final String REMOTES_VERSION = "_v2";
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

	private static final String OPTIONS_FILE = "remote.options";

	// Constructor from a file
	private Remote(Context c, String remoteName) {
		this();
		if (remoteName == null)
			throw new IllegalArgumentException("Name cannot be null");
		this.name = remoteName;
		final Gson gson = new Gson();
		File optfile = new File(getRemoteDir(c), OPTIONS_FILE);
		options = gson.fromJson(FileUtils.read(optfile), Options.class);
		for (final File f : getRemoteDir(c, remoteName).listFiles()) {
			if (f.getName().endsWith(BUTTON_EXTENSION)) {
				Button b = gson.fromJson(FileUtils.read(f), Button.class);
				addButton(b);
			}
		}
	}

	/** Load this remote from the file system */
	public static Remote load(Context c, String name) {
		if (name == null)
			return null;
		return new Remote(c, name);
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
				FileUtils.write(f, gson.toJson(b));
			}
		}
		File f = new File(dir, OPTIONS_FILE);
		FileUtils.write(f, gson.toJson(options));
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
