package org.twinone.irremote.ui;

import java.io.File;
import java.io.IOException;

import org.twinone.androidlib.AdMobBannerBuilder;
import org.twinone.androidlib.RateManager;
import org.twinone.androidlib.versionmanager.VersionManager;
import org.twinone.androidlib.versionmanager.VersionManager.OnUpdateListener;
import org.twinone.androidlib.versionmanager.VersionManager.UpdateInfo;
import org.twinone.irremote.Constants;
import org.twinone.irremote.R;
import org.twinone.irremote.compat.ToolbarActivity;
import org.twinone.irremote.components.AnimHelper;
import org.twinone.irremote.components.Remote;
import org.twinone.irremote.ir.SignalCorrector;
import org.twinone.irremote.ir.io.HTCReceiver;
import org.twinone.irremote.ir.io.Receiver;
import org.twinone.irremote.ir.io.Transmitter;
import org.twinone.irremote.providers.ProviderActivity;
import org.twinone.irremote.providers.twinone.RegisterActivity;
import org.twinone.irremote.providers.twinone.UploadActivity;
import org.twinone.irremote.ui.SelectRemoteListView.OnRemoteSelectedListener;
import org.twinone.irremote.ui.dialogs.RenameRemoteDialog;
import org.twinone.irremote.ui.dialogs.RenameRemoteDialog.OnRemoteRenamedListener;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.net.http.HttpResponseCache;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.melnykov.fab.FloatingActionButton;

public class MainActivity extends ToolbarActivity implements
		OnRemoteSelectedListener, OnRemoteRenamedListener, OnUpdateListener,
		android.view.View.OnClickListener {

	private static final String TAG = "MainActivity";

	public static final String EXTRA_RECREATE = "org.twinone.irremote.intent.extra.from_prefs";

	private MainNavFragment mNavFragment;

	private ImageView mBackground;
	private ViewGroup mAdViewContainer;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (!checkTransmitterAvailable() && !Constants.USE_DEBUG_TRANSMITTER) {
			showNotAvailableDialog();
		}

		new VersionManager(this, this).callFromEntryPoint();

		SignalCorrector.setAffectedOnce(this);
		HTCReceiver.setReceiverAvailableOnce(this);

		final SharedPreferences sp = SettingsActivity.getPreferences(this);
		if (sp.getBoolean(getString(R.string.pref_key_fullscreen), false)) {
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}

		setRequestedOrientation(getRequestedOrientation());

		setContentView(R.layout.activity_main);

		mAddRemoteButton = (FloatingActionButton) findViewById(R.id.add_remote);
		mAddRemoteButton.hide(false);
		mAddRemoteButton.setOnClickListener(this);

		setupNavigation();
		setupShowAds();

		mBackground = (ImageView) findViewById(R.id.background);
		new BackgroundManager(this, mBackground).setBackgroundFromPreference();

		RateManager.show(this, getString(R.string.share_promo));
	}


	private void setupNavigation() {

		mNavFragment = (MainNavFragment) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mNavFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));

		mNavFragment.setEdgeSizeDp(30);
	}

	private void setupShowAds() {
		mAdViewContainer = (ViewGroup) findViewById(R.id.ad_container);
		if (Constants.SHOW_ADS) {
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
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		if (intent.getBooleanExtra(EXTRA_RECREATE, false)) {
			recreate();
		}
	}

	/**
	 * Starts MainActivity, but if it's already created, it will recreate
	 * 
	 * @param c
	 */
	public static void recreate(Context c) {
		Intent i = new Intent(c, MainActivity.class);
		i.putExtra(EXTRA_RECREATE, true);
		c.startActivity(i);
	}

	private boolean checkTransmitterAvailable() {
		final String key = "_has_ir_emitter";
		SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);
		boolean available = false;
		if (sp.getBoolean(key, false)) {
			return true;
		}

		available = Transmitter.isTransmitterAvailable(this);
		sp.edit().putBoolean(key, true);
		return available;
	}

	private void showNotAvailableDialog() {
		AlertDialog.Builder ab = new AlertDialog.Builder(this);
		ab.setTitle(R.string.dlg_na_tit);
		ab.setMessage(R.string.dlg_na_msg);
		ab.setPositiveButton(android.R.string.ok, new OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				finish();
			}
		});
		ab.setCancelable(false);
		ab.show();
	}

	@Override
	protected void onResume() {
		super.onResume();

		onRemotesChanged();

	}

	@Override
	public int getRequestedOrientation() {
		return getRequestedOrientation(this);
	}

	public static int getRequestedOrientation(Context c) {
		SharedPreferences sp = SettingsActivity.getPreferences(c);
		String value = sp.getString(c.getString(R.string.pref_key_orientation),
				c.getString(R.string.pref_val_ori_system));
		String auto = c.getString(R.string.pref_val_ori_auto);
		String port = c.getString(R.string.pref_val_ori_port);
		String land = c.getString(R.string.pref_val_ori_land);

		if (value.equals(auto)) {
			return ActivityInfo.SCREEN_ORIENTATION_SENSOR;
		} else if (value.equals(port)) {
			return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
		} else if (value.equals(land)) {
			return ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
		} else {
			return ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
		}
	}

	public void setRemote(String name) {
		new DefaultRemoteFragment().showFor(this, name);
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
		getSupportActionBar().setTitle(title);
	}

	@Override
	public void onRemoteRenamed(String oldName, String newName) {
		// As we renamed this remote, it was selected before, so we need to
		// select it again
		Remote.setLastUsedRemoteName(this, newName);
		mNavFragment.update();
	}

	@Override
	public void onRemoteSelected(int position, String remoteName) {
		setRemote(remoteName);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.add_remote:
			onAddRemoteSelected();
			break;
		}
	}

	@Override
	public void onAddRemoteSelected() {
		Intent i = new Intent(this, ProviderActivity.class);
		i.setAction(ProviderActivity.ACTION_SAVE_REMOTE);
		AnimHelper.startActivity(this, i);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.remote, menu);
		boolean hasRemote = getRemoteName() != null;
		boolean canReceive = Receiver.isAvailable(this);
		if (!hasRemote) {
			setTitle(R.string.app_name);
		}
		menu.findItem(R.id.menu_action_delete).setVisible(hasRemote);
		menu.findItem(R.id.menu_action_rename).setVisible(hasRemote);
		menu.findItem(R.id.menu_action_edit).setVisible(hasRemote);

		boolean showLearn = canReceive || Constants.USE_DEBUG_RECEIVER;
		menu.findItem(R.id.menu_action_learn).setVisible(showLearn);

		menu.findItem(R.id.menu_debug).setVisible(Constants.DEBUG);
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
			EditRemoteActivity.show(this, getRemoteName());
			break;

		case R.id.menu_action_learn:

			Intent learn = new Intent(this, ProviderActivity.class);
			learn.putExtra(ProviderActivity.EXTRA_PROVIDER,
					ProviderActivity.PROVIDER_LEARN);
			learn.setAction(ProviderActivity.ACTION_SAVE_REMOTE);
			AnimHelper.startActivity(this, learn);
			break;
		case R.id.menu_action_settings:

			Intent i = new Intent(this, SettingsActivity.class);
			AnimHelper.startActivity(this, i);
			break;
		case R.id.menu_debug:
			debugDialog();
			break;
		}

		return false;
	}

	private void debugDialog() {
		AlertDialog.Builder ab = new AlertDialog.Builder(this);
		ab.setTitle("Debug");
		CharSequence[] titles = new CharSequence[] {

		"Upload",

		"Register",

		"Verify"

		};
		ab.setItems(titles, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					UploadActivity.startFor(getRemoteName(), MainActivity.this);
					break;
				case 1:
					Intent reg = new Intent(MainActivity.this,
							RegisterActivity.class);
					startActivity(reg);
					break;
				case 2:
					Intent vfy = new Intent(MainActivity.this,
							RegisterActivity.class);
					Uri uri = Uri.parse("org.twinone.irremote/launch?a=verify");
					vfy.setData(uri);
					startActivity(vfy);
					break;
				}
			}
		});
		ab.show();
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
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
		AnimHelper.showDialog(ab);
	}

	private void onRemotesChanged() {
		invalidateOptionsMenu();
		updateRemoteLayout();
		if (getRemoteName() == null) {
			mNavFragment.lockOpen(true);
		} else {
			mNavFragment.unlock();
		}
	}

	private FloatingActionButton mAddRemoteButton;

	public void showAddRemoteButton() {
		mAddRemoteButton.show();
	}

	public void hideAddRemoteButton() {
		mAddRemoteButton.hide();
	}

	@Override
	public void onUpdate(Context c, UpdateInfo ui) {
		if (ui.isUpdated()) {
		}
	}
}
