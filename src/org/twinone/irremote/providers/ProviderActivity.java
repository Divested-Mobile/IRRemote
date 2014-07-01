package org.twinone.irremote.providers;

import org.twinone.irremote.R;

import android.app.Activity;
import android.os.Bundle;

public class ProviderActivity extends Activity {

	private int mCurrentType;
	private int mExitType;

	public void setExitType(int exitType) {
		mExitType = exitType;

	}

	public void setCurrentType(int currentType) {
		mCurrentType = currentType;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_empty);

	}

}
