package org.twinone.irremote.ir.io;

import android.content.Context;
import android.hardware.ConsumerIrManager;
import android.hardware.ConsumerIrManager.CarrierFrequencyRange;
import android.util.Log;

import org.twinone.irremote.ir.Signal;
import org.twinone.irremote.ir.SignalCorrector;

public class KitKatTransmitter extends Transmitter {

    private static final String TAG = "KitKatTransmitter";
    private final ConsumerIrManager mIrManager;
    private final Runnable mTransmitRunnable = new TransmitterRunnable();
    private SignalCorrector mSignalCorrector;
    private volatile boolean mWaitingForTransmission;
    private volatile Signal mSignal;
    private volatile boolean mHasTransmittedOnce;

    public KitKatTransmitter(Context context) {
        super(context);
        mIrManager = (ConsumerIrManager) context
                .getSystemService(Context.CONSUMER_IR_SERVICE);
        if (!isAvailable()) {
            throw new ComponentNotAvailableException(
                    "Transmitter not available on this device");
        }

        mSignalCorrector = new SignalCorrector(context);
    }

    boolean isAvailable() {
        return mIrManager.hasIrEmitter();
    }

    public void transmit() {
        if (isInvalidFrequency(mSignal.getFrequency()))
            return;
        transmitImpl(mSignal);
    }

    public void setSignal(Signal signal) {
        mHasTransmittedOnce = false;
        mSignal = signal;
    }

    public void startTransmitting() {
        if (isInvalidFrequency(mSignal.getFrequency()))
            return;
        if (mWaitingForTransmission)
            stopTransmitting(false);

        mWaitingForTransmission = true;
        // Log.d(TAG, "Setting hasTransmitted to false");
        // mRunnable = new TransmitterRunnable();
        mHandler.post(mTransmitRunnable);
    }

    private synchronized void transmitImpl(Signal signal) {
        if (signal == null)
            return;
        Signal realSignal = signal.clone().fix(mSignalCorrector);

        if (getListener() != null) {
            getListener().onBeforeTransmit();
        }
        setTransmitting(true);
        mIrManager.transmit(realSignal.getFrequency(), realSignal.getPattern());
        setTransmitting(false);
        if (getListener() != null) {
            getListener().onAfterTransmit();
        }
    }

    /**
     * @param transmitAtLeastOnce If this is set to true, the signal is transmitted at least
     *                            once
     */
    public void stopTransmitting(boolean transmitAtLeastOnce) {
        if (mHandler == null)
            Log.d(TAG, "Null handler");
        if (mTransmitRunnable == null)
            Log.d(TAG, "Null Runnable");

        mHandler.removeCallbacks(mTransmitRunnable);
        if (transmitAtLeastOnce && !mHasTransmittedOnce) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    transmitImpl(mSignal);
                }
            });
        } else {
            Log.d(TAG, "Not transmitting signal");
            mWaitingForTransmission = false;
        }
        mHasTransmittedOnce = false;

    }

    @Override
    public boolean hasTransmittedOnce() {
        return mHasTransmittedOnce;
    }

    private boolean isInvalidFrequency(int frequency) {
        for (CarrierFrequencyRange cfr : mIrManager.getCarrierFrequencies()) {
            if (frequency <= cfr.getMaxFrequency()
                    && frequency >= cfr.getMinFrequency()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void pause() {
        mHandler.removeCallbacks(mTransmitRunnable);
    }

    private class TransmitterRunnable implements Runnable {
        @Override
        public void run() {
            transmitImpl(mSignal);
            if (!mHasTransmittedOnce)
                mHasTransmittedOnce = true;

            if (mWaitingForTransmission) {
                Log.d(TAG, "Posting new runnable");
                mHandler.postDelayed(this, getPeriodMillis());
            }
        }
    }

}
