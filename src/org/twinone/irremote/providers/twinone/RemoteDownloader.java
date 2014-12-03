package org.twinone.irremote.providers.twinone;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.twinone.irremote.Constants;

import android.os.AsyncTask;
import android.util.Log;

public class RemoteDownloader {
	private DownloadListener mListener;
	private int mStatusCode;

	public RemoteDownloader() {
	}

	private URL getRemoteURL(String id) throws MalformedURLException {
		if (id == null)
			throw new NullPointerException("Remote id cannot be null");
		return new URL(Constants.DOWNLOAD_URL + "?id=" + id);
	}

	public void setListener(DownloadListener listener) {
		mListener = listener;
	}

	public interface DownloadListener {
		public void onDownload(TransferableRemoteDetails remote, int statusCode);
	}

	public void download(String remoteId, DownloadListener listener) {
		setListener(listener);
		download(remoteId);
	}

	public void download(String remoteId) {
		if (mListener == null)
			throw new IllegalStateException("You must set a listener");
		new DownloadTask().execute(new String[] { remoteId });
	}

	public class DownloadTask extends
			AsyncTask<String, Void, TransferableRemoteDetails> {

		@Override
		protected TransferableRemoteDetails doInBackground(String... params) {
			if (params == null || params[0] == null) {
				throw new NullPointerException("Remote name cannot be null");
			}
			return downloadImpl(params[0]);
		}

		@Override
		protected void onPostExecute(TransferableRemoteDetails result) {
			if (mListener != null) {
				mListener.onDownload(result, mStatusCode);
			}
			super.onPostExecute(result);
		}

	}

	private TransferableRemoteDetails downloadImpl(String remoteId) {
		try {

			final URL url = getRemoteURL(remoteId);
			final HttpURLConnection conn = (HttpURLConnection) url
					.openConnection();
			conn.setRequestMethod("GET");

			TransferableRemoteDetails remote = null;
			try {
				InputStream in = new BufferedInputStream(conn.getInputStream());
				final BufferedReader br = new BufferedReader(
						new InputStreamReader(in));
				final StringBuilder data = new StringBuilder();
				String tmp;
				while ((tmp = br.readLine()) != null) {
					data.append(tmp);
				}
				Log.d("Downloader", "Downloaded remote:");
				Log.d("Downloader", data.toString());
				remote = TransferableRemoteDetails.deserialize(data.toString());
			} catch (Exception e) {
				Log.w("Downloader", "Error downloading remote " + url + ":", e);
			}
			conn.disconnect();

			mStatusCode = conn.getResponseCode();
			Log.d("Downloader", "Download result: " + conn.getResponseCode());
			return remote;
		} catch (Exception e) {
			Log.w("Uploader", "Error downloading remote " + remoteId + ":", e);
			return null;
		}
	}
}
