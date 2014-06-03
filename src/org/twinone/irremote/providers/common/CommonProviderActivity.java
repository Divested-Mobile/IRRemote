package org.twinone.irremote.providers.common;

import org.twinone.irremote.R;

import android.app.Activity;
import android.os.Bundle;

public class CommonProviderActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_db);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new CommonProviderFragment()).commit();
		}
	}

	@Override
	public boolean onNavigateUp() {
		popFragment();
		return true;
	}

	public void popFragment() {
		getFragmentManager().popBackStack();
	}

	@Override
	public void setTitle(CharSequence title) {
		super.setTitle(title);
		getActionBar().setTitle(title);
	}


}
