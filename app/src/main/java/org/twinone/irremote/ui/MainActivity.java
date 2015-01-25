package org.twinone.irremote.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.melnykov.fab.FloatingActionButton;

import org.twinone.androidlib.AdMobBannerBuilder;
import org.twinone.androidlib.RateManager;
import org.twinone.androidlib.compat.ToolbarActivity;
import org.twinone.androidlib.util.VersionManager;
import org.twinone.irremote.Constants;
import org.twinone.irremote.R;
import org.twinone.irremote.account.AccountActivity;
import org.twinone.irremote.account.LoginRegisterActivity;
import org.twinone.irremote.account.UserInfo;
import org.twinone.irremote.compat.Compat;
import org.twinone.irremote.components.AnimHelper;
import org.twinone.irremote.components.Remote;
import org.twinone.irremote.ir.SignalCorrector;
import org.twinone.irremote.ir.io.HTCReceiver;
import org.twinone.irremote.ir.io.Transmitter;
import org.twinone.irremote.providers.ProviderActivity;
import org.twinone.irremote.providers.twinone.UploadActivity;
import org.twinone.irremote.ui.dialogs.RenameRemoteDialog.OnRemoteRenamedListener;

public class MainActivity extends ToolbarActivity implements OnRemoteRenamedListener, VersionManager.OnUpdateListener,
        android.view.View.OnClickListener {

    private static final String EXTRA_RECREATE = "org.twinone.irremote.intent.extra.from_prefs";
    private static final String TAG = "MainActivity";
    private MainNavFragment mNavFragment;

    private FloatingActionButton mAddRemoteButton;

    /**
     * Starts MainActivity, but if it's already created, it will recreate
     *
     * @param c
     */
    public static void recreate(Context c) {
        setShouldRecreate(c, true);
    }

    private static void setShouldRecreate(Context c, boolean shouldRecreate) {
        c.getSharedPreferences("default", Context.MODE_PRIVATE).edit().putBoolean("recreate_main_activity", shouldRecreate).apply();
    }

    private boolean shouldRecreate() {
        boolean should = getSharedPreferences("default", Context.MODE_PRIVATE).getBoolean("recreate_main_activity", false);
        setShouldRecreate(this, false);
        return should;
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

        ImageView mBackground = (ImageView) findViewById(R.id.background);
        new BackgroundManager(this, mBackground).setBackgroundFromPreference();

        RateManager.show(this);
    }

    private void setupNavigation() {
        mNavFragment = (MainNavFragment) getSupportFragmentManager()
                .findFragmentById(R.id.navigation_drawer);
        mNavFragment.setUp(R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        mNavFragment.setEdgeSizeDp(30);
    }


    private void setupShowAds() {
        ViewGroup mAdViewContainer = (ViewGroup) findViewById(R.id.ad_container);
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

    private boolean checkTransmitterAvailable() {
        final String key = "_has_ir_emitter";
        SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);
        if (sp.getBoolean(key, false)) {
            return true;
        }

        boolean available = Transmitter.isTransmitterAvailable(this);
        sp.edit().putBoolean(key, true).apply();
        return available;
    }

    private void showNotAvailableDialog() {
        MaterialDialog.Builder mb = Compat.getMaterialDialogBuilder(this);
        mb.title(R.string.dlg_na_tit);
        mb.content(R.string.dlg_na_msg);
        mb.positiveText(android.R.string.ok);
        mb.cancelable(false);
        mb.callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                super.onPositive(dialog);
                finish();
            }
        });
        mb.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (shouldRecreate()) recreate();
        else onRemotesChanged();
    }

    @Override
    public int getRequestedOrientation() {
        return getRequestedOrientation(this);
    }

    public void setRemote(String name) {
        Log.i("MainActivity", "SetRemote: " + name);
        new DefaultRemoteFragment().showFor(this, name);
    }

    public String getRemoteName() {
        return mNavFragment.getSelectedRemoteName();
    }

    /**
     * Updates the navigation fragment after a menu_main was selectesd / deleted /
     * renamed
     */
    void updateRemoteLayout() {
        mNavFragment.update();
        setRemote(getRemoteName());
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void onRemoteRenamed(String newName) {
        // As we renamed this menu_main, it was selected before, so we need to
        // select it again
        Remote.setLastUsedRemoteName(this, newName);
        mNavFragment.update();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_remote:
                Intent i = new Intent(this, ProviderActivity.class);
                i.setAction(ProviderActivity.ACTION_SAVE_REMOTE);
                AnimHelper.startActivity(this, i);
                break;
        }
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        boolean open = mNavFragment.isOpen();
        boolean hasRemote = getRemoteName() != null;
        if (!hasRemote) setTitle(R.string.app_name);


        menu.findItem(R.id.menu_action_account).setVisible(hasRemote && open && Constants.ENABLE_ACCOUNTS);
        menu.findItem(R.id.menu_action_edit).setVisible(hasRemote && !open);
        menu.findItem(R.id.menu_debug).setVisible(Constants.DEBUG && !open);
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_action_edit:
                EditRemoteActivity.show(this, getRemoteName());
                break;
            case R.id.menu_action_settings:
                Intent i = new Intent(this, SettingsActivity.class);
                AnimHelper.startActivity(this, i);
                break;
            case R.id.menu_debug:
                debugDialog();
                break;
            case R.id.menu_action_account:
                if (UserInfo.load(MainActivity.this).isLoggedIn()) {
                    Intent acc = new Intent(MainActivity.this, AccountActivity.class);
                    AnimHelper.startActivity(this, acc);
                } else {
                    Intent reg = new Intent(MainActivity.this,
                            LoginRegisterActivity.class);
                    AnimHelper.startActivity(this, reg);
                }
                break;
        }
        return false;
    }

    private void debugDialog() {
        MaterialDialog.Builder mb = Compat.getMaterialDialogBuilder(this);
        mb.title("Debug");
        CharSequence[] titles = new CharSequence[]{

                "Upload",

        };
        mb.items(titles);
        mb.itemsCallback(new MaterialDialog.ListCallback() {
            @Override
            public void onSelection(MaterialDialog materialDialog, View view, int which, CharSequence charSequence) {
                switch (which) {
                    case 0:
                        UploadActivity.startFor(getRemoteName(), MainActivity.this);
                        break;
                }
            }
        });
        mb.show();
    }

    public void onRemotesChanged() {
        invalidateOptionsMenu();
        updateRemoteLayout();
        if (getRemoteName() == null) {
            mNavFragment.lockOpen(true);
        } else {
            mNavFragment.unlock();
        }
    }

    public void showAddRemoteButton() {
        mAddRemoteButton.show();
    }

    public void hideAddRemoteButton() {
        mAddRemoteButton.hide();
    }

    public FloatingActionButton getAddRemoteButton() {
        return mAddRemoteButton;
    }

    @Override
    public void onUpdate(VersionManager.UpdateInfo ui) {
        if (ui.isUpdated()) {
        }
    }
}
