package org.twinone.irremote.ui;

import org.twinone.irremote.R;
import org.twinone.irremote.components.AnimHelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

public class SettingsActivity extends ActionBarActivity {

	public static final String PREF_FILE_DEFAULT = "default";

	public static final SharedPreferences getPreferences(Context c) {
		return c.getSharedPreferences(PREF_FILE_DEFAULT, Context.MODE_PRIVATE);
	}

	private Toolbar mToolbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_empty);
		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		getFragmentManager().beginTransaction()
				.replace(R.id.container, new SettingsFragment()).commit();
	}

	@Override
	public boolean onSupportNavigateUp() {
		return onNavigateUp();
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
