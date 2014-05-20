package org.twinone.irremote;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import org.twinone.irremote.R;
import org.twinone.irremote.globalcache.DBConnector.UriData;

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
