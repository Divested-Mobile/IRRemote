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
import org.twinone.irremote.ir.Signal;
import org.twinone.irremote.ir.io.Transmitter;
import org.twinone.irremote.providers.common.CommonProviderFragment;
import org.twinone.irremote.providers.common.CommonProviderFragment.CommonProviderData;
import org.twinone.irremote.providers.globalcache.GCProviderFragment;
import org.twinone.irremote.providers.globalcache.GlobalCacheProviderData;
import org.twinone.irremote.providers.learn.LearnRemoteProviderFragment;
import org.twinone.irremote.providers.lirc.LircProviderData;
import org.twinone.irremote.providers.lirc.LircProviderFragment;
import org.twinone.irremote.ui.ProviderNavFragment;
import org.twinone.irremote.ui.dialogs.SaveButtonDialog;
import org.twinone.irremote.ui.dialogs.SaveButtonDialog.OnSaveButton;
import org.twinone.irremote.ui.dialogs.SaveRemoteDialog;
import org.twinone.irremote.ui.dialogs.SaveRemoteDialog.OnRemoteSavedListener;

public class ProviderActivity extends ActionBarActivity implements
        NavigationListener {

    /**
     * The user will select a remote which will be saved directly
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
     * On HTC Devices, learn a remote (or button)
     */
    public static final int PROVIDER_LEARN = 5;
    /**
     * My remotes
     */
    public static final int PROVIDER_LOCAL = 6;
    /**
     * Provides an empty remote (no buttons) or button (no code, color or text)
     */
    public static final int PROVIDER_EMPTY = 7;
    private static final String SAVE_TITLE = "save_title";
    int mPendingSwitch = -1;
    private String mAction;
    private Transmitter mTransmitter;
    private int mInnerFragmentCurrentState;
    private int mInnerFragmentExitState;
    private ProviderNavFragment mNavFragment;
    private int mCurrentProvider;
    private Toolbar mToolbar;
    private String mTitle;
    private String mSavedTitle;

    public static void saveRemote(final Activity activity, Remote remote) {
        SaveRemoteDialog dialog = SaveRemoteDialog.newInstance(remote);
        dialog.setListener(new OnRemoteSavedListener() {

            @Override
            public void onRemoteSaved(String name) {
                // Finish the activity, we've saved the remote
                Remote.setLastUsedRemoteName(activity, name);
                Toast.makeText(activity, R.string.remote_saved_toast,
                        Toast.LENGTH_SHORT).show();
                activity.finish();
            }
        });
        dialog.show(activity);
    }

    public String getAction() {
        return mAction;
    }

    public Toolbar getToolbar() {
        return mToolbar;
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

        if (getIntent().getAction() == null) {
            throw new IllegalStateException(
                    "ProviderActivity should be called with one of ACTION_SAVE_REMOTE of ACTION_GET_BUTTON specified");
        }

        mAction = getIntent().getAction();

        setContentView(R.layout.activity_provider);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.inflateMenu(R.menu.db_menu);

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

    /**
     * Use this method to send the selected button back to the calling activity
     */

    public void saveButton(final Button button) {
        SaveButtonDialog d = SaveButtonDialog.newInstance(button);
        d.setListener(new OnSaveButton() {

            @Override
            public void onSaveButton(Button result) {
                Intent i = new Intent();
                i.putExtra(EXTRA_RESULT_BUTTON, result);
                setResult(Activity.RESULT_OK, i);
                finish();
            }
        });
        d.show(this);
    }

    /**
     * Use this method to prompt the user to save this remote
     */
    public void saveRemote(Remote remote) {
        saveRemote(this, remote);
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
        Log.d("TAG", "onnavigateup");

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
        Log.w("ProviderActivity", "Adding fragment!");
        getFragmentManager().beginTransaction()
                .replace(R.id.container, fragment).addToBackStack("default")
                .commit();
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

    public void addCommonProviderFragment(CommonProviderData data) {
        setExitState(CommonProviderData.TARGET_DEVICE_TYPE);

        mInnerFragmentCurrentState = data.targetType;
        CommonProviderFragment frag = new CommonProviderFragment();
        Bundle args = new Bundle();
        args.putSerializable(CommonProviderFragment.ARG_DATA, data);
        frag.setArguments(args);
        addFragment(frag);
    }

    public void addGCProviderFragment(GlobalCacheProviderData data) {
        setExitState(CommonProviderData.TARGET_DEVICE_TYPE);

        mInnerFragmentCurrentState = data.targetType;
        GCProviderFragment frag = new GCProviderFragment();
        Bundle args = new Bundle();
        args.putSerializable(GCProviderFragment.ARG_URI_DATA, data);
        frag.setArguments(args);
        addFragment(frag);
    }

    public void addLircProviderFragment(LircProviderData data) {
        mInnerFragmentCurrentState = data.targetType;
        LircProviderFragment frag = new LircProviderFragment();
        Bundle args = new Bundle();
        args.putSerializable(LircProviderFragment.ARG_URI_DATA, data);
        frag.setArguments(args);
        addFragment(frag);
    }

    public void popAllFragments() {
        getFragmentManager().popBackStack(null,
                FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public void popFragment() {
        getFragmentManager().popBackStack();
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
                addFragment(new LearnRemoteProviderFragment());
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
