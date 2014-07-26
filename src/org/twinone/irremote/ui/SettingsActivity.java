package org.twinone.irremote.ui;

import org.twinone.irremote.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

public class SettingsActivity extends Activity {

	public static final String PREF_FILE_DEFAULT = "default";

	public static final SharedPreferences getPreferences(Context c) {
		return c.getSharedPreferences(PREF_FILE_DEFAULT, Context.MODE_PRIVATE);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getActionBar().setDisplayHomeAsUpEnabled(true);

		setContentView(R.layout.activity_empty);
		getFragmentManager().beginTransaction()
				.replace(R.id.container, new SettingsFragment()).commit();
	}

	@Override
	public boolean onNavigateUp() {
		// Start main activity telling it we came from preferences.
		exit();
		return true;
	}

	@Override
	public void onBackPressed() {
		exit();
	}

	private void exit() {
		Intent i = new Intent(this, MainActivity.class);
		i.putExtra(MainActivity.EXTRA_FROM_PREFS, true);
		startActivity(i);
		Log.d("", "onNavigateUp");
		finish();

	}

	public void startChooseBackgroundActivity() {

	}

}
