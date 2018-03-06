package org.twinone.irremote.ir.io;

import android.content.Context;
import android.util.Log;

import org.twinone.irremote.Constants;
import org.twinone.irremote.debug.DebugReceiver;
import org.twinone.irremote.ir.Signal;

public abstract class Receiver {

    private final Context mContext;
    private OnLearnListener mListener;

    protected Receiver(Context context) {
        mContext = context;
    }

    /**
     * Returns the best available ir receiver
     *
     * @return
     */
    public static Receiver getInstance(Context context) {;
        if (Constants.USE_DEBUG_RECEIVER) {
            return new DebugReceiver(context);
        }
        return null;
    }

    public static boolean isAvailable(Context c) {
        return getInstance(c) != null;
    }

    protected Context getContext() {
        return mContext;
    }

    /**
     * @return True if the receiver is currently active
     */
    public abstract boolean isReceiving();

    /**
     * Learn an IR Code. When it's learned, the listener will be called
     *
     * @param timeoutSecs Seconds after which we give up trying to learn
     */

    public abstract void learn(int timeoutSecs);

    public abstract void cancel();

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

    public abstract void start();

    public abstract void stop();

    public abstract boolean isAvailable();

    public interface OnLearnListener {
        public void onError(int errorCode);

        public void onLearnCancel();

        public void onLearnStart();

        public void onLearn(Signal s);

        public void onTimeout();
    }

}
