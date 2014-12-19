package org.twinone.irremote.debug;

import android.content.Context;
import android.util.Log;

import org.twinone.irremote.ir.Signal;
import org.twinone.irremote.ir.io.Transmitter;

public class DebugTransmitter extends Transmitter {

    public DebugTransmitter(Context context) {
        super(context);
        Log.w("DebugTransmitter", "Using debug transmitter");
    }

    @Override
    public void setSignal(Signal signal) {
    }

    @Override
    public void transmit() {
    }

    @Override
    public void startTransmitting() {
    }

    @Override
    public void stopTransmitting(boolean atLeastOnce) {
    }

    @Override
    public boolean hasTransmittedOnce() {
        return false;
    }

    @Override
    public void pause() {
    }

}
