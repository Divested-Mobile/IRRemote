package org.twinone.irremote.ir.io;

import org.twinone.irremote.ir.Signal;

import android.content.Context;
import android.util.Log;

public abstract class Transmitter {
	private final Context mContext;

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
		} catch (ComponentNotAvailableException e) {
		}
		return null;
	}

	public static boolean isTransmitterAvailable(Context c) {
		return getInstance(c) != null;
	}

	/**
	 * Set the signal to be transmitted in the next {@link #transmit()} or
	 * {@link #startTransmitting()} call
	 */
	public abstract void setSignal(Signal signal);

	/**
	 * Transmit a {@link Signal} once
	 */
	public abstract void transmit();

	/**
	 * Start transmitting a signal repeatedly until
	 * {@link #stopTransmitting(boolean)} is called
	 * 
	 * @param s
	 */
	public abstract void startTransmitting();

	/**
	 * Convenience method for {@link #setSignal(Signal)} and
	 * {@link #startTransmitting()}
	 */
	public void startTransmitting(Signal signal) {
		setSignal(signal);
		startTransmitting();
	}

	/** Convenience method for {@link #setSignal(Signal)} and {@link #transmit()} */
	public void transmit(Signal signal) {
		setSignal(signal);
		transmit();
	}

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

	public abstract void pause();

	public abstract void resume();

	public abstract void cancel();

}
