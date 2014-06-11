package org.twinone.irremote.providers;

import org.twinone.irremote.R;
import org.twinone.irremote.Remote;
import org.twinone.irremote.ir.Signal;
import org.twinone.irremote.ir.Transmitter;
import org.twinone.irremote.ui.SaveRemoteDialogFragment;
import org.twinone.irremote.ui.SaveRemoteDialogFragment.OnRemoteSavedListener;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

public class BaseProviderActivity extends Activity {

	private Transmitter mTransmitter;

	protected int mCurrentType;
	private int mExitType;

	protected void setExitType(int exitType) {
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
		SaveRemoteDialogFragment dialog = SaveRemoteDialogFragment
				.newInstance(remote);
		dialog.setListener(new OnRemoteSavedListener() {

			@Override
			public void onRemoteSaved(String name) {
				// Finish the activity, we've saved the remote
				finish();
				Toast.makeText(BaseProviderActivity.this,
						R.string.remote_saved_toast, Toast.LENGTH_SHORT).show();
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

	private Transmitter getTransmitter() {
		// Lazy initialization
		if (mTransmitter == null) {
			mTransmitter = new Transmitter(this);
		}
		return mTransmitter;
	}

}
