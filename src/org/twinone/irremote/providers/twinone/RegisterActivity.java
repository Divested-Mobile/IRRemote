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

	/**
	 * Returns true if this user is registered and verified
	 */
	public static boolean isRegistered(Context c) {
		return SettingsActivity.getPreferences(c).getBoolean(
				PREF_KEY_REGISTERED, false);
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
		return onSupportNavigateUp();
	}

	@Override
	public boolean onSupportNavigateUp() {
		finish();
		return true;
	}

}
