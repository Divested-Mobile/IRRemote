package org.twinone.irremote.components;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.twinone.irremote.R;
import org.twinone.irremote.ui.MainNavFragment;
import org.twinone.irremote.util.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
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
    private static final String FILE_PROVIDER_AUTHORITY = "org.twinone.irremote.fileprovider";

    public final List<Button> buttons;
    public String name;
    public Details details;

    public Remote() {
        buttons = new ArrayList<>();
        details = new Details();
    }

    private Remote(Context context, String remoteName) {
        this();
        if (remoteName == null)
            throw new IllegalArgumentException("Name cannot be null");
        this.name = remoteName;
        final Gson gson = new Gson();
        File optfile = new File(getRemoteDir(context), OPTIONS_FILE);
        details = gson.fromJson(FileUtils.read(optfile), Details.class);
        for (final File file : getRemoteDir(context, remoteName).listFiles()) {
            if (file.getName().endsWith(BUTTON_EXTENSION)) {
                Button button = gson.fromJson(FileUtils.read(file), Button.class);
                addButton(button);
            }
        }
    }

    // OK
    private static File getRemotesDir(Context context) {
        final File dir = new File(context.getFilesDir(), "remotes" + REMOTES_VERSION);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    private static File getRemoteDir(Context context, String remoteName) {
        File dir = new File(getRemotesDir(context), remoteName + EXTENSION);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    public static boolean exists(Context context, String remoteName) {
        return FileUtils.exists(getRemoteDir(context, remoteName));
    }

    /**
     * Load this menu_main from the file system
     */
    public static Remote load(Context context, String remoteName) {
        if (remoteName == null)
            return null;
        return new Remote(context, remoteName);
    }

    public static void remove(Context context, String remoteName) {
        FileUtils.remove(getRemoteDir(context, remoteName));
    }

    public static void rename(Context context, String oldName, String newName) {
        if (oldName.equals(newName) || newName.trim().isEmpty()) {
            return;
        }
        FileUtils.rename(getRemoteDir(context, oldName), getRemoteDir(context, newName));
    }

    public static List<String> getNames(Context context) {
        List<String> result = new ArrayList<>();
        File dir = getRemotesDir(context);
        for (String str : dir.list()) {
            if (str.endsWith(EXTENSION)) {
                result.add(str.substring(0, str.length() - EXTENSION.length()));
            }
        }
        return result;
    }

    /** Load details from disk */
    public static Remote.Details loadDetails(Context context, String remoteName) {
        File dir = getRemoteDir(context, remoteName);
        File file = new File(dir, OPTIONS_FILE);
        return new Gson().fromJson(FileUtils.read(file), Details.class);
    }

    /**
     * @return the persisted selected menu_main or null if it was not set
     */
    public static String getPersistedRemoteName(Context context) {
        return context.getSharedPreferences("remote", Context.MODE_PRIVATE)
                .getString(MainNavFragment.PREF_KEY_LAST_REMOTE, null);
    }

    /**
     * Set the menu_main selected by the user
     */
    public static void setLastUsedRemoteName(Context context, String remoteName) {
        context.getSharedPreferences("remote", Context.MODE_PRIVATE).edit()
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
    public static void addButton(Context context, String remoteName, Button button) {
        Remote remote = Remote.load(context, remoteName);
        remote.addButton(button);
        remote.save(context);
    }

    private static String serialize(Remote remote) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(remote);
    }

    public static Remote deserialize(String remoteData) {
        return new Gson().fromJson(remoteData, Remote.class);
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
    private File getRemoteDir(Context context) {
        return getRemoteDir(context, this.name);
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
    public void save(Context context) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File dir = getRemoteDir(context);
        FileUtils.clear(dir);
        for (Button button : buttons) {
            if (button != null) {
                File file = new File(dir, BUTTON_PREFIX + button.uid + BUTTON_EXTENSION);
                FileUtils.write(file, gson.toJson(button));
            }
        }

        File file = new File(dir, OPTIONS_FILE);
        FileUtils.write(file, gson.toJson(details));
    }

    public static void writeFileToExport(Context context, String remoteName, @Nullable Intent intent) {
        String remoteData = load(context, remoteName).serialize();
        try {
            OutputStream ostream = context.getContentResolver().openOutputStream(intent.getData());
            ostream.write(remoteData.getBytes(), 0, remoteData.length());
            ostream.flush();
            ostream.close();
        } catch (Exception e) {
            Toast.makeText(context,
                    R.string.export_remote_failed, Toast.LENGTH_SHORT).show();
        }
    }

    @Nullable
    public static Uri writeFileToShare(Context context, @NonNull String remoteName) {
        String remoteFileName = remoteName.replace(" ", "_") + ".txt";
        String remoteData = load(context, remoteName).serialize();
        File file = new File(context.getFilesDir(), remoteFileName);
        try {
            FileOutputStream ostream = context.openFileOutput(remoteFileName, Context.MODE_PRIVATE);
            ostream.write(remoteData.getBytes(), 0, remoteData.length());
            ostream.flush();
            ostream.close();
            return FileProvider.getUriForFile(context, FILE_PROVIDER_AUTHORITY, file);
        } catch (Exception e) {
            Toast.makeText(context,
                    R.string.share_remote_failed, Toast.LENGTH_SHORT).show();
            return null;
        }
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
     * @param button
     */
    public void addButton(Button button) {
        button.uid = getNextId();
        buttons.add(button);
    }

    /**
     * Strips buttons from this remote that have a null or empty code
     */
    public void stripInvalidButtons() {
        Iterator<Button> it = buttons.iterator();
        while (it.hasNext()) {
            final Button button = it.next();
            if (button.code == null || button.code.isEmpty()) {
                it.remove();
            }
        }
    }
    /**
     * Replace a button by another one
     */
    public void replaceButton(Button button) {
        buttons.add(button);
    }

    public void removeButton(Button button) {
        buttons.remove(button);
    }

    private int getNextId() {
        int id = 1;
        while (this.contains(id)) {
            id++;
        }
        return id;
    }

    @NonNull
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
        public int h; // px
        public int w;
        public int flags;
        public int marginTop;
        public int marginLeft;
        // Specify if this remote should be reorganized
        // Notably need to be set to false for special layout to be preserved when loading remotes from JSON files rather than just button definitions
        public Boolean organize = true;
    }
}
