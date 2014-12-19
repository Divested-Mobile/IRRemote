package org.twinone.irremote.ir.io;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.widget.Toast;

import com.htc.circontrol.CIRControl;
import com.htc.htcircontrol.HtcIrData;

import org.twinone.irremote.ir.Signal;
import org.twinone.irremote.ir.io.HTCReceiverHandler.OnMessageListener;

public class HTCReceiver extends Receiver implements OnMessageListener {

    private CIRControl mCirControl;

    private Context mContext;

    HTCReceiver(Context context) {
        super(context);
        Handler mHandler = new HTCReceiverHandler(this);
        try {
            mContext = context;
            mCirControl = new CIRControl(context, mHandler);
            checkAvailable();
        } catch (NoClassDefFoundError e) {
            throw new ComponentNotAvailableException();
        }
    }

    public static boolean isHTCReceiverAvailable(Context c) {
        return getPreferences(c).getBoolean("available", false);
    }

    private static SharedPreferences getPreferences(Context c) {
        return c.getSharedPreferences("receiver", Context.MODE_PRIVATE);
    }

    public static void setReceiverAvailableOnce(Context c) {
        SharedPreferences sp = getPreferences(c);
        if (sp.contains("available")) {
            return;
        }
        sp.edit().putBoolean("available", isPackageAvailable(c)).apply();
    }

    private static boolean isPackageAvailable(Context c) {
        try {
            c.getPackageManager().getPackageInfo("com.htc.cirmodule", 0);
            return true;
        } catch (Exception ignored) {
        }

        return false;
    }

    private void checkAvailable() {
        if (!isHTCReceiverAvailable(mContext)) {
            throw new ComponentNotAvailableException(
                    "The package com.htc.cirmodule was not installed");
        }
    }

    public boolean isAvailable() {
        return isHTCReceiverAvailable(mContext);
    }

    @Override
    public boolean isReceiving() {
        return mCirControl.isStarted();
    }

    @Override
    public void learn(int timeoutSecs) {
        mCirControl.learnIRCmd(timeoutSecs);
    }

    @Override
    public void cancel() {
        mCirControl.cancelCommand();
    }

    @Override
    public void onReceiveComplete(HtcIrData code) {
        Toast.makeText(mContext, "Reps: " + code.getRepeatCount(),
                Toast.LENGTH_LONG).show();
        Signal s = new Signal(code.getFrequency(), code.getFrame());
        getListener().onLearn(s);
    }

    @Override
    public void onReceiveCancel() {
        getListener().onLearnCancel();
    }

    @Override
    public void onReceiveStart() {
        getListener().onLearnStart();
    }

    @Override
    public void onError(int errorCode) {
        getListener().onError(errorCode);
    }

    @Override
    public void start() {
        mCirControl.start();
    }

    @Override
    public void stop() {
        mCirControl.stop();
    }

    @Override
    public void onTimeout() {
        getListener().onTimeout();
    }
}
