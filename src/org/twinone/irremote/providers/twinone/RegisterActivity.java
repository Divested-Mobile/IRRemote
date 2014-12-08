package org.twinone.irremote.providers.twinone;

import org.twinone.irremote.R;
import org.twinone.irremote.compat.ToolbarActivity;
import org.twinone.irremote.ui.SettingsActivity;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

public class RegisterActivity extends ToolbarActivity {

	/**
	 * This boolean preference contains true if the user is registered and
	 * verified
	 */
	public static final String PREF_KEY_REGISTERED = "org.twinone.irremote.registered_user";
	public static final String PREF_KEY_USERNAME = "org.twinone.irremote.username";
	public static final String PREF_KEY_EMAIL = "org.twinone.irremote.email";
	public static final String PREF_KEY_ACCESS_TOKEN = "org.twinone.irremote.access_token";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_empty);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		if (isVerifyIntent()) {
			addFragment(new VerifyFragment());
		} else {
			addFragment(new RegisterFragment());
		}
	}

	public static boolean isRegistered(Context c) {
		return SettingsActivity.getPreferences(c).getBoolean(PREF_KEY_REGISTERED, false);
	}

	public static String getUsername(Context c) {
		return SettingsActivity.getPreferences(c).getString(PREF_KEY_USERNAME, "");
	}

	public static String getEmail(Context c) {
		return SettingsActivity.getPreferences(c).getString(PREF_KEY_EMAIL, "");
	}

	@Override
	public void setTitle(CharSequence title) {
		super.setTitle(title);
		getToolbar().setTitle(title);
	}

	private boolean isVerifyIntent() {
		Uri data = getIntent().getData();
		if (data == null)
			return false;
		return data.getQueryParameter("a").equals("verify");
	}

	public void addFragment(Fragment fragment) {
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, fragment).commit();
	}

	@Override
	public boolean onNavigateUp() {
		Log.d("RegisterActivity", "onNavigateUp");
		return onSupportNavigateUp();
	}

	@Override
	public boolean onSupportNavigateUp() {
		Log.d("RegisterActivity", "onSupportNavigateUp");
		finish();
		return true;
	}

}
