package org.twinone.irremote.providers.globalcache;

import org.twinone.irremote.R;
import org.twinone.irremote.providers.ProviderActivity;
import org.twinone.irremote.providers.common.CommonProviderFragment.Data;

import android.os.Bundle;

public class GCProviderActivity extends ProviderActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setExitType(Data.TARGET_DEVICE_TYPE);

		setContentView(R.layout.activity_empty);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new GCProviderFragment()).commit();
		}
	}

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
