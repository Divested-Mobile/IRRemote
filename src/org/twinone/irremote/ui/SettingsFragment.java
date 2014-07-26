package org.twinone.irremote.ui;

import java.io.InputStream;
import java.util.List;

import org.twinone.irremote.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class SettingsFragment extends PreferenceFragment implements
		OnSharedPreferenceChangeListener {

	private static final int BG_REQUEST_CODE = 1337;

	@Override
	public void onResume() {
		super.onResume();
		getPreferenceManager().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);

	}

	@Override
	public void onPause() {
		super.onPause();
		getPreferenceManager().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PreferenceManager pm = getPreferenceManager();
		pm.setSharedPreferencesName(SettingsActivity.PREF_FILE_DEFAULT);
		pm.setSharedPreferencesMode(Context.MODE_PRIVATE);
		addPreferencesFromResource(R.xml.prefs);

	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
		if (key.equals(getString(R.string.pref_key_fix))) {
			showFixDialog();
		} else if (key.equals(getString(R.string.pref_key_bg))) {
			final String value = sp.getString(getString(R.string.pref_key_bg),
					getString(R.string.pref_val_bg_none));
			if (value.equals(getString(R.string.pref_val_bg_gallery))) {
				startChooseBackgroundActivity();
			} else {
				sp.edit().remove(key);
			}
		}

	}

	private void startChooseBackgroundActivity() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_PICK);
		List<ResolveInfo> ris = getActivity().getPackageManager()
				.queryIntentActivities(intent,
						PackageManager.MATCH_DEFAULT_ONLY);
		if (ris.size() > 0) {
			startActivityForResult(Intent.createChooser(intent, null),
					BG_REQUEST_CODE);
		} else {
			Toast.makeText(getActivity(), "Error - No gallery app",
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onActivityResult(int req, int res, Intent data) {
		super.onActivityResult(req, res, data);
		if (res == Activity.RESULT_OK) {

			switch (req) {
			case BG_REQUEST_CODE:
				String path = data.getData().toString();
				getPreferenceManager().getSharedPreferences().edit()
						.putString(getString(R.string.pref_key_bg_uri), path)
						.apply();
				
				Toast.makeText(getActivity(), R.string.bg_changed_ok,
						Toast.LENGTH_LONG).show();
			}
		}
	}

	private void showFixDialog() {
		AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
		ab.setTitle(R.string.pref_dlg_tit_fix);
		ab.setMessage(R.string.pref_dlg_msg_fix);
		ab.setPositiveButton(android.R.string.ok, null);
		ab.setCancelable(false);
		ab.show();
	}

}
