package org.twinone.irremote.ir;

import android.content.Context;
import android.hardware.ConsumerIrManager;
import android.hardware.ConsumerIrManager.CarrierFrequencyRange;
import android.os.Handler;
import android.util.Log;

public class Transmitter {

	// private static final String TAG = "IRManager";

	private Context mContext;

	private ConsumerIrManager mIrManager;

	public Transmitter(Context context) {
		mContext = context;
		mIrManager = (ConsumerIrManager) context
				.getSystemService(Context.CONSUMER_IR_SERVICE);
		mHandler = new Handler();
	}

	public boolean hasIrEmitter() {
		return mIrManager.hasIrEmitter();
	}

	public void transmit(final Signal signal) {
		if (!isFrequencySupported(signal.frequency))
			return;
		transmitImpl(signal);
	}

	public void startTransmitting(Signal signal) {
		if (!isFrequencySupported(signal.frequency))
			return;
		if (mTransmitting)
			return;
		mTransmitting = true;
		mTSignal = signal;
		mRunnable = new TransmitterRunnable();
		mHandler.postDelayed(mRunnable, mOffsetMillis);
	}

	private Runnable mRunnable;

	/**
	 * Time between the end of a transmission and the start of the next one
	 */
	private int mPeriodMillis = 200;
	/**
	 * If the user doesn't cancel in this time, we'll start transmitting
	 */
	private int mOffsetMillis = 100;

	/**
	 * Set how much each transmission should be away from another
	 * 
	 * @param millis
	 */
	public void setPeriodMillis(int millis) {
		mPeriodMillis = millis;
	}

	public void setOffsetMillis(int millis) {
		mOffsetMillis = millis;
	}

	private Handler mHandler;
	private volatile boolean mTransmitting;
	private Signal mTSignal;
	private volatile boolean mHasTransmittedOnce;

	private class TransmitterRunnable implements Runnable {
		@Override
		public void run() {
			transmitImpl(mTSignal);
			mHasTransmittedOnce = true;
			if (mTransmitting) {
				mHandler.postDelayed(this, mPeriodMillis);
			}
		}
	}

	private synchronized void transmitImpl(Signal s) {
		 s.fix(mContext);
		Log.d("", "hasIrEmitter: " + mIrManager.hasIrEmitter());

		if (mListener != null) {
			mListener.onBeforeTransmit();
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.pattern.length; i++) {
			sb.append(s.pattern[i]).append(" ");
		}
		Log.d("", "Sending: " + sb.toString());

		mIrManager.transmit(s.frequency, s.pattern);
		if (mListener != null) {
			mListener.onAfterTransmit();
		}
	}

	/**
	 * 
	 * @param transmitAtLeastOnce
	 *            If this is set to true, a
	 */
	public void stopTransmitting(boolean transmitAtLeastOnce) {
		// Ensure the signal is transmitted at least one time
		mTransmitting = false;
		mHandler.removeCallbacks(mRunnable);
		mHasTransmittedOnce = false;

		if (transmitAtLeastOnce && !mHasTransmittedOnce) {
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					transmitImpl(mTSignal);
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

	public void setListener(OnTransmitListener listener) {
		mListener = listener;
	}

	private OnTransmitListener mListener;

	/**
	 * Interface to implement to know when a IR signal is transmitted
	 * 
	 */
	public interface OnTransmitListener {
		public void onBeforeTransmit();

		public void onAfterTransmit();
	}
}
