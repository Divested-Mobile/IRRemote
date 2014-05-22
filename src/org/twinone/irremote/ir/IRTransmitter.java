/*
 * Copyright 2014 Luuk Willemsen (Twinone)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.twinone.irremote.ir;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.hardware.ConsumerIrManager;
import android.hardware.ConsumerIrManager.CarrierFrequencyRange;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

public class IRTransmitter {

	// private static final String TAG = "IRManager";

	private Context mContext;
	private ConsumerIrManager mIrManager;

	private boolean mShowBlinker;

	/** Sets whether the top red view should be shown */
	public void setShowBlinker(boolean showBlinker) {
		mShowBlinker = showBlinker;
	}

	public boolean getShowFeedbackView() {
		return mShowBlinker;
	}

	public IRTransmitter(Context context) {
		mContext = context;
		mIrManager = (ConsumerIrManager) mContext
				.getSystemService(Context.CONSUMER_IR_SERVICE);
		inflateView();
	}

	public boolean hasIrEmitter() {
		return mIrManager.hasIrEmitter();
	}

	/**
	 * 
	 * @return true if the signal has been transmitted
	 */
	public boolean transmit(final Signal signal) {
		if (!isFrequencySupported(signal.frequency))
			return false;
		showView();
		mIrManager.transmit(signal.frequency, signal.pattern);
		return true;
	}

	private boolean isBlinkerShown;
	private View mBlinker;
	private LayoutParams mLayoutParams;
	private WindowManager mWindowManager;

	private void inflateView() {
		mBlinker = new View(mContext);
		mBlinker.setBackgroundColor(Color.parseColor("#ff0000"));
		mLayoutParams = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
				WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
				PixelFormat.TRANSLUCENT);
		mLayoutParams.height = dpToPx(3);
		mLayoutParams.gravity = Gravity.TOP;
		mLayoutParams.y = dpToPx(-25);
	}

	int dpToPx(int dp) {
		return (int) (mContext.getResources().getDisplayMetrics().density * dp + 0.5f);
	}

	public void blink() {
		showView();
	}

	/**
	 * Pause this transmitter. If a blink is showing it will hide it
	 */
	public void pause() {
		hideView();
	}

	/**
	 * Shows a little red view on the top of the screen, this way, the user
	 * knows that a signal is being transmitted
	 */
	private void showView() {
		if (isBlinkerShown || !mShowBlinker)
			return;
		long start = System.currentTimeMillis();
		Log.d("", "Show at " + start);
		if (mWindowManager == null) {
			mWindowManager = (WindowManager) mContext
					.getSystemService(Context.WINDOW_SERVICE);
		}
		mWindowManager.addView(mBlinker, mLayoutParams);
		long end = System.currentTimeMillis();
		Log.d("", "Took " + (end - start) + "ms");

		isBlinkerShown = true;
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				hideView();
			}
		}, 300);

	}

	private void hideView() {
		if (isBlinkerShown)
			mWindowManager.removeView(mBlinker);
		isBlinkerShown = false;
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
}
