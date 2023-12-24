package org.twinone.irremote.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.twinone.androidlib.compat.ToolbarActivity;
import org.twinone.androidlib.util.VersionManager;
import org.twinone.irremote.Constants;
import org.twinone.irremote.R;
import org.twinone.irremote.compat.Compat;
import org.twinone.irremote.components.AnimHelper;
import org.twinone.irremote.components.Remote;
import org.twinone.irremote.ir.SignalCorrector;
import org.twinone.irremote.ir.io.Transmitter;
import org.twinone.irremote.providers.DefaultProviderActivity;
import org.twinone.irremote.ui.dialogs.DebugDialog;
import org.twinone.irremote.ui.dialogs.RenameRemoteDialog.OnRemoteRenamedListener;

public class MainActivity extends ToolbarActivity implements OnRemoteRenamedListener, VersionManager.OnUpdateListener,
        android.view.View.OnClickListener {

    private static final String EXTRA_RECREATE = "org.twinone.irremote.intent.extra.from_prefs";
    private static final String TAG = "MainActivity";
    private static final int EXPORT_REMOTE_REQUEST_CODE = 0xCDE1;
    private static final int IMPORT_REMOTE_REQUEST_CODE = 0xCDE2;
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

        final SharedPreferences sp = SettingsActivity.getPreferences(this);
        if (sp.getBoolean(getString(R.string.pref_key_fullscreen), false)) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        setRequestedOrientation(getRequestedOrientation());

        setContentView(R.layout.activity_main);

        mAddRemoteButton = (FloatingActionButton) findViewById(R.id.add_remote);
        mAddRemoteButton.setOnClickListener(this);

        setupNavigation();

        ImageView mBackground = (ImageView) findViewById(R.id.background);
        new BackgroundManager(this, mBackground).setBackgroundFromPreference();

    }

    private void setupNavigation() {
        mNavFragment = (MainNavFragment) getSupportFragmentManager()
                .findFragmentById(R.id.navigation_drawer);
        mNavFragment.setUp(R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        mNavFragment.setEdgeSizeDp(30);
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

    private void showDebugDialog() {
        CharSequence[] titles = new CharSequence[]{
            getString(R.string.dlg_debug_freq)
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dlg_debug_tit);
        builder.setItems(titles, new DebugDialog(this));
        builder.show();
    }

    private void startExportRemote() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_TITLE, getRemoteName());
        intent.setType("text/plain");
        startActivityForResult(intent, EXPORT_REMOTE_REQUEST_CODE);
    }

    private void startImportRemote() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        startActivityForResult(intent, IMPORT_REMOTE_REQUEST_CODE);
    }

    private void startShareRemote() {
        Uri fileUri = Remote.writeFileToShare(this, getRemoteName());
        if (fileUri == null)
            return;
        Intent sender = new Intent(Intent.ACTION_SEND);
        sender.putExtra(Intent.EXTRA_STREAM, fileUri);
        sender.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        sender.setType("text/plain");
        String title = getString(R.string.share_remote_message, getRemoteName());
        Intent target = Intent.createChooser(sender, title);
        startActivityForResult(target, 0);
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
        final int id = v.getId();

        if (id == R.id.add_remote) {
            Intent i = new Intent(this, DefaultProviderActivity.class);
            i.setAction(DefaultProviderActivity.ACTION_SAVE_REMOTE);
            AnimHelper.startActivity(this, i);
        }
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        boolean open = mNavFragment.isOpen();
        boolean hasRemote = getRemoteName() != null;
        if (!hasRemote) setTitle(R.string.app_name);

        menu.setGroupVisible(R.id.menu_main_group, hasRemote && !open);
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
        final int itemId = item.getItemId();

        if (itemId == R.id.menu_action_edit) {
            EditRemoteActivity.show(this, getRemoteName());
        } else if (itemId == R.id.menu_action_export) {
            startExportRemote();
        } else if (itemId == R.id.menu_action_share) {
            startShareRemote();
        } else if (itemId == R.id.menu_action_import) {
            startImportRemote();
        } else if (itemId == R.id.menu_action_settings) {
            Intent i = new Intent(this, SettingsActivity.class);
            AnimHelper.startActivity(this, i);
        } else if (itemId == R.id.menu_debug) {
            showDebugDialog();
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null || data.getData() == null) {
            Toast.makeText(this, R.string.empty_remote_tit, Toast.LENGTH_SHORT).show();
            return;
        }

        if (resultCode == AppCompatActivity.RESULT_OK) {
            switch (requestCode) {
                case EXPORT_REMOTE_REQUEST_CODE: {
                    Remote.writeFileToExport(this, getRemoteName(), data);
                    break;
                }
                case IMPORT_REMOTE_REQUEST_CODE: {
                    Intent intent = new Intent(this, ImportActivity.class);
                    intent.setData(data.getData());
                    AnimHelper.startActivity(this, intent);
                    break;
                }
            }
        }
    }

    public void onRemotesChanged() {
        invalidateOptionsMenu();
        updateRemoteLayout();
        if (getRemoteName() == null) {
            mNavFragment.lockOpen(true);
        } else {
            showAddRemoteButton();
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
            if (ui.getLastVersion() <= 1520) {
                MaterialDialog.Builder mb = new MaterialDialog.Builder(this);
                mb.title("Message");
                mb.content("We've made some improvements to how remotes are displayed. Since some users might prefer to keep their old layouts, updating to the new system is not done automatically. Please go to Edit Remote and organize them manually");
                mb.positiveText(android.R.string.ok);
                mb.show();
            }
        }
    }
}
