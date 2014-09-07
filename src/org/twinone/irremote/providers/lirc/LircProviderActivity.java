package org.twinone.irremote.providers.lirc;

import org.twinone.irremote.R;
import org.twinone.irremote.providers.ProviderActivity;
import org.twinone.irremote.providers.common.CommonProviderFragment.Data;

import android.os.Bundle;

public class LircProviderActivity extends ProviderActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setExitType(Data.TARGET_DEVICE_TYPE);

		setContentView(R.layout.activity_empty);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new LircProviderFragment()).commit();
		}
	}

	public void addFragment(UriData data) {
		mCurrentType = data.targetType;
		LircProviderFragment frag = new LircProviderFragment();
		Bundle args = new Bundle();
		args.putSerializable(LircProviderFragment.ARG_URI_DATA, data);
		frag.setArguments(args);
		getFragmentManager().beginTransaction().replace(R.id.container, frag)
				.addToBackStack("default").commit();
		// Update action bar back button:
	}

}
