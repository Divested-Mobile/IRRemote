package org.twinone.irremote.ui;

import org.twinone.irremote.R;
import org.twinone.irremote.components.AnimHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;

public class EditRemoteActivity extends Activity {

	private static final String EXTRA_REMOTE_NAME = "org.twinone.irremote.intent.extra.remote";

	public static void show(Activity a, String remoteName) {
		Intent i = new Intent(a, EditRemoteActivity.class);
		i.putExtra(EXTRA_REMOTE_NAME, remoteName);
		AnimHelper.startActivity(a, i);
	}

	private EditRemoteFragment mEditFragment;
	private String mRemoteName;

	@Override
	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setRequestedOrientation(MainActivity.getRequestedOrientation(this));

		setContentView(R.layout.activity_empty);

		mRemoteName = getIntent().getStringExtra(EXTRA_REMOTE_NAME);
		setTitle(getString(R.string.edit_activity_title, mRemoteName));

		getActionBar().setDisplayHomeAsUpEnabled(true);

		if (savedInstanceState == null) {
			mEditFragment = new EditRemoteFragment();
			mEditFragment.showFor(this, mRemoteName, "edit_remote");
		} else {
			mEditFragment = (EditRemoteFragment) getFragmentManager()
					.findFragmentByTag("edit_remote");
		}
		// mEditFragment.showFor(this,
		// getIntent().getStringExtra(EXTRA_REMOTE_NAME));

	}

	@Override
	public void setTitle(CharSequence title) {
		super.setTitle(title);
		getActionBar().setTitle(title);
	}

	@Override
	public void onBackPressed() {
		finishOrShowSaveDialog();
	}

	/**
	 * @return True if the activity finished
	 */
	private boolean finishOrShowSaveDialog() {
		if (mEditFragment.isEdited()) {
			showConfirmationDialog();
			return false;
		} else {
			finish();
			return true;
		}
	}

	@Override
	public boolean onNavigateUp() {
		return finishOrShowSaveDialog();
	}

	private void showConfirmationDialog() {
		AlertDialog.Builder ab = new AlertDialog.Builder(this);
		ab.setTitle(R.string.edit_confirmexitdlg_tit);

		ab.setPositiveButton(R.string.edit_confirmexitdlg_save,
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						saveRemote();
						finish();
					}
				});
		ab.setNegativeButton(R.string.edit_confirmexitdlg_discard,
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});
		AnimHelper.showDialog(ab);
	}

	private void saveRemote() {
		mEditFragment.saveRemote();
	}

	@Override
	public void finish() {
		super.finish();
		AnimHelper.onFinish(this);
	}
}
