package org.twinone.irremote;

import org.twinone.irremote.ir.Signal;
import org.twinone.irremote.ir.io.Transmitter;
import org.twinone.irremote.ui.ButtonView;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class TransmitOnTouchListener implements OnTouchListener {

	private final Transmitter mTransmitter;

	protected static final int DETECT_LONGPRESS_DELAY = 250; // ms

	public TransmitOnTouchListener(Transmitter t) {
		if (t == null)
			throw new NullPointerException("Transmitter cannot be null");
		mTransmitter = t;
	}

	private boolean mFingerDown;
	private int mFingerDownId;

	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:

			if (!mFingerDown) {
				mFingerDown = true;
				mFingerDownId = event.getPointerId(0);

				final Signal s = ((ButtonView) v).getButton().getSignal();
				mTransmitter.setSignal(s);
				v.postDelayed(new Runnable() {

					@Override
					public void run() {
						if (mFingerDown) {
							mTransmitter.startTransmitting();
						}
					}
				}, DETECT_LONGPRESS_DELAY);
				return false;
			}

			break;

		case MotionEvent.ACTION_MOVE:
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			if (mFingerDown && event.getPointerId(0) == mFingerDownId) {
				boolean atLeastOnce = event.getAction() == MotionEvent.ACTION_UP;
				Log.d("", "Stopping transmission: AtLeastOnce: " + atLeastOnce);
				mTransmitter.stopTransmitting(atLeastOnce);
				mFingerDown = false;
				return false;
			}
			return false;
		}
		// Block multiple fingers from appearing as clicked
		return true;
	}
}
