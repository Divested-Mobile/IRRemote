package org.twinone.irremote.providers.lirc;

import java.io.Serializable;

import org.twinone.irremote.util.SimpleCache;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

public class LircProviderData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8091426297558105438L;

	// API key that was generated using email address twinonetest@gmail.com
	private static final String BASE_URL = "http://lirc.sourceforge.net/remotes/";

	/** A manufacturer */
	public static final int TYPE_MANUFACTURER = 0;
	/** A specific remote / button list */
	public static final int TYPE_CODESET = 2;
	/** An IR code */
	public static final int TYPE_IR_CODE = 3;

	// The type we want to query
	public int targetType = TYPE_MANUFACTURER;
	public String manufacturer;
	public String codeset;

	public String getUrl() {
		final String fqn = getFullyQualifiedName("/");
		Log.d("", "getUrl: " + fqn);
		return fqn == null ? BASE_URL : BASE_URL + fqn;
	}

	// never null
	public String getFullyQualifiedName(String separator) {
		StringBuilder sb = new StringBuilder();
		if (targetType == TYPE_MANUFACTURER)
			return null;
		sb.append(manufacturer);
		if (targetType == TYPE_CODESET)
			return sb.toString();
		sb.append(separator).append(codeset);
		return sb.toString();
	}

	public String getCacheName() {
		final String fqn = getFullyQualifiedName("_");
		return fqn == null ? "LIRC" : "LIRC_" + fqn;
	}

	@Override
	public String toString() {
		return getFullyQualifiedName("/");
	}

	/** May be null if no manufacturer has been selected */
	public String getLastSelectedData() {
		switch (targetType) {
		case TYPE_CODESET:
			return manufacturer;
		case TYPE_IR_CODE:
			return codeset;
		default:
			return null;
		}
	}

	public void removeFromCache(Context c) {
		SimpleCache.remove(c, getCacheName());
	}

	public boolean isAvailableInCache(Context c) {
		return SimpleCache.isAvailable(c, getCacheName());
	}

	public LircProviderData clone() {
		return deserialize(serialize());
	}

	public String serialize() {
		return new Gson().toJson(this);
	}

	public static LircProviderData deserialize(String data) {
		return new Gson().fromJson(data, LircProviderData.class);
	}

}