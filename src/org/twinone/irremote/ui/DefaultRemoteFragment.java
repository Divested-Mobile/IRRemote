package org.twinone.irremote.ui;

import org.twinone.irremote.R;
import org.twinone.irremote.TransmitOnTouchListener;
import org.twinone.irremote.ir.io.Transmitter;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class DefaultRemoteFragment extends BaseRemoteFragment implements
		Transmitter.OnTransmitListener {

	private TransmitOnTouchListener mOnTouchListener;
	private MenuItem mMenuIcon;
	private static final int MINIMUM_SHOW_TIME = 85; // ms
	private Runnable mHideFeedbackRunnable = new HideFeedbackRunnable();
	private Runnable mShowFeedbackRunnable = new ShowFeedbackRunnable();

	private boolean mVisualFeedback;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getTransmitter() == null) {
			return;
		}

		SharedPreferences sp = SettingsActivity.getPreferences(getActivity());
		boolean vibrate = sp.getBoolean(getString(R.string.pref_key_vibrate),
				getResources().getBoolean(R.bool.pref_def_vibrate));
		mVisualFeedback = sp.getBoolean(getString(R.string.pref_key_light),
				getResources().getBoolean(R.bool.pref_def_light));

		mOnTouchListener = new TransmitOnTouchListener(getTransmitter());
		mOnTouchListener.setHapticFeedbackEnabled(vibrate);

		if (getTransmitter() != null) {
			getTransmitter().setListener(this);
		}

	}

	@Override
	protected void setupButtons() {
		super.setupButtons();
		for (ButtonView bv : mButtons) {
			bv.setOnTouchListener(mOnTouchListener);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		mMenuIcon = menu.findItem(R.id.menu_transmit_feedback);

	}

	private class ShowFeedbackRunnable implements Runnable {
		@Override
		public void run() {
			if (mMenuIcon != null)
				mMenuIcon.setVisible(true);
		}
	}

	private class HideFeedbackRunnable implements Runnable {
		@Override
		public void run() {
			if (mMenuIcon != null)
				mMenuIcon.setVisible(false);
		}
	}

	@Override
	public void onBeforeTransmit() {
		if (mVisualFeedback) {
			if (!getTransmitter().isTransmitting()) {
				mHandler.removeCallbacks(mHideFeedbackRunnable);
				mHandler.removeCallbacks(mShowFeedbackRunnable);
				mHandler.post(mShowFeedbackRunnable);
			}
		}
	}

	@Override
	public void onAfterTransmit() {
		if (mVisualFeedback) {
			mHandler.postDelayed(mHideFeedbackRunnable, MINIMUM_SHOW_TIME);
		}
	}

}
