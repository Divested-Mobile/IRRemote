package org.twinone.irremote.ui;

import org.twinone.irremote.TransmitOnTouchListener;

import android.os.Bundle;

public class DefaultRemoteFragment extends BaseRemoteFragment {

	private TransmitOnTouchListener mOnTouchListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mOnTouchListener = new TransmitOnTouchListener(getTransmitter());
	}

	@Override
	protected void setupButtons() {
		super.setupButtons();
		for (ButtonView bv : mButtons) {
			bv.setOnTouchListener(mOnTouchListener);
		}
	}

}
