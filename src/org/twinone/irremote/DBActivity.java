package org.twinone.irremote;

import org.twinone.irremote.globalcache.DBFragment;
import org.twinone.irremote.globalcache.UriData;

import android.app.Activity;
import android.os.Bundle;

public class DBActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_db);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new DBFragment()).commit();
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

	public void addFragment(UriData data) {
		DBFragment frag = new DBFragment();
		Bundle args = new Bundle();
		args.putSerializable(DBFragment.ARG_URI_DATA, data);
		frag.setArguments(args);
		getFragmentManager().beginTransaction().replace(R.id.container, frag)
				.addToBackStack("default").commit();
		// Update action bar back button:
	}

}
