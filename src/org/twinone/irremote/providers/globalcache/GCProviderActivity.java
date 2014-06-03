package org.twinone.irremote.providers.globalcache;

import org.twinone.irremote.R;
import org.twinone.irremote.providers.BaseProviderActivity;

import android.os.Bundle;

public class GCProviderActivity extends BaseProviderActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_db);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new GCProviderFragment()).commit();
		}
	}

	@Override
	public boolean onNavigateUp() {
		navigateUp();
		return true;
	}

	public void navigateUp() {
		if (mCurrentType == UriData.TYPE_MANUFACTURER) {
			finish();
		} else {
			getFragmentManager().popBackStack();
		}
	}

	@Override
	public void setTitle(CharSequence title) {
		super.setTitle(title);
		getActionBar().setTitle(title);
	}

	private int mCurrentType = UriData.TYPE_MANUFACTURER;

	public void addFragment(UriData data) {
		mCurrentType = data.targetType;
		GCProviderFragment frag = new GCProviderFragment();
		Bundle args = new Bundle();
		args.putSerializable(GCProviderFragment.ARG_URI_DATA, data);
		frag.setArguments(args);
		getFragmentManager().beginTransaction().replace(R.id.container, frag)
				.addToBackStack("default").commit();
		// Update action bar back button:
	}

}
