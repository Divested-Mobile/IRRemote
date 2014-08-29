package org.twinone.irremote.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

public class ButtonView extends Button {

	public ButtonView(Context context) {
		super(context);
	}

	public ButtonView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private org.twinone.irremote.components.Button mButton;

	public void setButton(org.twinone.irremote.components.Button button) {
		mButton = button;
	}

	public org.twinone.irremote.components.Button getButton() {
		return mButton;
	}

}
