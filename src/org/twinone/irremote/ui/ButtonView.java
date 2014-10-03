package org.twinone.irremote.ui;

import org.twinone.androidlib.view.CenterImageButton;
import org.twinone.irremote.components.ComponentUtils;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.StateSet;

public class ButtonView extends CenterImageButton {

	public ButtonView(Context context) {
		super(context);
	}

	public ButtonView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private org.twinone.irremote.components.Button mButton;

	public void setButton(org.twinone.irremote.components.Button button) {
		setHapticFeedbackEnabled(true);
		mButton = button;
		setText(mButton.text);
		setId(mButton.uid);
		updateIcon();
		updateBackground();
	}

	/**
	 * Set the icon drawable for this button<br>
	 * If the button has text, it will snap to the left, if not it will show in
	 * the center
	 */
	public void setIcon(int iconId) {
		mButton.ic = iconId;
		updateIcon();
		setBackground(mButton.bg);
	}

	private void updateIcon() {
		int iconResId = ComponentUtils.getIconResId(mButton.ic);
		if (iconResId == 0) {
			setCompoundDrawableCenter(null);
			return;
		}
		try {
			Drawable d = getResources().getDrawable(iconResId);
			int size = (int) Math.min(mButton.w, mButton.h);
			size *= 0.6;
			d.setBounds(new Rect(0, 0, size, size));
			if (mButton.hasText()) {
				setCompoundDrawables(d, null, null, null);
			} else {
				setCompoundDrawableCenter(d);
			}

		} catch (Exception e) {
			Log.w("ButtonView", "Exception loading drawable: ", e);
		}
	}

	public void setBackground(int bg) {
		mButton.bg = bg;
		updateBackground();
	}

	private void updateBackground() {
		// if (mButton.bg == 0)
		// return;
		final StateListDrawable dd = new StateListDrawable();
		final GradientDrawable pressed = (GradientDrawable) ComponentUtils
				.getGradientDrawable(getContext(), mButton.bg, true).mutate();

		pressed.setCornerRadii(mButton.getCornerRadii());
		dd.addState(new int[] { android.R.attr.state_pressed }, pressed);
		final GradientDrawable def = (GradientDrawable) ComponentUtils
				.getGradientDrawable(getContext(), mButton.bg, false).mutate();
		def.setCornerRadii(mButton.getCornerRadii());
		dd.addState(StateSet.WILD_CARD, def);
		setBackground(dd);
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

	@Override
	public void setWidth(int pixels) {
		getButton().w = pixels;
		setRight(getLeft() + pixels);
		super.setWidth(pixels);
	}

	@Override
	public void setHeight(int pixels) {
		getButton().h = pixels;
		setBottom(getTop() + pixels);
		super.setHeight(pixels);
	}

	/**
	 * Set the text of the view and update the internal button's text field
	 * 
	 * @param text
	 * @param applyToButton
	 */
	public void setText(CharSequence text, boolean applyToButton) {
		setText(text);
		mButton.text = (String) text;
	}

	private boolean mPressLock;

	@Override
	public void setPressed(boolean pressed) {
		if (!mPressLock) {
			super.setPressed(pressed);
		}
	}

	/**
	 * Lock or unlock pressed state<br>
	 * When press is locked you can only change the pressed value unlocking it
	 * first (this is useful to prevent background changes handled by the system
	 * on clicks)
	 * 
	 */
	public void setPressLock(boolean lock) {
		mPressLock = lock;
	}

	public void setPressedIgnoringLock(boolean pressed) {
		Log.d("TAG", "Setting Pressed Ingnoring: " + pressed);
		super.setPressed(pressed);
	}

	public boolean getPressLock() {
		return mPressLock;
	}

}
