package org.twinone.irremote.ir.io;

import org.twinone.irremote.Constants;
import org.twinone.irremote.R;
import org.twinone.irremote.debug.DebugTransmitter;
import org.twinone.irremote.ir.Signal;
import org.twinone.irremote.ui.SettingsActivity;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public abstract class Transmitter {
	private final Context mContext;

	protected Context getContext() {
		return mContext;
	}

	private LooperThread mThread;
	protected Handler mHandler;

	private class LooperThread extends Thread {

		public void run() {
			Looper.prepare();

			mHandler = new Handler(new Handler.Callback() {

				@Override
				public boolean handleMessage(Message msg) {

					return false;
				}
			});

			Looper.loop();
		}
	}

	protected Transmitter(Context context) {
		mContext = context;

		mThread = new LooperThread();
		mThread.start();
		setPeriodMillisFromPrefs();
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
		Log.w("Receiver", "Could not instantiate KitKatTransmitter");
		
		if (Constants.USE_DEBUG_TRANSMITTER) {
			return new DebugTransmitter(c);
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

	public abstract boolean hasTransmittedOnce();

	public void setListener(OnTransmitListener listener) {
		mListener = listener;
	}

	protected OnTransmitListener getListener() {
		return mListener;
	}

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

	protected int getPeriodMillis() {
		return mPeriodMillis;
	}

	/**
	 * @return The milliseconds to wait between transmissions that the user has
	 *         saved
	 */
	public void setPeriodMillisFromPrefs() {
		int def = mContext.getResources().getInteger(R.integer.pref_def_delay);
		int millis = SettingsActivity.getPreferences(mContext).getInt(
				mContext.getString(R.string.pref_key_delay), def);
		setPeriodMillis(millis);
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

	private boolean mTransmitting;

	public boolean isTransmitting() {
		return mTransmitting;
	}

	protected void setTransmitting(boolean transmitting) {
		mTransmitting = transmitting;
	}

}
