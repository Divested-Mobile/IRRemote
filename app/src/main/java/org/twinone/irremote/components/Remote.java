package org.twinone.irremote.components;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import org.twinone.irremote.ui.MainNavFragment;
import org.twinone.irremote.util.FileUtils;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class Remote implements Serializable {

    public static final int TYPE_UNKNOWN = -1;
    public static final int TYPE_TV = 0;
    public static final int TYPE_CABLE = 1;
    public static final int TYPE_CD = 2;
    public static final int TYPE_DVD = 3;
    public static final int TYPE_BLU_RAY = 4;
    public static final int TYPE_AUDIO = 5;
    public static final int TYPE_CAMERA = 6;
    public static final int TYPE_AIR_CON = 7;

    public static final int FLAG_LEARNED = 1;
    /**
     * This remote is from the globalcache database
     */
    public static final int FLAG_GC = 1 << 1;

    private static final String REMOTES_VERSION = "_v2";
    private static final String EXTENSION = ".remote";
    private static final String BUTTON_EXTENSION = ".button";
    private static final String BUTTON_PREFIX = "b_";
    private static final String OPTIONS_FILE = "remote.options";
    public final List<Button> buttons;
    public String name;
    public Details details;

    public Remote() {
        buttons = new ArrayList<>();
        details = new Details();
    }

    private Remote(Context c, String remoteName) {
        this();
        if (remoteName == null)
            throw new IllegalArgumentException("Name cannot be null");
        this.name = remoteName;
        final Gson gson = new Gson();
        File optfile = new File(getRemoteDir(c), OPTIONS_FILE);
        details = gson.fromJson(FileUtils.read(optfile), Details.class);
        for (final File f : getRemoteDir(c, remoteName).listFiles()) {
            if (f.getName().endsWith(BUTTON_EXTENSION)) {
                Button b = gson.fromJson(FileUtils.read(f), Button.class);
                addButton(b);
            }
        }
    }

    // OK
    private static File getRemotesDir(Context c) {
        final File dir = new File(c.getFilesDir(), "remotes" + REMOTES_VERSION);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
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

    /**
     * Load this menu_main from the file system
     */
    public static Remote load(Context c, String name) {
        if (name == null)
            return null;
        return new Remote(c, name);
    }

    public static void remove(Context c, String name) {
        FileUtils.remove(getRemoteDir(c, name));
    }

    public static void rename(Context c, String oldName, String newName) {
        if (oldName.equals(newName) || newName.trim().isEmpty()) {
            return;
        }
        FileUtils.rename(getRemoteDir(c, oldName), getRemoteDir(c, newName));
    }

    public static List<String> getNames(Context c) {
        List<String> result = new ArrayList<>();
        File dir = getRemotesDir(c);
        for (String s : dir.list()) {
            if (s.endsWith(EXTENSION)) {
                result.add(s.substring(0, s.length() - EXTENSION.length()));
            }
        }
        return result;
    }

    /** Load details from disk */
    public static Remote.Details loadDetails(Context c, String remoteName) {
        File dir = getRemoteDir(c, remoteName);
        File f = new File(dir, OPTIONS_FILE);
        return new Gson().fromJson(FileUtils.read(f), Details.class);
    }

    /**
     * @return the persisted selected menu_main or null if it was not set
     */
    public static String getPersistedRemoteName(Context c) {
        return c.getSharedPreferences("remote", Context.MODE_PRIVATE)
                .getString(MainNavFragment.PREF_KEY_LAST_REMOTE, null);
    }

    /**
     * Set the menu_main selected by the user
     */
    public static void setLastUsedRemoteName(Context c, String remoteName) {
        c.getSharedPreferences("remote", Context.MODE_PRIVATE).edit()
                .putString(MainNavFragment.PREF_KEY_LAST_REMOTE, remoteName)
                .apply();
    }

    /**
     * Add a button to a menu_main and save it to disk If you call save on another
     * menu_main with the same name which was loaded earlier, you will overwrite
     * this changes<br>
     * Please note that this is highly inefficient for adding multiple buttons.
     * Load the menu_main, and add the buttons manually instead of using this.
     */
    public static void addButton(Context c, String remote, Button b) {
        Remote r = Remote.load(c, remote);
        r.addButton(b);
        r.save(c);
    }

    private static String serialize(Remote remote) {
        return new Gson().toJson(remote);
    }

    public static Remote deserialize(String remote) {
        return new Gson().fromJson(remote, Remote.class);
    }

    /**
     * Add flags to this menu_main, you can OR them together
     *
     * @param flags
     */
    public void addFlags(int flags) {
        details.flags |= flags;
    }

    /**
     * Remove flags from this menu_main, you can OR them together
     *
     * @param flags
     */
    public void removeFlags(int flags) {
        details.flags &= ~flags;
    }

    /**
     * Calls {@link #getRemoteDir(Context, String)} for this menu_main
     */
    private File getRemoteDir(Context c) {
        return getRemoteDir(c, name);
    }

    public void sortButtonsById() {
        Collections.sort(buttons, new Comparator<Button>() {
            @Override
            public int compare(Button lhs, Button rhs) {
                if (lhs.id == rhs.id)
                    return 0;
                return lhs.id < rhs.id ? -1 : 1;
            }
        });
    }

    /**
     * Save this menu_main to the file system
     */
    public void save(Context c) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File dir = getRemoteDir(c);
        FileUtils.clear(dir);
        for (Button b : buttons) {
            if (b != null) {
                File f = new File(dir, BUTTON_PREFIX + b.uid + BUTTON_EXTENSION);
                FileUtils.write(f, gson.toJson(b));
            }
        }

        File f = new File(dir, OPTIONS_FILE);
        FileUtils.write(f, gson.toJson(details));
    }

    /**
     * True if this menu_main contains the specified button
     */
    boolean contains(int uid) {
        for (int i = 0; i < buttons.size(); i++) {
            if (buttons.get(i).uid == uid) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get a button by it's ID rather than UID
     *
     * @param id
     * @return
     */
    public Button getButtonById(int id) {
        // if (id == Button.ID_UNKNOWN)
        // return null;
        for (int i = 0; i < buttons.size(); i++) {
            if (buttons.get(i).id == id) {
                return buttons.get(i);
            }
        }
        return null;
    }

    /**
     * @return The matching button (by uid) or null if no such button found
     */
    public Button getButton(int uid) {
        for (int i = 0; i < buttons.size(); i++) {
            if (buttons.get(i).uid == uid) {
                return buttons.get(i);
            }
        }
        return null;
    }

    /**
     * Add a button and automatically generate it's uid
     *
     * @param b
     */
    public void addButton(Button b) {
        b.uid = getNextId();
        buttons.add(b);
    }

    /**
     * Strips buttons from this remote that have a null or empty code
     */
    public void stripInvalidButtons() {
        Iterator<Button> it = buttons.iterator();
        while (it.hasNext()) {
            final Button b = it.next();
            if (b.code == null || b.code.isEmpty()) {
                it.remove();
            }
        }
    }
    /**
     * Replace a button by another one
     */
    public void replaceButton(Button b) {
        buttons.add(b);
    }

    public void removeButton(Button b) {
        buttons.remove(b);
    }

    private int getNextId() {
        int id = 1;
        while (this.contains(id)) {
            id++;
        }
        return id;
    }

    @Override
    public String toString() {
        return serialize();
    }

    public String serialize() {
        return serialize(this);
    }

    public static class Details implements Serializable {
        private static final long serialVersionUID = -6674520681482052007L;
        public int type;
        /**
         * Used if the user indicates the menu_main is for some strange device type
         */
        @SerializedName("type_string")
        public String typeString;
        public String manufacturer;
        public String model;
        public int h; // px
        public int w;
        public int flags;
        public int marginTop;
        public int marginLeft;
        long id;
        /**
         * If this menu_main was forked from another, this is the parent's id
         */
        @SerializedName("parent_id")
        long parentId;
    }
}
