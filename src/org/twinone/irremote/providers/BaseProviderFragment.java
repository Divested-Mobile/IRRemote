package org.twinone.irremote.providers;

import android.app.Activity;
import android.app.Fragment;

public class BaseProviderFragment extends Fragment {

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (!(activity instanceof BaseProviderActivity)) {
			throw new ClassCastException(
					"BaseProviderFragment should be attached to a BaseProviderActivity");
		}
	}

	protected void setCurrentType(int type) {
		getProvider().mCurrentType = type;
	}

	protected BaseProviderActivity getProvider() {
		return (BaseProviderActivity) getActivity();
	}

}
