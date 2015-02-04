package org.twinone.irremote.ir.io;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

import com.htc.circontrol.CIRControl;
import com.htc.htcircontrol.HtcIrData;

import org.twinone.irremote.ir.Signal;
import org.twinone.irremote.ir.SignalCorrector;

public class HTCTransmitter extends Transmitter {

    private CIRControl mCirControl;
    private static final String TAG = "HTCTransmitter";

    private Context mContext;
    private Handler mHandler = new Handler();

    private boolean mWaitingForTransmission;
    private boolean mHasTransmittedOnce = false;
    private TransmitRunnable mTransmitRunnable = new TransmitRunnable();


    HTCTransmitter(Context context) {
        super(context);
        try {
            mContext = context;
            checkAvailable();
            mCirControl = new CIRControl(context, mHandler);
            mCirControl.start();
        } catch (NoClassDefFoundError e) {
            throw new ComponentNotAvailableException();
        }
    }

    private HtcIrData mHtcIrData;

    @Override
    public void setSignal(Signal signal) {
//        signal.fix(new SignalCorrector(mContext));
//        int freq = signal.getFrequency();
//        int[] patt = signal.getPattern();
//        for (int i = 0; i < patt.length; i++) {
//            patt[i] = patt[i] / freq;
//        }
//        Signal s = new Signal(freq, patt);

        mHtcIrData = getHtcIrData(signal);
    }

    @Override
    protected void transmit() {
        if (mHtcIrData == null)
            throw new NullPointerException("You must call setSignal before transmit()");
        Log.d("", "Transmitting!!!");
        transmitImpl();
    }

    private synchronized void transmitImpl() {
        mCirControl.transmitIRCmd(mHtcIrData, false);
    }

    private HtcIrData getHtcIrData(Signal s) {
        HtcIrData res = new HtcIrData();
        res.setRepeatCount(1);
        res.setFrame(s.getPattern());
        res.setFrequency(s.getFrequency());
        return res;
    }

    @Override
    public void startTransmitting() {
        Log.d("", "startTransmitting()");
        if (mWaitingForTransmission)
            stopTransmitting(false);

        // Could happen that startTransmitting is called twice with the same
        // signal
//        Log.d("", "Setting mHasTransmittedOnce to false!!!");
//        mHasTransmittedOnce = false;

        mWaitingForTransmission = true;
        // Log.d(TAG, "Setting hasTransmitted to false");
        // mRunnable = new TransmitterRunnable();
        mHandler.post(mTransmitRunnable);
    }

    private class TransmitRunnable implements Runnable {
        @Override
        public void run() {
            transmitImpl();
            if (!mHasTransmittedOnce)
                mHasTransmittedOnce = true;

            if (mWaitingForTransmission) {
                Log.d(TAG, "Posting new runnable");
                mHandler.postDelayed(this, getPeriodMillis());
            }
        }
    }

    @Override
    public void stopTransmitting(boolean atLeastOnce) {
        if (mHandler == null)
            Log.d(TAG, "Null handler");
        if (mTransmitRunnable == null)
            Log.d(TAG, "Null Runnable");

        mHandler.removeCallbacks(mTransmitRunnable);
        if (atLeastOnce && !mHasTransmittedOnce) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    transmitImpl();
                }
            });
        } else {
            Log.d(TAG, "Not transmitting: atLeastOnce=" + atLeastOnce + ", mHasTransmittedOnce=" + mHasTransmittedOnce);
            mWaitingForTransmission = false;
        }
        mHasTransmittedOnce = false;
    }

    @Override
    public boolean hasTransmittedOnce() {
        return mHasTransmittedOnce;
    }

    @Override
    public void pause() {

    }

    public static boolean isHTCTransmitterAvailable(Context c) {
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
        if (!isHTCTransmitterAvailable(mContext)) {
            throw new ComponentNotAvailableException(
                    "The package com.htc.cirmodule was not installed");
        }
    }

    public boolean isAvailable() {
        return isHTCTransmitterAvailable(mContext);
    }

}
