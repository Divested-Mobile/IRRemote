package org.twinone.irremote.components;

import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.MotionEvent.PointerCoords;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;

import org.twinone.irremote.ir.Signal;
import org.twinone.irremote.ir.io.Transmitter;
import org.twinone.irremote.ui.ButtonView;

public class TransmitOnTouchListener implements OnTouchListener {

    private static final int DETECT_LONGPRESS_DELAY = 250; // ms
    private final Transmitter mTransmitter;
    private final PointerCoords mCoords = new PointerCoords();
    private final Runnable mDelayedRunnable = new MyDelayedRunnable();
    private boolean mFingerDown;
    private float mFingerDownX;
    private float mFingerDownY;
    private int mFingerDownId;
    private View mView;
    private boolean mHapticFeedbackEnabled;

    public TransmitOnTouchListener(Transmitter t) {
        if (t == null)
            throw new NullPointerException("Transmitter cannot be null");
        mTransmitter = t;
    }

    public boolean onTouch(final View v, MotionEvent event) {
        return v instanceof ButtonView && onTouch((ButtonView) v, event);
    }

    boolean onTouch(final ButtonView v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                if (!mFingerDown) {
                    mFingerDown = true;
                    mFingerDownX = event.getX();
                    mFingerDownY = event.getY();
                    mFingerDownId = event.getPointerId(0);

                    final Signal s = v.getButton().getSignal();
                    mTransmitter.setSignal(s);
                    mView = v;
                    v.removeCallbacks(mDelayedRunnable);
                    v.postDelayed(mDelayedRunnable, DETECT_LONGPRESS_DELAY);
                    return false;
                }

                break;

            case MotionEvent.ACTION_MOVE:

                event.getPointerCoords(0, mCoords);
                final int x = (int) mCoords.x;
                final int y = (int) mCoords.y;

                // Be lenient about moving outside of buttons
                int slop = ViewConfiguration.get(v.getContext())
                        .getScaledTouchSlop();
                if ((x < 0 - slop) || (x >= v.getWidth() + slop) || (y < 0 - slop)
                        || (y >= v.getHeight() + slop)) {
                    // Outside button
                    mTransmitter.stopTransmitting(false);
                    v.removeCallbacks(mDelayedRunnable);
                    if (v.isPressed()) {
                        v.setPressed(false);
                    }
                    mFingerDown = false;
                    return false;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:

                if (mFingerDown && mFingerDownId == event.getPointerId(0)) {
                    boolean atLeastOnce = event.getAction() == MotionEvent.ACTION_UP;
                    if (event.getX() == mFingerDownX
                            && event.getY() == mFingerDownY) {
                        atLeastOnce = true;
                    }
                    v.performClick();

                    final boolean once = atLeastOnce;
                    if (once && !mTransmitter.hasTransmittedOnce()) {
                        vibrateShort(v);
                    }
                    mTransmitter.stopTransmitting(once);
                    mFingerDown = false;
                }

                return false;
        }
        return true;
    }

    public boolean isHapticFeedbackEnabled() {
        return mHapticFeedbackEnabled;
    }

    public void setHapticFeedbackEnabled(boolean enabled) {
        mHapticFeedbackEnabled = enabled;
    }

    private void vibrateShort(View v) {
        if (mHapticFeedbackEnabled) {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        }
    }

    private class MyDelayedRunnable implements Runnable {

        @Override
        public void run() {
            if (mFingerDown) {
                mTransmitter.startTransmitting();
                vibrateShort(mView);
                // Don't scroll...
                mView.getParent().requestDisallowInterceptTouchEvent(true);
            }
        }
    }
}
