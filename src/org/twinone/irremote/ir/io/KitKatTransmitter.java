package org.twinone.irremote.ir.io;

import org.twinone.irremote.ir.Signal;
import org.twinone.irremote.ir.SignalCorrector;

import android.content.Context;
import android.hardware.ConsumerIrManager;
import android.hardware.ConsumerIrManager.CarrierFrequencyRange;
import android.os.Handler;
import android.util.Log;

public class KitKatTransmitter extends Transmitter {

	private static final String TAG = "KitKatTransmitter";
	private ConsumerIrManager mIrManager;
	private SignalCorrector mSignalCorrector;

	public KitKatTransmitter(Context context) {
		super(context);
		mIrManager = (ConsumerIrManager) context
				.getSystemService(Context.CONSUMER_IR_SERVICE);
		if (!isAvailable()) {
			throw new ComponentNotAvailableException(
					"Transmitter not available on this device");
		}

		mHandler = new Handler();
		mSignalCorrector = new SignalCorrector(context);
	}

	public boolean isAvailable() {
		return mIrManager.hasIrEmitter();
	}

	public void transmit() {
		if (!isFrequencySupported(mSignal.frequency))
			return;
		transmitImpl(mSignal);
	}

	public void setSignal(Signal signal) {
		mHasTransmittedOnce = false;
		mSignal = signal;
	}

	public void startTransmitting() {
		if (!isFrequencySupported(mSignal.frequency))
			return;
		if (mTransmitting)
			stopTransmitting(false);

		mTransmitting = true;
		Log.d(TAG, "Setting hasTransmitted to false");
		mRunnable = new TransmitterRunnable();
		mHandler.post(mRunnable);
	}

	private Runnable mRunnable;

	/**
	 * Time between the end of a transmission and the start of the next one
	 */
	private int mPeriodMillis = 200;

	/**
	 * If the user doesn't cancel in this time, we'll start transmitting
	 */

	/**
	 * Set how much each transmission should be away from another
	 * 
	 * @param millis
	 */
	public void setPeriodMillis(int millis) {
		mPeriodMillis = millis;
	}

	private Handler mHandler;
	private volatile boolean mTransmitting;
	private Signal mSignal;
	private volatile boolean mHasTransmittedOnce;

	private class TransmitterRunnable implements Runnable {
		@Override
		public void run() {
			transmitImpl(mSignal);
			if (!mHasTransmittedOnce)
				mHasTransmittedOnce = true;
			if (mTransmitting) {
				mHandler.postDelayed(this, mPeriodMillis);
			}
		}
	}

	private synchronized void transmitImpl(Signal signal) {
		if (signal == null)
			return;
		Signal realSignal = signal.clone().fix(mSignalCorrector);
		if (getListener() != null) {
			getListener().onBeforeTransmit();
		}
		mIrManager.transmit(realSignal.frequency, realSignal.pattern);
		if (getListener() != null) {
			getListener().onAfterTransmit();
		}
	}

	/**
	 * 
	 * @param transmitAtLeastOnce
	 *            If this is set to true, the signal is transmitted at least
	 *            once
	 */
	public void stopTransmitting(boolean transmitAtLeastOnce) {
		mHandler.removeCallbacks(mRunnable);
		if (transmitAtLeastOnce && !mHasTransmittedOnce) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					transmitImpl(mSignal);
					mTransmitting = false;
				}
			});
		}
	}

	private boolean isFrequencySupported(int frequency) {
		for (CarrierFrequencyRange cfr : mIrManager.getCarrierFrequencies()) {
			if (frequency <= cfr.getMaxFrequency()
					&& frequency >= cfr.getMinFrequency()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void pause() {
		mHandler.removeCallbacks(mRunnable);
	}

	@Override
	public void resume() {
	}

	@Override
	public void cancel() {
	}

}
