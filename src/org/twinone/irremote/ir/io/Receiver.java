package org.twinone.irremote.ir.io;

import org.twinone.irremote.ir.Signal;

import android.content.Context;
import android.util.Log;

public abstract class Receiver {

	private Context mContext;

	protected Context getContext() {
		return mContext;
	}

	protected Receiver(Context context) {
		mContext = context;
	}

	/**
	 * Returns the best available ir receiver
	 * 
	 * @return
	 */
	public static Receiver getInstance(Context context) {
		try {
			return new HTCReceiver(context);
		} catch (ComponentNotAvailableException e) {
		}

		Log.w("Receiver", "Could not instantiate HTCReceiver");
		return null;
	}

	/**
	 * @return True if the receiver is currently active
	 */
	public abstract boolean isReceiving();

	/**
	 * Learn an IR Code. When it's learned, the listener will be called
	 * 
	 * @param timeout
	 *            Seconds after which we give up trying to learn
	 */

	public abstract void learn(int timeoutSecs);

	public abstract void cancel();

	private OnLearnListener mListener;

	protected OnLearnListener getListener() {
		if (mListener == null) {
			throw new IllegalStateException(
					"You should specify a OnLearnListener for this receiver!");
		}
		return mListener;
	}

	public void setListener(OnLearnListener listener) {
		mListener = listener;
	}

	public interface OnLearnListener {
		public void onError(int errorCode);

		public void onCancel();

		public void onStart();

		public void onLearn(Signal s);
		
		public void onTimeout();
	}

	public abstract void start();

	public abstract void stop();
	
	public abstract boolean isAvailable();

}
