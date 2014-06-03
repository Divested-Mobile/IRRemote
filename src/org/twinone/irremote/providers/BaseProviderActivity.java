package org.twinone.irremote.providers;

import org.twinone.irremote.R;
import org.twinone.irremote.Remote;
import org.twinone.irremote.ui.SaveRemoteDialogFragment;
import org.twinone.irremote.ui.SaveRemoteDialogFragment.OnRemoteSavedListener;

import android.app.Activity;
import android.widget.Toast;

public class BaseProviderActivity extends Activity {

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

}
