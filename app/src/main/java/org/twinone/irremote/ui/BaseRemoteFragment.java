package org.twinone.irremote.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import org.twinone.irremote.R;
import org.twinone.irremote.components.Remote;
import org.twinone.irremote.ir.io.Transmitter;

import java.util.ArrayList;
import java.util.List;

/**
 * Displays the menu_main.
 *
 * @author twinone
 */
public abstract class BaseRemoteFragment extends Fragment {

    public static final String FRAGMENT_TAG = "BaseRemoteFragment";
    private static final String TAG = "RemoteFragment";
    private static final String SAVE_REMOTE = "save_remote";
    private static final String ARG_REMOTE = "arg_remote";
    protected final Handler mHandler = new Handler();
    protected Remote mRemote;
    protected List<ButtonView> mButtons = new ArrayList<>();
    // protected ComponentUtils mComponentUtils;

    RemoteView mRemoteView;
    ScrollView mScroll;
    private Transmitter mTransmitter;

    public BaseRemoteFragment() {
    }

    public void prepareForRemote(Remote remote) {
        Bundle b = new Bundle();
        b.putSerializable(ARG_REMOTE, remote);
        setArguments(b);
    }
    /**
     * Use this method just after calling the constructor
     */
    public final BaseRemoteFragment showFor(Activity a, String remoteName) {
        return showFor(a, Remote.load(a, remoteName));
    }

    public final BaseRemoteFragment showFor(FragmentManager fm, Remote remote) {
        prepareForRemote(remote);
        fm.beginTransaction().add(this, FRAGMENT_TAG).commit();
        return this;
    }

    public final BaseRemoteFragment showFor(Activity a, Remote remote) {
        prepareForRemote(remote);
        a.getFragmentManager().beginTransaction()
                .replace(R.id.container, this, FRAGMENT_TAG).commit();
        return this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(false);
        Log.i("BaseRemoteFragment", "OnCreate");
        if (getArguments() == null
                || !getArguments().containsKey(ARG_REMOTE)) {
            throw new RuntimeException(
                    "You should create this fragment with the showFor method");
        }

        if (savedInstanceState != null) {
            Log.d(TAG, "Retrieving menu_main from savedInstanceState");
            mRemote = (Remote) savedInstanceState.getSerializable(SAVE_REMOTE);
        } else {
            mRemote = (Remote) getArguments().getSerializable(ARG_REMOTE);
        }
        mTransmitter = Transmitter.getInstance(getActivity());

        // mComponentUtils = new ComponentUtils(getActivity());

    }

    /**
     * Call super.onCreateView for theming and optionsMenu
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mRemote == null) {
            return new View(getActivity());
        }

        mScroll = (ScrollView) inflater.inflate(R.layout.fragment_remote_new,
                container, false);

        mRemoteView = (RemoteView) mScroll.findViewById(R.id.container);
        mRemoteView.setRemote(mRemote);
        setupButtons();

        return mScroll;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(SAVE_REMOTE, mRemote);
        super.onSaveInstanceState(outState);
    }

    protected void setupButtons() {
        mRemoteView.removeAllViews();
        mButtons = new ArrayList<>(mRemote.buttons.size());
        for (org.twinone.irremote.components.Button b : mRemote.buttons) {
            ButtonView bv = new ButtonView(getActivity());
            bv.setButton(b);

            mButtons.add(bv);
            mRemoteView.addView(bv);
            // bv.setX(b.x);
            // bv.setY(b.y);
            bv.getLayoutParams().width = (int) b.w;
            bv.getLayoutParams().height = (int) b.h;
            bv.requestLayout();
        }
        mRemoteView.setGravity(Gravity.CENTER_HORIZONTAL);
    }

    Transmitter getTransmitter() {
        return mTransmitter;
    }

    Remote getRemote() {
        return mRemote;
    }
}
