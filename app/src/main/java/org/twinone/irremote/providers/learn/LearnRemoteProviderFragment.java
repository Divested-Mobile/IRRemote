package org.twinone.irremote.providers.learn;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.twinone.irremote.R;
import org.twinone.irremote.components.Button;
import org.twinone.irremote.components.ComponentUtils;
import org.twinone.irremote.components.Remote;
import org.twinone.irremote.ir.Signal;
import org.twinone.irremote.ir.SignalFactory;

import de.passsy.holocircularprogressbar.HoloCircularProgressBar;

public class LearnRemoteProviderFragment extends BaseLearnProviderFragment
        implements View.OnClickListener, DialogInterface.OnClickListener {

    private static final String SAVE_DEVICE_TYPE = "save_device_type";
    private static final String SAVE_NAME = "save_name";

    private static final String ACTION_SAVE_REMOTE = "org.twinone.irremote.intent.action.save_remote";
    private static final String ACTION_GET_BUTTON = "org.twinone.irremote.intent.action.get_button";

    private static final String ARG_REMOTE_TYPE = "arg_remote_type";
    private static final String SAVE_BUTTON_NUMBER = "button_number";
    private static final String SAVE_REMOTE = "save_remote";

    private String mName;
    private int mType;

    private Remote mRemote;

    private TextView mTitle;
    private TextView mDescription;
    private TextView mFooter;
    private android.widget.Button mBottomLeft;
    private android.widget.Button mBottomRight;
    private android.widget.Button mBottomCenter;
    private android.widget.Button mCenterButton;

    private Signal mSignal;
    private int mCurrentButtonIndex = 0;
    private AlertDialog mSelectDialog;

    public static LearnRemoteProviderFragment getInstance(int remoteType) {
        LearnRemoteProviderFragment f = new LearnRemoteProviderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_REMOTE_TYPE, remoteType);
        f.setArguments(args);
        return f;
    }

    private Button getCurrentButton() {
        return mRemote.buttons.get(mCurrentButtonIndex);
    }

    private String getCurrentButtonName() {
        return getCurrentButton().getText();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void createRemoteAndSetType(int type) {
        mRemote = ComponentUtils.createEmptyRemote(getActivity(), type);
        Log.d("", "Creating empty remote");
        Log.d("", "Remote null :" + (mRemote == null));
        setType(type);
    }

    private void setType(int type) {
        mType = type;
        mName = getResources().getStringArray(R.array.learn_device_types)[mType];
        getActivity().setTitle(getString(R.string.learn_activity_title, mName));
        setupStateForCurrentButton();
    }

    public void saveRemote(Remote remote) {
        remote.name = mName;
        remote.addFlags(Remote.FLAG_LEARNED);
        Log.d("", "Name: " + mName);
        getProvider().saveRemote(remote);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_learn_remote, container,
                false);

        mTitle = (TextView) view.findViewById(R.id.learn_remote_title);
        mDescription = (TextView) view
                .findViewById(R.id.learn_remote_description);
        mFooter = (TextView) view.findViewById(R.id.learn_remote_footer);

        mBottomLeft = (android.widget.Button) view
                .findViewById(R.id.learn_btn_btm_left);
        mCenterButton = (android.widget.Button) view
                .findViewById(R.id.learn_btn_center);
        mBottomRight = (android.widget.Button) view
                .findViewById(R.id.learn_btn_btm_right);
        mBottomCenter = (android.widget.Button) view
                .findViewById(R.id.learn_btn_btm_center);

        mBottomLeft.setOnClickListener(this);
        mBottomCenter.setOnClickListener(this);
        mCenterButton.setOnClickListener(this);
        mBottomRight.setOnClickListener(this);

        HoloCircularProgressBar bar = (HoloCircularProgressBar) view
                .findViewById(R.id.learn_progress);
        setProgressBar(bar);
        if (mRemote != null)
            setupStateForCurrentButton();

        onRestoreInstance(savedInstanceState);
        logButtons();
        return view;

    }

    private void logButtons() {
        if (mRemote == null) {
            Log.d("", "Cannot log buttons: null remote");
            return;
        }
        int c = 0;
        for (Button b : mRemote.buttons) {
            if (b.code != null)
                c++;
        }
        Log.d("", "Total buttons: " + mRemote.buttons.size() + ", nonempty: "
                + c);
    }

    private void setupStateForCurrentButton() {
        if (getCurrentButton().code != null) {
            setState(State.SAVED);
            setupState(State.SAVED);
            updateProgress();
        } else {
            setupState(State.READY);
        }
    }

    private void onRestoreInstance(Bundle savedInstanceState) {
        if (savedInstanceState != null
                && savedInstanceState.containsKey(SAVE_NAME)) {
            Log.d("", "restoreInstance: A");
            mCurrentButtonIndex = savedInstanceState.getInt(SAVE_BUTTON_NUMBER);
            mRemote = (Remote) savedInstanceState.getSerializable(SAVE_REMOTE);
            mName = savedInstanceState.getString(SAVE_NAME);
            setType(savedInstanceState.getInt(SAVE_DEVICE_TYPE));
        } else if (getArguments() != null
                && getArguments().containsKey(ARG_REMOTE_TYPE)) {
            Log.d("", "restoreInstance: B");
            createRemoteAndSetType(getArguments().getInt(ARG_REMOTE_TYPE));
        } else {
            Log.d("", "restoreInstance: C");
            showSelectDialog();
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(SAVE_BUTTON_NUMBER, mCurrentButtonIndex);

        if (mRemote != null) {
            outState.putString(SAVE_NAME, mName);
            outState.putInt(SAVE_DEVICE_TYPE, mType);
            outState.putSerializable(SAVE_REMOTE, mRemote);
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void learnStart() {
        super.learnStart();
        setupState(State.LEARNING);
    }

    @Override
    protected void learnStop() {
        super.learnStop();
        setupStateForCurrentButton();
    }

    private void setupButton(int index) {
        mCurrentButtonIndex = index;
        updateFooterText();
    }

    private void updateFooterText() {
        setFooterText(getString(R.string.learn_footer, mCurrentButtonIndex + 1,
                mRemote.buttons.size()));
    }

    private void setFooterText(String text) {
        mFooter.setVisibility(View.VISIBLE);
        mFooter.setText(text);

    }

    private void onPreviousClicked() {
        if (mCurrentButtonIndex > 0) {
            setupButton(mCurrentButtonIndex - 1);
        }
        learnStop();
    }

    private void onNextClicked() {
        if (mCurrentButtonIndex < mRemote.buttons.size() - 1) {
            setupButton(mCurrentButtonIndex + 1);
            learnStop();
        } else {
            saveRemote(mRemote);
        }
    }

    private void setupState(State state) {
        switch (state) {
            case READY:
                mTitle.setText(getString(R.string.learn_tit_ready,
                        getCurrentButtonName()));
                mDescription.setText(getString(R.string.learn_desc_ready,
                        getCurrentButtonName()));

                mCenterButton.setEnabled(true);
                mCenterButton.setText(R.string.learn_button_ready);

                mBottomLeft.setVisibility(View.VISIBLE);
                mBottomCenter.setVisibility(View.GONE);
                mBottomRight.setVisibility(View.VISIBLE);

                mBottomLeft.setText(R.string.learn_button_back);
                if (mCurrentButtonIndex == mRemote.buttons.size() - 1) {
                    mBottomRight.setText(R.string.learn_button_finish);
                } else {
                    mBottomRight.setText(R.string.learn_button_skip);
                }

                updateFooterText();
                break;

            case LEARNING:
                mTitle.setText(R.string.learn_tit_learning);
                mDescription.setText(getString(R.string.learn_desc_learning,
                        getCurrentButtonName()));
                mCenterButton.setText(R.string.learn_learning);
                mCenterButton.setEnabled(false);

                mBottomLeft.setVisibility(View.GONE);
                mBottomCenter.setVisibility(View.VISIBLE);
                mBottomRight.setVisibility(View.GONE);

                mBottomCenter.setText(R.string.learn_button_cancel);

                updateFooterText();
                break;

            case LEARNED:
                mTitle.setText(R.string.learn_tit_learned);
                mDescription.setText(getString(R.string.learn_desc_learned,
                        getCurrentButtonName()));
                mCenterButton.setText(getCurrentButtonName());
                mCenterButton.setEnabled(true);

                mBottomLeft.setVisibility(View.INVISIBLE);
                mBottomCenter.setVisibility(View.INVISIBLE);
                mBottomRight.setVisibility(View.INVISIBLE);

                mFooter.setVisibility(View.INVISIBLE);
                break;

            case LEARNED_TRIED:
                mBottomLeft.setVisibility(View.VISIBLE);
                mBottomCenter.setVisibility(View.VISIBLE);
                mBottomRight.setVisibility(View.VISIBLE);

                mBottomLeft.setText(R.string.learn_button_skip);
                mBottomCenter.setText(R.string.learn_button_try_again);
                mBottomRight.setText(R.string.learn_button_it_works);

                setFooterText(getString(R.string.learn_footer_did_it_work));
                break;

            case SAVED:
                mTitle.setText(getString(R.string.learn_tit_saved,
                        getCurrentButtonName()));
                mDescription.setText(R.string.learn_desc_saved);

                mCenterButton.setText(getCurrentButtonName());
                mCenterButton.setEnabled(true);

                mBottomLeft.setVisibility(View.VISIBLE);
                mBottomCenter.setVisibility(View.VISIBLE);
                mBottomRight.setVisibility(View.VISIBLE);

                mBottomLeft.setText(R.string.learn_button_back);
                mBottomCenter.setText(R.string.learn_button_try_again);
                mBottomRight.setText(R.string.learn_button_next);

                mFooter.setVisibility(View.VISIBLE);
                updateFooterText();
                break;
        }

    }

    @Override
    protected void learnConfirm(Signal s) {
        super.learnConfirm(s);
        mSignal = s;
        Log.d("", "Received: " + s.toString());
        setupState(State.LEARNED);
    }

    @Override
    protected void onLearnTimeout() {
        AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
        ab.setTitle(R.string.learn_help_tit);
        ab.setMessage(R.string.learn_help_msg);
        ab.setPositiveButton(android.R.string.ok, null);
        ab.show();
    }

    private void saveCurrentButton() {
        final String code = SignalFactory.toPronto(mSignal);
        Button b = mRemote.buttons.get(mCurrentButtonIndex);
        b.code = code;
    }

    @Override
    public void onClick(View v) {
        final State state = getState();
        Log.d("", "Click: Current State: " + state);
        final int id = v.getId();

        switch (state) {
            case READY:
                if (id == R.id.learn_btn_center) {
                    learnStart();
                } else if (id == R.id.learn_btn_btm_left) {
                    onPreviousClicked();
                } else if (id == R.id.learn_btn_btm_right) {
                    onNextClicked();
                }
                break;
            case LEARNING:
                if (id == R.id.learn_btn_btm_center) {
                    learnStop();
                }
                break;
            case LEARNED:
                if (id == R.id.learn_btn_center) {
                    if (mSignal != null) {
                        getTransmitter().transmit(mSignal);
                        setupState(State.LEARNED_TRIED);
                    }
                } else if (id == R.id.learn_btn_btm_left) {
                    onNextClicked();
                } else if (id == R.id.learn_btn_btm_center) {
                    learnStart();
                } else if (id == R.id.learn_btn_btm_right) {
                    saveCurrentButton();
                    onNextClicked();
                }
                break;
            case SAVED:
                if (id == R.id.learn_btn_center) {
                    if (mSignal != null) {
                        getTransmitter().transmit(mSignal);
                    }
                } else if (id == R.id.learn_btn_btm_left) {
                    onPreviousClicked();
                } else if (id == R.id.learn_btn_btm_center) {
                    learnStart();
                } else if (id == R.id.learn_btn_btm_right) {
                    onNextClicked();
                }
                break;
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_NEGATIVE) {
            getActivity().finish();
        } else {
            createRemoteAndSetType(which);
            dialog.dismiss();
        }
    }

    @Override
    public void onPause() {
        dismissDialog();
        super.onPause();
    }

    private void dismissDialog() {
        if (mSelectDialog != null && mSelectDialog.isShowing())
            mSelectDialog.dismiss();

    }

    private void showSelectDialog() {
        dismissDialog();
        AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
        ab.setTitle(R.string.learn_select_tit);
        ab.setItems(R.array.learn_device_types, this);
        ab.setNegativeButton(android.R.string.cancel, this);
        ab.setCancelable(false);
        mSelectDialog = ab.show();
    }

}
