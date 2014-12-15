package org.twinone.irremote.providers.twinone;

import java.util.Locale;

import org.twinone.androidlib.versionmanager.VersionManager;

import android.content.Context;
import android.os.Build;
import android.provider.Settings.Secure;

public class DeviceInfo {

	public DeviceInfo(Context c) {
		androidId = Secure.getString(c.getContentResolver(), Secure.ANDROID_ID);
		language = Locale.getDefault().getLanguage();
		androidVersion = Build.VERSION.SDK_INT;
		appVersion = VersionManager.getManifestVersion(c);
		manufacturer = Build.MANUFACTURER;
		device = Build.DEVICE;
	}

	public String manufacturer;
	public String device;
	public int androidVersion;
	public int appVersion;
	public String androidId;
	public String language;

}
