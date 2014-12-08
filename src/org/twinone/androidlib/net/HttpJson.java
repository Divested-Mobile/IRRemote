package org.twinone.androidlib.net;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

/**
 * This class performs a simple json network http request to a server
 * 
 * @author twinone
 * 
 */
public class HttpJson<Req, Resp> extends AsyncTask<Void, Void, Void> {

	private String mMethod = "POST";
	private String mUrl;
	private Req mReq;
	private Resp mResp;
	private boolean mHasError;
	private Exception mException;

	private int mStatusCode;
	private Listener<Req, Resp> mListener;

	private Class<? extends Resp> mRespClass;

	public void setListener(Listener<Req, Resp> listener) {
		mListener = listener;
	}

	public HttpJson(Class<? extends Resp> respClass) {
		mRespClass = respClass;
	}

	public HttpJson(Class<? extends Resp> respClass, String url) {
		this(respClass);
		setUrl(url);
	}

	public void setUrl(String url) {
		mUrl = url;
	}

	public void execute(Req req, Listener<Req, Resp> listener) {
		if (mUrl == null) {
			throw new IllegalStateException(
					"You must specify an URL with setUrl()");
		}
		setListener(listener);
		mReq = req;
		execute((Void[]) null);

	}

	public interface Listener<Req, Resp> {
		public void onServerResponse(int statusCode, Req req, Resp resp);

		public void onServerException(Exception e);
	}

	@Override
	protected Void doInBackground(Void... params) {
		mHasError = !serverRequestImpl();
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		if (mListener != null) {
			if (!mHasError)
				mListener.onServerResponse(mStatusCode, mReq, mResp);
			else
				mListener.onServerException(mException);
		}
	}

	/**
	 * Set the request method, default is POST
	 */
	public void setMethod(String method) {
		mMethod = method;
	}

	private boolean serverRequestImpl() {
		try {

			final HttpURLConnection conn = (HttpURLConnection) new URL(mUrl)
					.openConnection();
			conn.setRequestMethod(mMethod);
			conn.setRequestProperty("Content-Type", "application/json");

			conn.connect();
			if (mReq != null) {
				String json = new Gson().toJson(mReq);
				Log.d("", "Pushing string: " + json);
				conn.getOutputStream().write(json.getBytes());
				conn.getOutputStream().close();
			}

			InputStream in = new BufferedInputStream(conn.getInputStream());
			final BufferedReader br = new BufferedReader(new InputStreamReader(
					in));
			final StringBuilder data = new StringBuilder();
			String tmp;
			while ((tmp = br.readLine()) != null) {
				data.append(tmp);
			}
			Log.i("HttpJson", "Response:");
			Log.i("HttpJson", data.toString());
			mResp = (Resp) new Gson().fromJson(data.toString(), mRespClass);
			mStatusCode = conn.getResponseCode();
			conn.disconnect();
			return true;
		} catch (Exception e) {
			mException = e;
			return false;
		}
	}

}
