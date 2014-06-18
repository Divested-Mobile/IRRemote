package org.twinone.irremote.ui;

import org.twinone.androidlib.AdMobBannerBuilder;
import org.twinone.androidlib.ShareManager;
import org.twinone.irremote.R;
import org.twinone.irremote.Remote;
import org.twinone.irremote.providers.common.CommonProviderActivity;
import org.twinone.irremote.ui.RenameRemoteDialog.OnRemoteRenamedListener;
import org.twinone.irremote.ui.SelectRemoteListView.OnRemoteSelectedListener;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class MainActivity extends ActionBarActivity implements
		OnRemoteSelectedListener, OnRemoteRenamedListener {

	private static final String TAG = "MainActivity";

	public static final boolean SHOW_ADS = true;

	private NavFragment mNavFragment;

	private ViewGroup mAdViewContainer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		mNavFragment = (NavFragment) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mNavFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));

		mNavFragment.setEdgeSizeDp(30);

		mAdViewContainer = (ViewGroup) findViewById(R.id.ad_container);
		// Show ads
		if (SHOW_ADS) {
			AdMobBannerBuilder builder = new AdMobBannerBuilder();
			builder.setParent(mAdViewContainer);
			builder.addTestDevice("285ACA7E7666862031AA5111058518DB");
			builder.setAdUnitId("ca-app-pub-5756278739960648/2006850014");
			builder.show();
		} else {
			Log.w(TAG, "Not showing ads in debug mode!");
			// Don't waste my precious space :D
			mAdViewContainer.setVisibility(View.GONE);
		}

		ShareManager.show(this, getString(R.string.share_promo));
	}

	@Override
	protected void onResume() {
		super.onResume();
		onRemotesChanged();
	}

	public void setRemote(String name) {
		Log.d("", "Set remote!!");
		RemoteFragment.showFor(this, name);
	}

	public String getRemoteName() {
		return mNavFragment.getSelectedRemoteName();
	}

	/**
	 * Updates the navigation fragment after a remote was selected / deleted /
	 * renamed
	 */
	public void updateRemoteLayout() {
		mNavFragment.update();
		setRemote(getRemoteName());
	}

	@Override
	public void setTitle(CharSequence title) {
		super.setTitle(title);
		getActionBar().setTitle(title);
	}

	@Override
	public void onRemoteRenamed(String oldName, String newName) {
		// As we renamed this remote, it was selected before, so we need to
		// select it again
		Remote.setPersistedRemoteName(this, newName);
		mNavFragment.update();
	}

	@Override
	public void onRemoteSelected(int position, String remoteName) {
		Log.d("", "OnSelectedListener MainActivity");
		setRemote(remoteName);
	}

	@Override
	public void onAddRemoteSelected() {
		Intent i = new Intent(this, CommonProviderActivity.class);
		startActivity(i);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.remote, menu);
		boolean hasRemote = getRemoteName() != null;
		if (!hasRemote) {
			setTitle(R.string.app_name);
		}
		menu.findItem(R.id.menu_action_delete).setVisible(hasRemote);
		menu.findItem(R.id.menu_action_rename).setVisible(hasRemote);
		menu.findItem(R.id.menu_action_edit).setVisible(hasRemote);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_action_delete:
			showDeleteRemoteDialog();
			return true;
		case R.id.menu_action_rename:
			RenameRemoteDialog d = RenameRemoteDialog
					.newInstance(getRemoteName());
			d.setOnRemoteRenamedListener(this);
			d.show(this);
			break;

		case R.id.menu_action_edit:
			Intent i = new Intent(this, EditRemoteActivity.class);
			i.putExtra(EditRemoteActivity.EXTRA_REMOTE, getRemoteName());
			startActivity(i);
			break;
		}
		return false;
	}

	private void showDeleteRemoteDialog() {
		final String remoteName = getRemoteName();
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
		if (getRemoteName() == null) {
			mNavFragment.lockOpen(true);
			Log.d("", "Opened and locked!");
		} else {
			mNavFragment.unlock();
		}
	}

}
