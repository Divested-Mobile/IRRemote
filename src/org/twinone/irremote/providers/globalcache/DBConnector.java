package org.twinone.irremote.providers.globalcache;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.twinone.irremote.util.SimpleCache;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

public class DBConnector {

	private static final String TAG = "DBConnector";

	public void setOnDataReceivedListener(OnDataReceivedListener listener) {
		mListener = listener;
	}

	/*
	 * Listener that will provide callbacks to the user
	 */
	private OnDataReceivedListener mListener;

	// private UriData mUriData = new UriData();

	public void query(UriData data) {
		if (data == null)
			data = new UriData();
		queryServer(data);
	}

	private final Context mContext;

	public DBConnector(Context c) {
		this(c, null);
	}

	public DBConnector(Context c, OnDataReceivedListener listener) {
		this.mListener = listener;
		this.mContext = c;
	}

	private void queryServer(UriData data) {
		cancelQuery();
		mDBTask = new DBTask(data);
		mDBTask.execute();
	}

	private DBTask mDBTask;

	private class DBTask extends AsyncTask<String, Void, String> {
		private String mUrl;
		private String mCacheName;
		private int mTarget;

		public DBTask(UriData data) {
			mUrl = data.getUrl();
			mCacheName = data.getCacheName();
			mTarget = data.targetType;
		}

		@Override
		protected String doInBackground(String... params) {
			String cached = SimpleCache.read(mContext, mCacheName);
			if (cached != null) {
				return cached;
			}
			// If not cached, load from http
			try {
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
				// Save to cache for future access
				SimpleCache.write(mContext, mCacheName, data.toString());

				return data.toString();
			} catch (Exception e) {
				Log.i(TAG, "Query to server failed ", e);
				return null;
			}
		}

		@Override
		protected void onPostExecute(String result) {
			triggerListenerOnReceived(mTarget, result);
		}

	}

	public void cancelQuery() {
		if (mDBTask != null && !mDBTask.isCancelled())
			// Allow task to finish (improving cache) ?
			mDBTask.cancel(false);
	}

	// Result may be null if connection failed!
	public void triggerListenerOnReceived(int target, String result) {
		if (mListener == null)
			return;
		Gson gson = new Gson();
		Object[] data = null;
		if (result != null) {
			switch (target) {
			case UriData.TYPE_MANUFACTURER:
				data = gson.fromJson(result, Manufacturer[].class);
				break;
			case UriData.TYPE_DEVICE_TYPE:
				data = gson.fromJson(result, DeviceType[].class);
				break;
			case UriData.TYPE_CODESET:
				data = gson.fromJson(result, Codeset[].class);
				break;
			case UriData.TYPE_IR_CODE:
				data = gson.fromJson(result, IrCode[].class);
				break;
			}
		}
		mListener.onDataReceived(target, data);
	}

	public interface OnDataReceivedListener {
		/**
		 * Called when data was returned from the database server
		 * 
		 * @param data
		 *            may be null if the connection failed
		 */
		public void onDataReceived(int type, Object[] data);
	}

}
