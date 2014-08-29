package org.twinone.irremote.ui;

import org.twinone.irremote.ir.Signal;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class DefaultRemoteFragment extends BaseRemoteFragment {
	private boolean mFingerDown;
	private int mFingerDownId;

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {

		case MotionEvent.ACTION_DOWN:

			if (!mFingerDown) {
				mFingerDown = true;
				mFingerDownId = event.getPointerId(0);
				final Signal s = mRemote.getButton(true,
						mComponentUtils.getButtonId(v.getId())).getSignal();
				mTransmitter.setSignal(s);
				mHandler.postDelayed(new Runnable() {

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
