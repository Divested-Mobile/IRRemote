package org.twinone.irremote.ir;

import java.util.Locale;

import org.twinone.irremote.R;
import org.twinone.irremote.ui.SettingsActivity;

import android.content.Context;
import android.os.Build;
import android.util.Log;

/**
 * Utility class to solve the errors of the crappy devs that implemented IR
 * signals in Samsung and Cyanogenmod devices
 * 
 */
public class SignalCorrector {

	private Context mContext;

	public SignalCorrector(Context c) {
		mContext = c;
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

	private Boolean mAffected = null;

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

	private static void setPreference(Context c, boolean value) {
		SettingsActivity.getPreferences(c).edit()
				.putBoolean(c.getString(R.string.pref_key_fix), value).apply();
	}

	public static void setAffectedOnce(Context c) {
		if (SettingsActivity.getPreferences(c).contains(
				c.getString(R.string.pref_key_fix)))
			return;
		boolean affected = isCyanogen(c) || isAffectedSamsung();
		Log.w("SignalCompat", "Setting afected: " + affected);
		setPreference(c, affected);
	}

	private static boolean isCyanogen(Context c) {
		Log.d("", "os.version: " + System.getProperty("os.version"));
		if (c.getPackageManager().hasSystemFeature("com.cyanogenmod.android"))
			return true;
		if (System.getProperty("os.version").contains("cyanogenmod"))
			return true;

		if (Build.USER.toLowerCase(Locale.ENGLISH).contains("shade"))
			return true;

		return false;
	}

	public static boolean isAffectedSamsung() {
		if (!getManufacturer().contains("samsung"))
			return false;
		if (Build.VERSION.SDK_INT != Build.VERSION_CODES.KITKAT)
			return false;
		if (getVersionMR() < 3)
			return true;
		return false;
	}

	private static String getManufacturer() {
		return android.os.Build.MANUFACTURER.toLowerCase(Locale.ENGLISH);

	}

	private static int getVersionMR() {
		int lastIdx = Build.VERSION.RELEASE.lastIndexOf(".");
		int versionmr = Integer.valueOf(Build.VERSION.RELEASE
				.substring(lastIdx + 1));
		return versionmr;

	}
}
