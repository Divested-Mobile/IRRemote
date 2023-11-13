package org.twinone.irremote;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.Preference;
import android.util.AttributeSet;

public class VersionPreference extends Preference {

    public VersionPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getVersion();
    }

    public VersionPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        getVersion();
    }

    public VersionPreference(Context context) {
        super(context);
        getVersion();
    }

    protected void getVersion() {
        try {
            String name = getContext().getPackageName();
            PackageInfo info = getContext().getPackageManager().getPackageInfo(name, 0);
            setTitle(getContext().getString(R.string.version_preference_format, info.versionName, info.versionCode));
        } catch (PackageManager.NameNotFoundException ignored) {
        }
    }
}
