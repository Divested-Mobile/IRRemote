package org.twinone.irremote.providers;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import org.twinone.androidlib.NavigationFragment.NavigationListener;
import org.twinone.irremote.R;
import org.twinone.irremote.components.AnimHelper;
import org.twinone.irremote.components.Button;
import org.twinone.irremote.components.Remote;
import org.twinone.irremote.components.RemoteOrganizer;
import org.twinone.irremote.ir.Signal;
import org.twinone.irremote.ir.io.Transmitter;
import org.twinone.irremote.providers.common.CommonProviderFragment;
import org.twinone.irremote.providers.globalcache.GCProviderFragment;
import org.twinone.irremote.providers.learn.LearnButtonProviderFragment;
import org.twinone.irremote.providers.learn.LearnRemoteProviderFragment;
import org.twinone.irremote.providers.local.LocalProviderFragment;
import org.twinone.irremote.providers.twinone.TwinoneProviderFragment;
import org.twinone.irremote.ui.ProviderNavFragment;
import org.twinone.irremote.ui.dialogs.RemotePreviewDialog;
import org.twinone.irremote.ui.dialogs.SaveButtonDialog;
import org.twinone.irremote.ui.dialogs.SaveRemoteDialog;

import java.util.Iterator;

//import org.twinone.irremote.ui.dialogs.SaveButtonDialog.OnSaveButton;
//import org.twinone.irremote.ui.dialogs.SaveRemoteDialog.OnRemoteSavedListener;

public class ProviderActivity extends ActionBarActivity implements
        NavigationListener {

    /**
     * The user will select a menu_main which will be saved directly
     */
    public static final String ACTION_SAVE_REMOTE = "org.twinone.irremote.intent.action.save_remote";
    /**
     * The user will select a button that will be returned to the calling
     * activity
     */
    public static final String ACTION_GET_BUTTON = "org.twinone.irremote.intent.action.get_button";
    /**
     * This extra contains a Button object representing the button that the user
     * has chosen
     */
    public static final String EXTRA_RESULT_BUTTON = "org.twinone.irremote.intent.extra.result_buttons";
    /**
     * If specified, {@link ProviderActivity} will open this provider instead of
     * the default
     */
    public static final String EXTRA_PROVIDER = "org.twinone.irremote.intent.extra.provider_name";
    /**
     * Common remotes (assets db)
     */
    public static final int PROVIDER_COMMON = 1;
    /**
     * Lirc online database
     */
    public static final int PROVIDER_LIRC = 2;
    /**
     * GlobalCaché online database
     */
    public static final int PROVIDER_GLOBALCACHE = 3;
    /**
     * Twinone online database
     */
    public static final int PROVIDER_TWINONE = 4;
    /**
     * On HTC Devices, learn a menu_main (or button)
     */
    public static final int PROVIDER_LEARN = 5;
    /**
     * My remotes
     */
    public static final int PROVIDER_LOCAL = 6;
    /**
     * Provides an empty menu_main (no buttons) or button (no code, color or text)
     */
    public static final int PROVIDER_EMPTY = 7;

    private static final String SAVE_TITLE = "save_title";
    private int mPendingSwitch = -1;
    private String mAction;
    private Transmitter mTransmitter;
    private int mInnerFragmentCurrentState;
    private int mInnerFragmentExitState;
    private ProviderNavFragment mNavFragment;
    private int mCurrentProvider;
    private String mTitle;
    private String mSavedTitle;

    private SaveRemoteDialog mSaveRemoteDialog;
    private RemotePreviewDialog mPreviewRemoteDialog;

    @Override
    protected void onPause() {
        super.onPause();
//        hideSaveRemoteDialog();
//        hideRemotePreviewDialog();
    }


    private void showSaveRemoteDialog(final Activity activity, final Remote remote) {
        if (getFragmentManager().findFragmentByTag(SaveRemoteDialog.DIALOG_TAG) != null) return;
        SaveRemoteDialog.newInstance(remote).show(activity);
    }

    private void hideSaveRemoteDialog() {
        SaveRemoteDialog d = (SaveRemoteDialog) getFragmentManager().findFragmentByTag(SaveRemoteDialog.DIALOG_TAG);
        if (d != null && d.getDialog().isShowing()) {
            d.dismiss();
        }
    }

    public void requestPreviewRemote(Remote remote) {
//        if (getFragmentManager().findFragmentByTag(RemotePreviewDialog.DIALOG_TAG) == null) {
        RemotePreviewDialog d = RemotePreviewDialog.newInstance(remote);
        mPreviewRemoteDialog = d.show(this);
//        }
    }

    private void hideRemotePreviewDialog() {
        if (mPreviewRemoteDialog != null) mPreviewRemoteDialog.dismiss();
    }

    public String getAction() {
        return mAction;
    }


    public void setCurrentState(int state) {
        mInnerFragmentCurrentState = state;
    }

    public void setExitState(int state) {
        mInnerFragmentExitState = state;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!ACTION_SAVE_REMOTE.equals(getIntent().getAction())
                && !ACTION_GET_BUTTON.equals(getIntent().getAction())) {
            throw new IllegalStateException(
                    "ProviderActivity should be called with one of ACTION_SAVE_REMOTE of ACTION_GET_BUTTON specified");
        }

        mAction = getIntent().getAction();


        setContentView(R.layout.activity_provider);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.androidlib_toolbar);
        setSupportActionBar(mToolbar);

        setupNavigation();

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SAVE_TITLE)) {
                setTitle(savedInstanceState.getString(SAVE_TITLE));
            }
        } else {
            int provider = getIntent().getIntExtra(EXTRA_PROVIDER, -1);
            if (provider != -1) {
                switchTo(provider);
            } else {
                setTitle(R.string.app_name);
                mNavFragment.lockOpen(true);
                onNavigationOpened();
            }
        }

    }

    private void setupNavigation() {
        mNavFragment = (ProviderNavFragment) getSupportFragmentManager()
                .findFragmentById(R.id.navigation_drawer);
        mNavFragment.setUp(R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        mNavFragment.setEdgeSizeDp(30);
        mNavFragment.setNavigationListener(this);

    }


    public void requestSaveButton(final Button button) {
        SaveButtonDialog.newInstance(button).show(this);
    }

    public void performSaveButton(final Button button) {
        Intent i = new Intent();
        i.putExtra(EXTRA_RESULT_BUTTON, button);
        setResult(Activity.RESULT_OK, i);
        finish();
    }


    /**
     * Use this method to prompt the user to save this menu_main
     */
    public void requestSaveRemote(Remote remote) {
        if (ACTION_GET_BUTTON.equals(getIntent().getAction())) {
            requestPreviewRemote(remote);
        } else {
            showSaveRemoteDialog(this, remote);
        }
    }

    public void performSaveRemote(Remote remote) {
        Iterator<Button> it = remote.buttons.iterator();
        while (it.hasNext()) {
            final Button b = it.next();
            if (b.code == null || b.code.isEmpty()) {
                it.remove();
            }
        }

        RemoteOrganizer ro = new RemoteOrganizer(this);
        ro.updateWithoutSaving(remote);
        RemoteOrganizer.addIcons(remote, false);

        remote.save(this);

        Remote.setLastUsedRemoteName(this, remote.name);
        Toast.makeText(this, R.string.remote_saved_toast,
                Toast.LENGTH_SHORT).show();
        finish();
    }


    @Override
    public void onBackPressed() {
        if (mInnerFragmentCurrentState == mInnerFragmentExitState) {
            finish();
        } else {
            getFragmentManager().popBackStack();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return onNavigateUp();
    }

    @Override
    public boolean onNavigateUp() {
        if (mInnerFragmentCurrentState == mInnerFragmentExitState) {
            finish();
        } else {
            getFragmentManager().popBackStack();
        }
        return true;
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        if (mNavFragment.isOpen()) {
            mSavedTitle = title.toString();
        } else {
            getSupportActionBar().setTitle(title);
            mTitle = (String) title;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SAVE_TITLE, mTitle);
    }

    public void transmit(Signal signal) {
        getTransmitter().transmit(signal);
    }

    public void addFragment(ProviderFragment fragment) {
        String name = "provider_fragment_id_" + getFragmentCount();
        Log.d("FragTest", "Adding: " + name);
        getFragmentManager().beginTransaction()
                .replace(R.id.container, fragment).addToBackStack(name)
                .commit();
    }


    private int getFragmentCount () {
        return getFragmentManager().getBackStackEntryCount();
    }

    public ProviderFragment getCurrentFragment() {
        FragmentManager.BackStackEntry entry = getFragmentManager().getBackStackEntryAt(getFragmentManager().getBackStackEntryCount() - 1);
        Log.d("FragTest", "GetCurrentFragment: " + entry.getName());
        return (ProviderFragment) getFragmentManager().findFragmentByTag(entry.getName());
    }

    public Transmitter getTransmitter() {
        // Lazy initialization
        if (mTransmitter == null) {
            mTransmitter = Transmitter.getInstance(this);
        }
        return mTransmitter;
    }

    @Override
    public void finish() {
        super.finish();
        AnimHelper.onFinish(this);
    }

    public void popAllFragments() {
        getFragmentManager().popBackStack(null,
                FragmentManager.POP_BACK_STACK_INCLUSIVE);
        mNavFragment.lockOpen(true);
        mCurrentProvider = 0;
    }

    public void switchTo(int provider) {
        if (provider == mCurrentProvider)
            return;
        if (mNavFragment.isOpen()) {
            mPendingSwitch = provider;
            mNavFragment.close();
        } else {
            switchToImpl(provider);
        }
    }

    private void switchToImpl(int provider) {
        Log.d("", "SwitchToImpl");
        popAllFragments();
        switch (provider) {
            case PROVIDER_GLOBALCACHE:
                addFragment(new GCProviderFragment());
                break;
            case PROVIDER_LEARN:
                if (mAction.equals(ACTION_SAVE_REMOTE))
                    addFragment(new LearnRemoteProviderFragment());
                else
                    addFragment(new LearnButtonProviderFragment());
                break;
            case PROVIDER_TWINONE:
                addFragment(new TwinoneProviderFragment());
                break;
            case PROVIDER_LOCAL:
                addFragment(new LocalProviderFragment());
                break;
            default:
                addFragment(new CommonProviderFragment());
                break;
        }
        mNavFragment.unlock();
        mNavFragment.close();
        mCurrentProvider = provider;
    }

    @Override
    public void onNavigationOpened() {
        Log.i("", "OnNavigationOpened");
        mSavedTitle = getTitle().toString();
        if (ACTION_GET_BUTTON.equals(mAction)) {
            getSupportActionBar().setTitle(R.string.title_provider_add_button);
        } else {
            getSupportActionBar().setTitle(R.string.title_provider_add_remote);
        }
    }

    @Override
    public void onNavigationClosed() {
        Log.i("", "OnNavigationCLosed");
        getSupportActionBar().setTitle(mSavedTitle);

        if (mPendingSwitch != -1) {
            switchToImpl(mPendingSwitch);
            mPendingSwitch = -1;
        }
    }



}
