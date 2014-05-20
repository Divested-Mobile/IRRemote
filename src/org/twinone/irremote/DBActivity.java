package org.twinone.irremote;

import org.twinone.irremote.globalcache.DBFragment;
import org.twinone.irremote.globalcache.UriData;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class DBActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new DBFragment()).commit();
		}
	}

	public void popFragment() {
		getSupportFragmentManager().popBackStack();
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
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, frag).addToBackStack("default")
				.commit();
	}

}
