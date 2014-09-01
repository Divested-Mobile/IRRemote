package org.twinone.irremote.ui;

import org.twinone.irremote.R;
import org.twinone.irremote.components.AnimHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.widget.Toast;

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
		setContentView(R.layout.activity_empty);

		mRemoteName = getIntent().getStringExtra(EXTRA_REMOTE_NAME);
		setTitle(getString(R.string.edit_activity_title, mRemoteName));
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		mEditFragment = new EditRemoteFragment();
		mEditFragment.showFor(this, mRemoteName);
		// mEditFragment.showFor(this,
		// getIntent().getStringExtra(EXTRA_REMOTE_NAME));

	}

	@Override
	public void setTitle(CharSequence title) {
		super.setTitle(title);
		getActionBar().setTitle(title);
	}

	@Override
	public boolean onNavigateUp() {
		if (mEditFragment.isModified()) {
			showConfirmationDialog();
			return false;
		} else {
			Toast.makeText(this, R.string.remote_saved_toast, Toast.LENGTH_LONG)
					.show();
			finish();
		}
		return true;
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
		if (mEditFragment.isModified())
			mEditFragment.getRemote().save(this);
	}

	@Override
	public void finish() {
		super.finish();
		AnimHelper.onFinish(this);
	}
}
