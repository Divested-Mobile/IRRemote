package org.twinone.irremote.debug;

import android.content.Context;
import android.util.Log;

import org.twinone.irremote.ir.io.Receiver;

public class DebugReceiver extends Receiver {

    public DebugReceiver(Context context) {
        super(context);
        Log.w("DebugReceiver", "Using debug receiver");
    }

    @Override
    public boolean isReceiving() {
        return false;
    }

    @Override
    public void learn(final int timeoutSecs) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(timeoutSecs * 1000);
                    getListener().onError(-1);
                } catch (InterruptedException e) {
                    getListener().onError(-1);
                }
            }
        }).start();
    }

    @Override
    public void cancel() {
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }

    @Override
    public boolean isAvailable() {
        return false;
    }

}
