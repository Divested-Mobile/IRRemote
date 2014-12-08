package org.twinone.irremote.providers.twinone;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.twinone.irremote.Constants;
import org.twinone.irremote.components.Remote;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class RemoteUploader {
	private Context mContext;
	private UploadListener mListener;

	private String mMessage;
	private int mStatusCode;

	public RemoteUploader(Context c) {
		mContext = c;
	}

	public void setListener(UploadListener listener) {
		mListener = listener;
	}

	public interface UploadListener {
		public void onUpload(int statusCode, String message);
	}

	public void upload(Remote remote) {
		new UploadTask().execute(new Remote[] { remote });
	}

	public class UploadTask extends AsyncTask<Remote, Void, Void> {

		@Override
		protected Void doInBackground(Remote... params) {
			if (params == null || params[0] == null) {
				throw new NullPointerException("Remote name cannot be null");
			}
			uploadImpl(params[0].name);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (mListener != null) {
				mListener.onUpload(mStatusCode, mMessage);
			}
			super.onPostExecute(result);
		}

	}

	private void uploadImpl(String remoteName) {
		try {

			final TransferableRemoteDetails ud = new TransferableRemoteDetails(
					mContext);
			final Remote remote = Remote.load(mContext, remoteName);
			ud.setRemote(remote);

			final URL url = new URL(Constants.URL_UPLOAD);
			final HttpURLConnection conn = (HttpURLConnection) url
					.openConnection();
			conn.setRequestMethod("POST");
			conn.connect();
			conn.getOutputStream().write(ud.toString().getBytes());
			conn.getOutputStream().close();
			try {
				InputStream in = new BufferedInputStream(conn.getInputStream());
				final BufferedReader br = new BufferedReader(
						new InputStreamReader(in));
				final StringBuilder data = new StringBuilder();
				String tmp;
				while ((tmp = br.readLine()) != null) {
					data.append(tmp);
				}
				mMessage = data.toString();
			} catch (Exception e) {
			}
			conn.disconnect();

			Log.d("Uploader", "Upload result: " + conn.getResponseCode());
			mStatusCode = conn.getResponseCode();
		} catch (Exception e) {
			Log.w("Uploader", "Error uploading remotea " + remoteName + ":", e);
		}
	}
}
