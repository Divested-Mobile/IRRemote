package org.twinone.irremote.providers.twinone;

import android.content.Context;
import android.os.Build;
import android.provider.Settings.Secure;

import org.twinone.androidlib.versionmanager.VersionManager;

import java.util.Locale;

public class DeviceInfo {

    public String manufacturer;
    public String device;
    public int androidVersion;
    public int appVersion;
    public String androidId;
    public String language;
    public DeviceInfo(Context c) {
        androidId = Secure.getString(c.getContentResolver(), Secure.ANDROID_ID);
        language = Locale.getDefault().getLanguage();
        androidVersion = Build.VERSION.SDK_INT;
        appVersion = VersionManager.getManifestVersion(c);
        manufacturer = Build.MANUFACTURER;
        device = Build.DEVICE;
    }

}
