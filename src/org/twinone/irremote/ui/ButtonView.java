package org.twinone.irremote.ui;

import org.twinone.irremote.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
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

		// setText(mButton.text);

		Drawable d = getResources().getDrawable(R.drawable.b_arrow_left);
		int size = (int) Math.min(mButton.w, mButton.h);
		size *= 0.7;
		d.setBounds(new Rect(0, 0, size, size));

		boolean text = false;
		if (text) {
			setCompoundDrawables(d, null, null, null);
		} else {
			mDrawable = d;
		}
	}

	private float dpToPx(float dp) {
		return dp * getContext().getResources().getDisplayMetrics().density;
	}

	private Drawable mDrawable;

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mDrawable != null) {
			canvas.save();
			final Rect bounds = mDrawable.getBounds();
			int w = (getWidth() - (bounds.right - bounds.left)) / 2;
			int h = (getHeight() - (bounds.bottom - bounds.top)) / 2;

			canvas.translate(w, h);
			mDrawable.draw(canvas);

			canvas.restore();
		}
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
}
