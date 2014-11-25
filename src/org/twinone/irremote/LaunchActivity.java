package org.twinone.irremote;

import android.app.Activity;
import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;

public class LaunchActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!validateIntent()) {
			finish();
			return;
		}
		showMessage();
	}

	private void showMessage() {
		Uri data = getIntent().getData();
		String msg = data.getQueryParameter("message");
		if (msg == null || msg.isEmpty()) {
			return;
		}
		AlertDialog.Builder ab = new AlertDialog.Builder(this);
		ab.setTitle("Message from developer");
		ab.setMessage(msg);
		ab.setPositiveButton(android.R.string.ok, null);
		ab.show();

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
