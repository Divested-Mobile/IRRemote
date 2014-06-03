package org.twinone.irremote.ir;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.hardware.ConsumerIrManager;
import android.hardware.ConsumerIrManager.CarrierFrequencyRange;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

public class Transmitter {

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

	public Transmitter(Context context) {
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
		if (mWindowManager == null) {
			mWindowManager = (WindowManager) mContext
					.getSystemService(Context.WINDOW_SERVICE);
		}
		mWindowManager.addView(mBlinker, mLayoutParams);

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
