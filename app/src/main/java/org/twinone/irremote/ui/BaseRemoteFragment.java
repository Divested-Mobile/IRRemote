package org.twinone.irremote.ui;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
 * Displays the remote.
 *
 * @author twinone
 */
public abstract class BaseRemoteFragment extends Fragment {

    private static final String TAG = "RemoteFragment";
    private static final String SAVE_REMOTE = "save_remote";
    private static final String ARG_REMOTE_NAME = "arg_remote_name";
    final Handler mHandler = new Handler();
    Remote mRemote;
    List<ButtonView> mButtons = new ArrayList<>();
    // protected ComponentUtils mComponentUtils;

    RemoteView mRemoteView;
    ScrollView mScroll;
    private Transmitter mTransmitter;

    public final void showFor(Activity a, String remoteName) {
        showFor(a, remoteName, null);
    }

    /**
     * Use this method just after calling the constructor
     */
    public final void showFor(Activity a, String remoteName, String tag) {

        Bundle b = new Bundle();
        b.putSerializable(ARG_REMOTE_NAME, remoteName);
        setArguments(b);
        a.getFragmentManager().beginTransaction()
                .replace(R.id.container, this, tag).commit();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("BaseRemoteFragment", "OnCreate");
        if (getArguments() == null
                || !getArguments().containsKey(ARG_REMOTE_NAME)) {
            throw new RuntimeException(
                    "You should create this fragment with the showFor method");
        }

        if (savedInstanceState != null) {
            Log.d(TAG, "Retrieving remote from savedInstanceState");
            mRemote = (Remote) savedInstanceState.getSerializable(SAVE_REMOTE);
        } else {
            mRemote = Remote.load(getActivity(), (String) getArguments()
                    .getSerializable(ARG_REMOTE_NAME));
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
        Log.i("BaseRemoteFragment", "OnCreateView");

        setHasOptionsMenu(true);

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

    void setupButtons() {
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
    }

    Transmitter getTransmitter() {
        return mTransmitter;
    }

    Remote getRemote() {
        return mRemote;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mTransmitter != null)
            mTransmitter.resume();

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mTransmitter != null)
            mTransmitter.pause();
    }

}
