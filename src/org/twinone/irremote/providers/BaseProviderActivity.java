package org.twinone.irremote.providers;

import org.twinone.irremote.R;
import org.twinone.irremote.Remote;
import org.twinone.irremote.ir.Signal;
import org.twinone.irremote.ir.io.Transmitter;
import org.twinone.irremote.ui.SaveRemoteDialog;
import org.twinone.irremote.ui.SaveRemoteDialog.OnRemoteSavedListener;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.widget.Toast;

public class BaseProviderActivity extends Activity {

	public static final String ACTION_GET_REMOTE = "org.twinone.irremote.intent.action.get_remote";
	public static final String ACTION_GET_BUTTON = "org.twinone.irremote.intent.action.get_button";

	/**
	 * To be used with {@link #ACTION_GET_BUTTON}, the name of the remote to
	 * which this button has to be added
	 */
	public static final String EXTRA_TARGET_REMOTE_NAME = "org.twinone.irremote.intent.extra.remote_name";

	private Transmitter mTransmitter;

	protected int mCurrentType;
	private int mExitType;

	public void setCurrentType(int currentType) {
		mCurrentType = currentType;
	}

	public void setExitType(int exitType) {
		mExitType = exitType;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	/**
	 * Use this method to prompt the user to save this remote
	 * 
	 * @param r
	 */
	public void save(Remote remote) {
		SaveRemoteDialog dialog = SaveRemoteDialog.newInstance(remote);
		dialog.setListener(new OnRemoteSavedListener() {

			@Override
			public void onRemoteSaved(String name) {
				// Finish the activity, we've saved the remote
				Remote.setPersistedRemoteName(BaseProviderActivity.this, name);
				Toast.makeText(BaseProviderActivity.this,
						R.string.remote_saved_toast, Toast.LENGTH_SHORT).show();
				finish();
			}
		});
		dialog.show(this);

	}

	@Override
	public boolean onNavigateUp() {
		if (mCurrentType == mExitType) {
			finish();
		} else {
			getFragmentManager().popBackStack();
		}
		return true;
	}

	@Override
	public void setTitle(CharSequence title) {
		super.setTitle(title);
		getActionBar().setTitle(title);
	}

	public void transmit(Signal signal) {
		getTransmitter().transmit(signal);
	}

	public void addFragment(Fragment fragment) {
		getFragmentManager().beginTransaction()
				.replace(R.id.container, fragment).addToBackStack("default")
				.commit();
	}

	public Transmitter getTransmitter() {
		// Lazy initialization
		if (mTransmitter == null) {
			mTransmitter = Transmitter.getInstance(this);
		}
		return mTransmitter;
	}

}
