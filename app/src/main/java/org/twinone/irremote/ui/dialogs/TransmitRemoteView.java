package org.twinone.irremote.ui.dialogs;

import android.content.Context;

import org.twinone.irremote.components.Remote;
import org.twinone.irremote.components.TransmitOnTouchListener;
import org.twinone.irremote.ir.io.Transmitter;
import org.twinone.irremote.ui.ButtonView;
import org.twinone.irremote.ui.RemoteView;

/**
 * Created by twinone on 1/25/15.
 */
public class TransmitRemoteView extends RemoteView {

    private Transmitter mTransmitter;
    private TransmitOnTouchListener mTransmitOnTouchListener;

    public TransmitRemoteView(Context context, Remote remote) {
        super(context, remote);
    }

    @Override
    protected void init() {
        super.init();
        mTransmitter = Transmitter.getInstance(getContext());
        mTransmitOnTouchListener = new TransmitOnTouchListener(mTransmitter);
    }

    @Override
    protected void setupButton(ButtonView bv) {
        super.setupButton(bv);
        bv.setOnTouchListener(mTransmitOnTouchListener);
    }
}
