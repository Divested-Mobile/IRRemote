package org.twinone.irremote.ui;

import org.twinone.androidlib.AdMobBannerBuilder;
import org.twinone.androidlib.ShareManager;
import org.twinone.irremote.BuildConfig;
import org.twinone.irremote.R;
import org.twinone.irremote.Remote;
import org.twinone.irremote.providers.common.CommonProviderActivity;
import org.twinone.irremote.ui.SelectRemoteListView.OnSelectListener;

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
import android.view.ViewGroup;

public class MainActivity extends ActionBarActivity implements OnSelectListener {

	private static final String TAG = "MainActivity";

	// We don't want ads in debug mode...
	// Sometimes eclipse messes this up and doesn't change DEBUG to false when
	// signing, so double check when exporting the app
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
		mNavFragment.setEdgeSizeDp(80);

		// Show ads
		if (SHOW_ADS) {
			mAdViewContainer = (ViewGroup) findViewById(R.id.ad_container);
			AdMobBannerBuilder builder = new AdMobBannerBuilder();
			builder.setParent(mAdViewContainer);
			builder.addTestDevice("896CB3D3288417013D38303D179FD45B");
			builder.setAdUnitId("ca-app-pub-5756278739960648/2006850014");
			builder.show();
		} else {
			Log.w(TAG, "Not showing ads in debug mode!");
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

	public void updateRemoteLayout() {
		mNavFragment.update();
		setRemote(mNavFragment.getSelectedRemoteName());
	}

	@Override
	public void setTitle(CharSequence title) {
		super.setTitle(title);
		getActionBar().setTitle(title);
	}

	@Override
	public void onRemoteSelected(int position, String remoteName) {
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
