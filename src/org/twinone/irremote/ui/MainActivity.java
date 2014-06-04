package org.twinone.irremote.ui;

import org.twinone.irremote.R;
import org.twinone.irremote.Remote;
import org.twinone.irremote.providers.globalcache.GCProviderActivity;
import org.twinone.irremote.ui.SelectRemoteListView.OnSelectListener;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity implements OnSelectListener {

	private static final String TAG = "RemoteActivity";

	private NavFragment mNavFragment;
	private RemoteFragment mRemoteFragment;
 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		mRemoteFragment = (RemoteFragment) getFragmentManager()
				.findFragmentById(R.id.container);

		mNavFragment = (NavFragment) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mNavFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
		mNavFragment.setEdgeSizeDp(80);
	}

	@Override
	protected void onResume() {
		super.onResume();
		onRemotesChanged();
	}

	public void updateRemoteLayout() {
		mNavFragment.update();
		mRemoteFragment.setRemote(mNavFragment.getSelectedRemoteName());
	}

	@Override
	public void setTitle(CharSequence title) {
		super.setTitle(title);
		getActionBar().setTitle(title);
	}

	@Override
	public void onRemoteSelected(int position, String remoteName) {
		loadRemote(remoteName);
	}

	private void loadRemote(String remoteName) {
		mRemoteFragment.setRemote(remoteName);
	}

	@Override
	public void onAddRemoteSelected() {
		Intent i = new Intent(this, GCProviderActivity.class);
		startActivityForResult(i, 0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.remote, menu);
		boolean hasRemote = mNavFragment.getSelectedRemoteName() != null;
		if (!hasRemote) {
			setTitle(R.string.app_name);
		}
		menu.findItem(R.id.menu_action_delete_remote).setVisible(hasRemote);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_action_delete_remote:
			showDeleteRemoteDialog();
			return true;
		}
		return false;
	}

	private void showDeleteRemoteDialog() {
		final String remoteName = mNavFragment.getSelectedRemoteName();
		if (remoteName == null)
			return;
		AlertDialog.Builder ab = new AlertDialog.Builder(this);
		ab.setTitle(R.string.delete_remote_title);
		ab.setMessage(getString(R.string.delete_remote_message, remoteName));
		ab.setPositiveButton(android.R.string.ok, new OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				Remote.remove(MainActivity.this, remoteName);
				onRemotesChanged();
			}
		});
		ab.setNegativeButton(android.R.string.cancel, null);
		ab.show();
	}

	private void onRemotesChanged() {
		invalidateOptionsMenu();
		updateRemoteLayout();
		if (mNavFragment.getSelectedRemoteName() == null) {
			mNavFragment.lockOpen(true);
			Log.d("", "Opened and locked!");
		} else {
			mNavFragment.unlock();
		}
	}

}
