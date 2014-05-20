/*
 * Copyright 2014 Luuk Willemsen (Twinone)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.twinone.irremote.globalcache;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;

import org.twinone.irremote.Listable;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

public class DBConnector {

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

	private static final String TAG = "DBConnector";

	public void setOnDataReceivedListener(Listener listener) {
		mListener = listener;
	}

	/*
	 * Listener that will provide callbacks to the user
	 */
	private Listener mListener;

	private UriData mUriData = new UriData();

	public static class UriData implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -8091426297558105438L;
		// The type we want to query
		public int target = TYPE_MANUFACTURER;
		public Manufacturer manufacturer;
		public DeviceType deviceType;
		public Codeset codeset;
	}

	public UriData getQueryData() {
		return mUriData;
	}

	public void query(UriData data) {
		mUriData = data;
		if (data == null)
			mUriData = new UriData();
		queryServer();
	}

	private final Context mContext;

	public DBConnector(Context c) {
		this(c, null);
	}

	public DBConnector(Context c, Listener listener) {
		this.mListener = listener;
		this.mContext = c;
	}

	private void queryServer() {
		cancelQuery();
		Log.d(TAG, "queryServer (type=" + mUriData.target + ")");
		mHttpTask = new DBTask(mUriData);
		mHttpTask.execute();
	}

	private AsyncTask<String, Void, String> mHttpTask;

	private class DBTask extends AsyncTask<String, Void, String> {
		private final UriData mUriData;
		private String mUrl;
		private String mCacheName;

		public DBTask(UriData data) {
			mUriData = data;
			mUrl = getUrl(data);
			mCacheName = getCacheName(data);
		}

		private String getCacheName(UriData data) {
			StringBuilder sb = new StringBuilder("GlobalCache");
			if (data.target == TYPE_MANUFACTURER)
				return sb.toString();
			sb.append('_').append(mUriData.manufacturer.Key);
			if (data.target == TYPE_DEVICE_TYPE)
				return sb.toString();
			sb.append('_').append(mUriData.deviceType.Key);
			if (data.target == TYPE_CODESET)
				return sb.toString();
			sb.append('_').append(mUriData.codeset.Key);
			return sb.toString();
		}

		private String getUrl(UriData data) {
			StringBuilder sb = new StringBuilder(BASE_URL);
			sb.append('/').append(URL_MANUFACTURERS);
			if (data.target == TYPE_MANUFACTURER)
				return sb.toString();
			sb.append('/').append(mUriData.manufacturer.Key);
			sb.append('/').append(URL_DEVICE_TYPES);
			if (data.target == TYPE_DEVICE_TYPE)
				return sb.toString();
			sb.append('/').append(mUriData.deviceType.Key);
			sb.append('/').append(URL_CODESETS);
			if (data.target == TYPE_CODESET)
				return sb.toString();
			sb.append('/').append(mUriData.codeset.Key);
			return sb.toString();
		}

		@Override
		protected String doInBackground(String... params) {
			String cached = SimpleCache.get(mContext, mCacheName);
			if (cached != null) {
				Log.i(TAG, "Returning cached version for " + mCacheName);
				return cached;
			}
			// If not cached, load from http
			try {
				Log.d(TAG, "Querying " + mUrl);
				URL url = new URL(mUrl);
				HttpURLConnection urlConnection = (HttpURLConnection) url
						.openConnection();

				InputStream in = new BufferedInputStream(
						urlConnection.getInputStream());
				BufferedReader br = new BufferedReader(
						new InputStreamReader(in));
				StringBuilder data = new StringBuilder();
				String tmp;
				while ((tmp = br.readLine()) != null) {
					data.append(tmp);
				}
				urlConnection.disconnect();
				// Save to cache for future access...
				SimpleCache.put(mContext, mCacheName, data.toString());

				Log.d(TAG, "Done");
				return data.toString();
			} catch (Exception e) {
				Log.w(TAG, "Query to server failed ", e);
				return null;
			}
		}

		@Override
		protected void onPostExecute(String result) {
			Log.d(TAG, "onPostExecute");
			onQueryCompleted(result);
		}

		private void onQueryCompleted(String result) {
			onHttpReceived(mUrl, result);
		}

	}

	public void cancelQuery() {
		if (mHttpTask != null)
			mHttpTask.cancel(true);
	}

	/**
	 * Get a list based on previously selected items
	 * 
	 * @param type
	 */
	public void getList(int type) {
		mUriData.target = type;
		queryServer();
	}

	public void select(Listable listable) {
		mUriData.target = TYPE_MANUFACTURER;
		if (listable != null) {
			if (listable.getType() == TYPE_MANUFACTURER) {
				mUriData.manufacturer = (Manufacturer) listable;
				mUriData.target = TYPE_DEVICE_TYPE;
			} else if (listable.getType() == TYPE_DEVICE_TYPE) {
				mUriData.deviceType = (DeviceType) listable;
				mUriData.target = TYPE_CODESET;
			} else if (listable.getType() == TYPE_CODESET) {
				mUriData.codeset = (Codeset) listable;
				mUriData.target = TYPE_IR_CODE;
			}
		}
	}

	// Result may be null if connection failed!
	public void onHttpReceived(String url, String result) {
		Gson gson = new Gson();
		Object[] data = null;
		if (result != null) {
			switch (mUriData.target) {
			case TYPE_MANUFACTURER:
				data = gson.fromJson(result, Manufacturer[].class);
				break;
			case TYPE_DEVICE_TYPE:
				data = gson.fromJson(result, DeviceType[].class);
				break;
			case TYPE_CODESET:
				data = gson.fromJson(result, Codeset[].class);
				break;
			case TYPE_IR_CODE:
				data = gson.fromJson(result, IrCode[].class);
				break;
			}
		}
		if (mListener != null)
			mListener.onReceiveData(mUriData.target, data);
	}

	public interface Listener {
		/**
		 * Called when data was returned from the DB
		 * 
		 * @param data
		 *            may be null if the connection failed
		 */
		public void onReceiveData(int type, Object[] data);
	}

}
