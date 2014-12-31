package org.twinone.irremote.ir;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import org.twinone.irremote.R;
import org.twinone.irremote.ui.SettingsActivity;

import java.util.Locale;

/**
 * Utility class to solve the errors of the crappy devs that implemented IR
 * signals in Samsung and Cyanogenmod devices
 */
public class SignalCorrector {

    private final Context mContext;
    private Boolean mAffected = null;

    public SignalCorrector(Context c) {
        mContext = c;
    }

    private static void setPreference(Context c, boolean value) {
        SettingsActivity.getPreferences(c).edit()
                .putBoolean(c.getString(R.string.pref_key_fix), value).apply();
    }

    public static void setAffectedOnce(Context c) {
        if (SettingsActivity.getPreferences(c).contains(
                c.getString(R.string.pref_key_fix)))
            return;
        // It looks like CyanogenMod is no longer affected...
        boolean sams = isAffectedSamsung();
        boolean cyan = isCyanogen(c);
        Log.d("SignalCorrector", "Sams: " + sams + " cyan: " + cyan);
        boolean affected = isAffectedSamsung() && !isCyanogen(c);
        setPreference(c, affected);
    }

    private static boolean isCyanogen(Context c) {
        if (c.getPackageManager().hasSystemFeature("com.cyanogenmod.android"))
            return true;
        if (System.getProperty("os.version").contains("cyanogenmod"))
            return true;

        return Build.USER.toLowerCase(Locale.ENGLISH).contains("shade");

    }

    private static boolean isAffectedSamsung() {
        if (!getManufacturer().contains("samsung"))
            return false;
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.KITKAT)
            return false;
        return getVersionMR() < 3;
    }

    private static String getManufacturer() {
        return android.os.Build.MANUFACTURER.toLowerCase(Locale.ENGLISH);

    }

    private static int getVersionMR() {
        try {
            int lastIdx = Build.VERSION.RELEASE.lastIndexOf(".");
            return Integer.valueOf(Build.VERSION.RELEASE
                    .substring(lastIdx + 1));
        } catch (Exception e) {
            return 0;
        }
    }

    public int[] fix(int frequency, int[] patternInPulses) {
        if (isAffected()) {
            return patternInPulses;
        }
        int[] patternInUSecs = new int[patternInPulses.length];
        float pulsePeriod = (float) 1000000 / frequency;
        for (int i = 0; i < patternInPulses.length; i++) {
            patternInUSecs[i] = (int) (patternInPulses[i] * pulsePeriod);
        }
        return patternInUSecs;
    }

    /**
     * Returns true if this version is affected by the <a href=
     * "http://webcache.googleusercontent.com/search?q=cache:wIZ5rrokRKMJ:developer.samsung.com/android/technical-docs/workaround-to-solve-issues-with-the-consumerirmanager-in-android-version-lower-than-4-4-3-kitkat+&cd=3&hl=en&ct=clnk&gl=es"
     * >bug</a>
     *
     * @return
     */
    private boolean isAffected() {
        return getPreference();
        // if (mAffected == null) {
        // mAffected = getPreference();
        // }
        // Log.w("", "mAffected: " + mAffected);
        // return mAffected;
    }

    private boolean getPreference() {
        return SettingsActivity.getPreferences(mContext).getBoolean(
                mContext.getString(R.string.pref_key_fix), false);
    }
}
