package org.twinone.irremote.globalcache;

import java.io.Serializable;

import org.twinone.irremote.R;

import android.content.Context;

import com.google.gson.Gson;

public class UriData implements Serializable {

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

	public static final int TYPE_MANUFACTURER = 0;
	public static final int TYPE_DEVICE_TYPE = 1;
	public static final int TYPE_CODESET = 2;
	public static final int TYPE_IR_CODE = 3;

	// The type we want to query
	public int targetType = TYPE_MANUFACTURER;
	public Manufacturer manufacturer;
	public DeviceType deviceType;
	public Codeset codeset;

	public String getUrl() {
		StringBuilder sb = new StringBuilder(BASE_URL);
		sb.append('/').append(URL_MANUFACTURERS);
		if (targetType == TYPE_MANUFACTURER)
			return sb.toString();
		sb.append('/').append(manufacturer.Key);
		sb.append('/').append(URL_DEVICE_TYPES);
		if (targetType == TYPE_DEVICE_TYPE)
			return sb.toString();
		sb.append('/').append(deviceType.Key);
		sb.append('/').append(URL_CODESETS);
		if (targetType == TYPE_CODESET)
			return sb.toString();
		sb.append('/').append(codeset.Key);
		return sb.toString();
	}

	public String getTitle(Context c, String separator) {
		StringBuilder sb = new StringBuilder();
		if (targetType == TYPE_MANUFACTURER)
			return c.getString(R.string.db_select_manufacturer);
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
		return sb.toString();
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

	public boolean removeFromCache(Context c) {
		return SimpleCache.remove(c, getCacheName());
	}

	public boolean isAvailableInCache(Context c) {
		return SimpleCache.isAvailable(c, getCacheName());
	}

	public UriData clone() {
		return deserialize(serialize());
	}

	public String serialize() {
		return new Gson().toJson(this);
	}

	public static UriData deserialize(String data) {
		return new Gson().fromJson(data, UriData.class);
	}

}