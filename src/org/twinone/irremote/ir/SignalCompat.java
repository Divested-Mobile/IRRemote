package org.twinone.irremote.ir;

import java.util.Locale;

import android.content.Context;
import android.os.Build;
import android.util.Log;

/**
 * Utility class to solve the errors of the crappy devs that implemented IR
 * signals in Samsung and Cyanogenmod devices
 * 
 */
public class SignalCompat {

	private Context mContext;

	public SignalCompat(Context c) {
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

	private static Boolean mAffected = null;

	/**
	 * Returns true if this version is affected by the <a href=
	 * "http://webcache.googleusercontent.com/search?q=cache:wIZ5rrokRKMJ:developer.samsung.com/android/technical-docs/workaround-to-solve-issues-with-the-consumerirmanager-in-android-version-lower-than-4-4-3-kitkat+&cd=3&hl=en&ct=clnk&gl=es"
	 * >bug</a>
	 * 
	 * @return
	 */
	private boolean isAffected() {
		if (mAffected != null) {
			return mAffected;
		}

		// all cyanogenmod versions are affected
		boolean cyan = isCyanogen();
		// samsung 4.4.2 and lower are affected
		boolean sams = isAffectedSamsung();
		// my htc one is NOT affected

		Log.i("", "isAffectedSamsung: " + sams + " isAffectedCyan: " + cyan);
		return cyan || sams;

	}

	private boolean isCyanogen() {
		Log.d("", "os.version: " + System.getProperty("os.version"));
		if (mContext.getPackageManager().hasSystemFeature(
				"com.cyanogenmod.android"))
			return true;
		if (System.getProperty("os.version").contains("cyanogenmod"))
			return true;

		if (Build.USER.toLowerCase(Locale.ENGLISH).contains("shade")) {
			return true;
		}

		return false;
	}

	private boolean isAffectedSamsung() {
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
