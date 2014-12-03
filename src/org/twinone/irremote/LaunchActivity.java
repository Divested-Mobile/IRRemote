package org.twinone.irremote;

import org.twinone.irremote.providers.twinone.DownloadActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class LaunchActivity extends Activity {

	private static final String PARAM_ACTION = "a";
	private static final String ACTION_DOWNLOAD = "download";
	private static final String ACTION_UPLOAD = "upload";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!validateIntent()) {
			finish();
			return;
		}
		Uri uri = getIntent().getData();
		String action = uri.getQueryParameter(PARAM_ACTION);
		Class<?> c = null;
		switch (action) {
		case ACTION_UPLOAD:
			c = LaunchActivity.class;
			break;
		case ACTION_DOWNLOAD:
			c = DownloadActivity.class;
			break;
		}
		Log.i("LaunchActivity", "Received "
				+ " for intent " + uri.toString());

		if (c != null) {
			Intent i = new Intent(this, c);
			i.setData(getIntent().getData());
			Log.i("LaunchActivity", "Starting " + c.getSimpleName()
					+ " for intent " + uri.toString());
			startActivity(i);
		}
	}

	private boolean validateIntent() {
		Uri data = getIntent().getData();
		if (data == null)
			return false;
		if (!data.getScheme().equals(getPackageName()))
			return false;
		if (!data.getAuthority().equals("launch"))
			return false;
		return true;
	}

}
