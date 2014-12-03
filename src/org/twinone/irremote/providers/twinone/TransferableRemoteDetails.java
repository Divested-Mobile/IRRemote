package org.twinone.irremote.providers.twinone.upload;

import java.util.Locale;

import org.twinone.androidlib.versionmanager.VersionManager;
import org.twinone.irremote.components.Remote;

import android.content.Context;
import android.os.Build;
import android.provider.Settings.Secure;

import com.google.gson.Gson;

public class UploadDetails {

	public UploadDetails(Context c) {
		androidId = Secure.getString(c.getContentResolver(), Secure.ANDROID_ID);
		language = Locale.getDefault().getLanguage();
		androidVersion = Build.VERSION.SDK_INT;
		appVersion = VersionManager.getManifestVersion(c);
		manufacturer = Build.MANUFACTURER;
		device = Build.DEVICE;
	}

	public void setRemote(Remote remote) {
		this.remote = remote;
	}

	public Remote remote;

	// Uploader details
	/** Android manufacturer */
	public String manufacturer;
	public String device;
	public String nick;
	public String language;
	public int androidVersion;
	public int appVersion;
	public String androidId;

	@Override
	public String toString() {
		return toJson();
	}

	public String toJson() {
		return new Gson().toJson(this);
	}
}
