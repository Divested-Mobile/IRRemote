package org.twinone.irremote.ui;

import org.twinone.irremote.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class SettingsActivity extends Activity {

	public static final String PREF_FILE_DEFAULT = "default";

	public static final SharedPreferences getPreferences(Context c) {
		return c.getSharedPreferences(PREF_FILE_DEFAULT, Context.MODE_PRIVATE);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_empty);
		getFragmentManager().beginTransaction()
				.replace(R.id.container, new SettingsFragment()).commit();
	}

	public static class SettingsFragment extends PreferenceFragment implements
			OnSharedPreferenceChangeListener {

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
			pm.setSharedPreferencesName(PREF_FILE_DEFAULT);
			pm.setSharedPreferencesMode(Context.MODE_PRIVATE);
			addPreferencesFromResource(R.xml.prefs);
		}

		@Override
		public void onSharedPreferenceChanged(
				SharedPreferences sharedPreferences, String key) {
			if (key.equals(getString(R.string.pref_key_fix))) {
				showFixDialog();
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
}
