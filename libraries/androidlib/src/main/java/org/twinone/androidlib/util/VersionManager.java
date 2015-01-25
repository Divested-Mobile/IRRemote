package org.twinone.androidlib.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.util.Log;

/**
 * This class provides a simple mechanism to detect the update and first run of
 * your app<br>
 * Just implement {@link OnUpdateListener} and call
 * {@link #callFromEntryPoint()}
 *
 * @author twinone
 * @see OnUpdateListener
 * @see #callFromEntryPoint()
 */
public class VersionManager {
    private static final String PREFS_NAME = "org.twinone.androidlib.versionmanager.prefs";
    private static final String KEY_LAST_VERSION = "org.twinone.androidlib.versionmanager.last_version";

    private Context mContext;
    private OnUpdateListener mListener;

    public VersionManager(Context context, OnUpdateListener listener) {
        if (context == null) {
            throw new NullPointerException("Null context");
        }
        if (listener == null) {
            throw new NullPointerException("Null listener");
        }

        mContext = context;
        mListener = listener;
    }

    public static int getManifestVersion(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            return UpdateInfo.VERSION_UNKNOWN;
        }
    }

    private SharedPreferences prefs() {
        return mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Call this method from the entry point of your app (a good place would be
     * the onCreate method of your MainActivity)
     */
    public void callFromEntryPoint() {
        final int current = getManifestVersion();
        final int last = getLastKnownInstalledVersion();
        MyUpdateInfo ui = new MyUpdateInfo();
        ui.currentVersion = current;
        ui.lastKnownVersion = last;
        Log.d("TEST", "current: " + current + " last: " + last);
        if (current > last) {
            mListener.onUpdate(ui);
            setLastKnownInstalledVersion(current);
        }
    }

    int getLastKnownInstalledVersion() {
        return prefs().getInt(KEY_LAST_VERSION, UpdateInfo.VERSION_UNKNOWN);
    }

    @SuppressLint("NewApi")
    void setLastKnownInstalledVersion(int value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            prefs().edit().putInt(KEY_LAST_VERSION, value).apply();
        } else {
            prefs().edit().putInt(KEY_LAST_VERSION, value).commit();
        }
    }

    /**
     * Gets the versionCode from AndroidManifest.xml<br>
     * This is the current version installed on the device.
     *
     */
    int getManifestVersion() {
        return getManifestVersion(mContext);
    }

    public static interface OnUpdateListener {

        /**
         * Gets called when the user has installed or updated the app<br>
         * If last is 0 the user has just installed the app
         */
        public void onUpdate(UpdateInfo info);
    }

    public interface UpdateInfo {
        public static final int VERSION_UNKNOWN = -1;

        /**
         * @return The current Android versionCode as declared in the
         * AndroidManifest file
         */
        public int getCurrentVersion();

        /**
         * @return the last known previous version of this app or
         * {@link #VERSION_UNKNOWN}
         */
        public int getLastVersion();

        /**
         * @return True if this app has been updated at least once (this method
         * works correctly in the
         * {@link VersionManager.OnUpdateListener#onUpdate(org.twinone.androidlib.versionmanager.VersionManager.UpdateInfo)}
         * method, but will report incorrect results if used outside)
         */
        public boolean isUpdated();

    }

    public static class MyUpdateInfo implements UpdateInfo {

        private int currentVersion = VERSION_UNKNOWN;
        private int lastKnownVersion = VERSION_UNKNOWN;

        public int getCurrentVersion() {
            return currentVersion;
        }

        public int getLastVersion() {
            return lastKnownVersion;
        }

        public boolean isUpdated() {
            return lastKnownVersion != VERSION_UNKNOWN;
        }

    }

}
