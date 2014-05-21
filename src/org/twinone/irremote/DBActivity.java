package org.twinone.irremote;

import org.twinone.irremote.globalcache.DBFragment;
import org.twinone.irremote.globalcache.UriData;

import android.app.Activity;
import android.app.FragmentManager.OnBackStackChangedListener;
import android.os.Bundle;

public class DBActivity extends Activity implements OnBackStackChangedListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new DBFragment()).commit();
		}

		getFragmentManager().addOnBackStackChangedListener(this);
	}

	@Override
	public void onBackStackChanged() {
		getActionBar().setDisplayHomeAsUpEnabled(
				getFragmentManager().getBackStackEntryCount() > 0);
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
