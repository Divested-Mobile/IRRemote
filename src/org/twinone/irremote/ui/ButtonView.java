package org.twinone.irremote.ui;

import org.twinone.androidlib.view.CenterImageButton;
import org.twinone.irremote.R;
import org.twinone.irremote.components.ComponentUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.StateSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewOutlineProvider;

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
		if (mButton.ic == 0)
			setText(mButton.text);
		setPadding(0, 0, 0, 0);
		updateIcon();
		setId(mButton.uid);
		updateBackground();

		setX(mButton.x);
		setY(mButton.y);

		setTextSize(TypedValue.COMPLEX_UNIT_DIP, mButton.getTextSize());
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
			// if (mButton.hasText()) {
			// // Text adapts to size of button
			// if (mButton.w >= mButton.h)
			// setCompoundDrawables(d, null, null, null);
			// else
			// setCompoundDrawables(null, d, null, null);
			// } else {
			setCompoundDrawableCenter(d);
			// }

		} catch (Exception e) {
			Log.w("ButtonView", "Exception loading drawable: ", e);
		}
	}

	public void setBackground(int bg) {
		mButton.bg = bg;
		updateBackground();
	}

	@SuppressLint("NewApi")
	private void updateBackground() {
		if (mButton.bg == 0)
			return;
		final StateListDrawable dd = new StateListDrawable();
		final GradientDrawable pressed = (GradientDrawable) ComponentUtils
				.getGradientDrawable(getContext(), mButton.bg, true).mutate();

		pressed.setCornerRadii(mButton.getCornerRadii());
		dd.addState(new int[] { android.R.attr.state_pressed }, pressed);
		final GradientDrawable def = (GradientDrawable) ComponentUtils
				.getGradientDrawable(getContext(), mButton.bg, false).mutate();
		def.setCornerRadii(mButton.getCornerRadii());
		dd.addState(StateSet.WILD_CARD, def);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			ColorStateList cls = new ColorStateList(
					new int[][] { new int[] { android.R.attr.state_pressed } // pressed
					}, new int[] { getResources().getColor(
							R.color.ripple_material_dark) });
			RippleDrawable rd = new RippleDrawable(cls, def, null);
			setOutlineProvider(new ViewOutlineProvider() {

				@Override
				public void getOutline(View view, Outline outline) {
					outline.setRoundRect(0, 0, (int) mButton.w,
							(int) mButton.h,
							mButton.rtl > mButton.h / 2 ? mButton.h / 2
									: mButton.rtl);
				}
			});
			setClipToOutline(true);
			setBackground(rd);
		} else {
			setBackground(dd);
		}
	}

	private float dpToPx(float dp) {
		return dp * getContext().getResources().getDisplayMetrics().density;
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
		if (applyToButton)
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
