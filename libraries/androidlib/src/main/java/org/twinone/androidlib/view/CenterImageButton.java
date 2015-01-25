package org.twinone.androidlib.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * This class allows an image to be drawn in the center of a button, while
 * retaining background and text functionality
 *
 * @author twinone
 */
public class CenterImageButton extends Button {

    private Drawable mCenterDrawable;

    public CenterImageButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CenterImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CenterImageButton(Context context) {
        super(context);
    }

    protected void setCompoundDrawableCenter(Drawable d) {
        mCenterDrawable = d;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mCenterDrawable != null) {
            canvas.save();
            final Rect bounds = mCenterDrawable.getBounds();
            int w = (getWidth() - (bounds.right - bounds.left)) / 2;
            int h = (getHeight() - (bounds.bottom - bounds.top)) / 2;

            canvas.translate(w, h);
            mCenterDrawable.draw(canvas);
            canvas.restore();
        }
    }

}
