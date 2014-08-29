package org.twinone.irremote.ui;

import org.twinone.irremote.R;
import org.twinone.irremote.components.AnimHelper;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

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
		exit();
		return true;
	}

	@Override
	public void onBackPressed() {
		exit();
	}

	private void exit() {
		MainActivity.recreate(this);
		finish();
	}

	@Override
	public void finish() {
		super.finish();
		AnimHelper.onFinish(this);
	}

}
