package org.twinone.irremote.providers.learn;

import org.twinone.irremote.R;
import org.twinone.irremote.components.Remote;
import org.twinone.irremote.providers.BaseProviderActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

public class LearnProviderActivity extends BaseProviderActivity implements
		DialogInterface.OnClickListener {

	private String mSelectedDeviceType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_empty);

		showSelectDialog();

	}

	@Override
	public void saveRemote(Remote remote) {
		remote.name = mSelectedDeviceType;
		super.saveRemote(remote);
	}

	private void showLearnFragmentForType(int type) {
		getActionBar().setTitle(
				getString(R.string.learn_activity_title, mSelectedDeviceType));

		getFragmentManager().beginTransaction()
				.replace(R.id.container, LearnRemoteFragment.getInstance(type))
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
			mSelectedDeviceType = getResources().getStringArray(
					R.array.learn_device_types)[which];
			showLearnFragmentForType(which);
			dialog.dismiss();
		}
	}

}
