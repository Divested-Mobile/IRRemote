package org.twinone.irremote.providers.globalcache;

import java.io.Serializable;

import org.twinone.irremote.providers.BaseListable;
import org.twinone.irremote.util.SimpleCache;

import android.content.Context;
import android.net.Uri;

import com.google.gson.Gson;

public class GlobalCacheProviderData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8091426297558105438L;

	// API key that was generated using email address twinonetest@gmail.com
	private static final String API_KEY = "aafb55e5-528d-4efe-8330-51b82fc3ba18";
	private static final String BASE_URL = "http://irdatabase.globalcache.com/api/v1/"
			+ API_KEY;

	private static final String URL_MANUFACTURERS = "manufacturers";
	private static final String URL_DEVICE_TYPES = "devicetypes";
	private static final String URL_CODESETS = "codesets";

	/** A manufacturer */
	public static final int TYPE_MANUFACTURER = 0;
	/** A device type or category (such as TV or Cable */
	public static final int TYPE_DEVICE_TYPE = 1;
	/** A specific remote / button list */
	public static final int TYPE_CODESET = 2;
	/** An IR code */
	public static final int TYPE_IR_CODE = 3;

	// The type we want to query
	public int targetType = TYPE_MANUFACTURER;
	public Manufacturer manufacturer;
	public DeviceType deviceType;
	public Codeset codeset;

	public String getUrl() {
		Uri.Builder ub = Uri.parse(BASE_URL).buildUpon();
		ub.appendPath(URL_MANUFACTURERS);
		if (targetType == TYPE_MANUFACTURER)
			return ub.build().toString();
		ub.appendPath(manufacturer.Key);
		ub.appendPath(URL_DEVICE_TYPES);
		if (targetType == TYPE_DEVICE_TYPE)
			return ub.build().toString();
		ub.appendPath(deviceType.Key);
		ub.appendPath(URL_CODESETS);
		if (targetType == TYPE_CODESET)
			return ub.build().toString();
		ub.appendPath(codeset.Key);
		return ub.build().toString();
	}

	public String getFullyQualifiedName(String separator) {
		StringBuilder sb = new StringBuilder();
		if (targetType == TYPE_MANUFACTURER)
			return null;
		sb.append(manufacturer.Manufacturer);
		if (targetType == TYPE_DEVICE_TYPE)
			return sb.toString();
		sb.append(separator).append(deviceType.DeviceType);
		if (targetType == TYPE_CODESET)
			return sb.toString();
		sb.append(separator).append(codeset.Codeset);
		return sb.toString();

	}

	public String getCacheName() {
		StringBuilder sb = new StringBuilder("GlobalCache");
		if (targetType == TYPE_MANUFACTURER)
			return sb.toString();
		sb.append('_').append(manufacturer.Key);
		if (targetType == TYPE_DEVICE_TYPE)
			return sb.toString();
		sb.append('_').append(deviceType.Key);
		if (targetType == TYPE_CODESET)
			return sb.toString();
		sb.append('_').append(codeset.Key);
		return sb.toString().replaceAll(" ", "_");
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("UriData@" + hashCode() + " Root");
		if (targetType == TYPE_MANUFACTURER)
			return sb.toString();
		sb.append('/').append(manufacturer.Key);
		if (targetType == TYPE_DEVICE_TYPE)
			return sb.toString();
		sb.append('/').append(deviceType.Key);
		if (targetType == TYPE_CODESET)
			return sb.toString();
		sb.append('/').append(codeset.Key);
		return sb.toString();
	}

	/** May be null if no manufacturer has been selected */
	public BaseListable getLastSelectedData() {
		switch (targetType) {
		case TYPE_DEVICE_TYPE:
			return manufacturer;
		case TYPE_CODESET:
			return deviceType;
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

	public GlobalCacheProviderData clone() {
		return deserialize(serialize());
	}

	public String serialize() {
		return new Gson().toJson(this);
	}

	public static GlobalCacheProviderData deserialize(String data) {
		return new Gson().fromJson(data, GlobalCacheProviderData.class);
	}

}