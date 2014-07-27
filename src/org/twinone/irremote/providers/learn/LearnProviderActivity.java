package org.twinone.irremote.providers.learn;

import org.twinone.irremote.R;
import org.twinone.irremote.components.Remote;
import org.twinone.irremote.providers.BaseProviderActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

public class LearnProviderActivity extends BaseProviderActivity implements
		DialogInterface.OnClickListener {

	private static final String SAVE_DEVICE_TYPE = "device_type";

	private String mName;
	private int mSelectedDeviceType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_empty);

		if (savedInstanceState != null
				&& savedInstanceState.containsKey(SAVE_DEVICE_TYPE)) {
			mSelectedDeviceType = savedInstanceState.getInt(SAVE_DEVICE_TYPE);
			showLearnFragmentForType();
		} else {
			showSelectDialog();
		}

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt(SAVE_DEVICE_TYPE, mSelectedDeviceType);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void saveRemote(Remote remote) {
		remote.name = mName;
		super.saveRemote(remote);
	}

	private void showLearnFragmentForType() {
		getActionBar()
				.setTitle(getString(R.string.learn_activity_title, mName));

		getFragmentManager()
				.beginTransaction()
				.replace(R.id.container,
						LearnRemoteFragment.getInstance(mSelectedDeviceType))
				.commit();
	}

	private void showSelectDialog() {
		AlertDialog.Builder ab = new AlertDialog.Builder(this);
		ab.setTitle(R.string.learn_select_tit);
		ab.setItems(R.array.learn_device_types, this);
		ab.setNegativeButton(android.R.string.cancel, this);
		ab.setCancelable(false);
		ab.show();

	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (which == DialogInterface.BUTTON_NEGATIVE) {
			finish();
		} else {
			mName = getResources().getStringArray(R.array.learn_device_types)[which];
			mSelectedDeviceType = which;
			showLearnFragmentForType();
			dialog.dismiss();
		}
	}

}
