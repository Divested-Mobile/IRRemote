package org.twinone.irremote.ui;

import android.content.SharedPreferences;
import android.os.Bundle;

import org.twinone.irremote.R;
import org.twinone.irremote.components.TransmitOnTouchListener;

public class DefaultRemoteFragment extends BaseRemoteFragment {

    private TransmitOnTouchListener mTransmitOnTouchListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getTransmitter() == null) {
            return;
        }

        SharedPreferences sp = SettingsActivity.getPreferences(getActivity());
        boolean vibrate = sp.getBoolean(getString(R.string.pref_key_vibrate),
                getResources().getBoolean(R.bool.pref_def_vibrate));

        mTransmitOnTouchListener = new TransmitOnTouchListener(getTransmitter());
        mTransmitOnTouchListener.setHapticFeedbackEnabled(vibrate);

    }

    @Override
    protected void setupButtons() {
        super.setupButtons();
        for (ButtonView bv : mButtons) {
            bv.setOnTouchListener(mTransmitOnTouchListener);
        }
    }

}
