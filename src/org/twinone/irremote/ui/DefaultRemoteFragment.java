package org.twinone.irremote.ui;

import org.twinone.irremote.R;
import org.twinone.irremote.compat.CompatLogic;
import org.twinone.irremote.components.ComponentUtils;
import org.twinone.irremote.ir.Signal;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;

public class DefaultRemoteFragment extends BaseRemoteFragment {
	private boolean mFingerDown;
	private int mFingerDownId;

	private void onViewTreeReady() {
		new CompatLogic(getActivity())
				.updateRemotesToCoordinateSystem(getView());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		if (mRemote == null) {
			return new View(getActivity());
		}

		int resId = R.layout.fragment_remote_tv;
		try {
			resId = ComponentUtils.getLayout(mRemote.options.type);
		} catch (Exception e) {
			// Older versions don't have the options file in remote
		}

		View view = inflater.inflate(resId, container, false);

		SparseIntArray ids = mComponentUtils.getArray();
		for (int i = 0; i < ids.size(); i++) {
			final int id = ids.valueAt(i);
			if (id != 0) {
				Button b = (Button) view.findViewById(id);
				if (b != null) {
					mButtons.add((Button) view.findViewById(id));
				}
			}
		}

		setupButtons();
		view.getViewTreeObserver().addOnGlobalLayoutListener(
				mOnGlobalLayoutlistener);

		return view;

	}

	private OnGlobalLayoutListener mOnGlobalLayoutlistener = new OnGlobalLayoutListener() {

		@Override
		public void onGlobalLayout() {
			if (getView() != null && getView().getViewTreeObserver() != null)
				if (getView().getViewTreeObserver().isAlive()) {
					onViewTreeReady();
				}

		}
	};

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {

		case MotionEvent.ACTION_DOWN:

			if (!mFingerDown) {
				mFingerDown = true;
				mFingerDownId = event.getPointerId(0);
				final Signal s = mRemote.getButton(
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
