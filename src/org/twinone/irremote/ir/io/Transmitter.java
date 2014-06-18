package org.twinone.irremote.ir.io;

import org.twinone.irremote.ir.Signal;

import android.content.Context;

public abstract class Transmitter {
	private Context mContext;

	protected Context getContext() {
		return mContext;
	}

	protected Transmitter(Context context) {
		mContext = context;
	}

	/**
	 * Returns the best available ir transmitter
	 * 
	 * @return
	 */
	public static Transmitter getInstance(Context c) {
		try {
			return new KitKatTransmitter(c);
		} catch (RuntimeException e) {
		}
		return null;
	}

	/**
	 * Transmit a {@link Signal} once
	 */
	public abstract void transmit(Signal signal);

	/**
	 * Start transmitting a signal repeatedly until
	 * {@link #stopTransmitting(boolean)} is called
	 * 
	 * @param s
	 */
	public abstract void startTransmitting(Signal s);

	/**
	 * Stop transmitting a repeating signal started by
	 * {@link #startTransmitting(Signal)}
	 * 
	 * @param atLeastOnce
	 *            True if a signal has to be sent at least once
	 */
	public abstract void stopTransmitting(boolean atLeastOnce);

	public void setListener(OnTransmitListener listener) {
		mListener = listener;
	}

	protected OnTransmitListener getListener() {
		return mListener;
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
