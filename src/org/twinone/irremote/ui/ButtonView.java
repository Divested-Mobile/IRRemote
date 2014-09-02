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
		setText(mButton.text);
	}

	public org.twinone.irremote.components.Button getButton() {
		return mButton;
	}

	@Override
	public void setX(float x) {
		getButton().x = x;
		super.setX(x);
	}

	@Override
	public void setY(float y) {
		getButton().y = y;
		super.setY(y);
	}

}
