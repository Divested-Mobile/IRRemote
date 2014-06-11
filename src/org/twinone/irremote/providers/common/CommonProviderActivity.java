package org.twinone.irremote.providers.common;

import org.twinone.irremote.R;
import org.twinone.irremote.providers.BaseProviderActivity;
import org.twinone.irremote.providers.common.CommonProviderFragment.Data;

import android.os.Bundle;

public class CommonProviderActivity extends BaseProviderActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setExitType(Data.TARGET_DEVICE_TYPE);

		setContentView(R.layout.activity_db);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new CommonProviderFragment()).commit();
		}
	}

	public void popFragment() {
		getFragmentManager().popBackStack();
	}

	public void addFragment(Data data) {
		mCurrentType = data.targetType;
		CommonProviderFragment frag = new CommonProviderFragment();
		Bundle args = new Bundle();
		args.putSerializable(CommonProviderFragment.ARG_DATA, data);
		frag.setArguments(args);
		getFragmentManager().beginTransaction().replace(R.id.container, frag)
				.addToBackStack("default").commit();
		// Update action bar back button:
	}

}
