package org.twinone.irremote.providers.twinone;

import java.util.Locale;

import org.twinone.androidlib.versionmanager.VersionManager;
import org.twinone.irremote.components.Remote;

import android.content.Context;
import android.os.Build;
import android.provider.Settings.Secure;

import com.google.gson.Gson;

public class TransferableRemoteDetails {

	public TransferableRemoteDetails(Context c) {
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

	// Device info
	public String manufacturer;
	public String device;
	public int androidVersion;
	public int appVersion;
	public String androidId;

	// Info about user
	public String language;

	// TODO
	// country?
	// use email as id?

	@Override
	public String toString() {
		return serialize();
	}

	public String serialize() {
		return new Gson().toJson(this);
	}

	public static TransferableRemoteDetails deserialize(String s) {
		return new Gson().fromJson(s, TransferableRemoteDetails.class);
	}
}
