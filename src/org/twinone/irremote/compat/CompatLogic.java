package org.twinone.irremote.compat;

import org.twinone.irremote.R;
import org.twinone.irremote.components.Button;
import org.twinone.irremote.components.Remote;

import android.content.Context;
import android.util.Log;
import android.view.View;

public class CompatLogic {

	private final Context mContext;

	private static final int GRID_SIZE_X = 16;// dp
	private static final int GRID_SIZE_Y = 16;// dp

	// all in px
	private int mButtonWidth;
	private int mButtonHeight;
	private int mSmallButtonHeight;
	private int mActivityMarginH;
	private int mActivityMarginV;
	private int mButtonMarginH;
	private int mButtonMarginV;
	private int mViewWidth;

	private int mGridSizeX;
	private int mGridSizeY;

	private Remote mRemote;

	private int mCols = 3;

	public void setCols(int cols) {
		mCols = cols;
	}

	public CompatLogic(Context c) {
		mContext = c;
	}

	private static final String TAG = "CompatLogic";

	private float pxToDp(float px) {
		return px / mContext.getResources().getDisplayMetrics().density;
	}

	private float dpToPx(Context c, float dp) {
		return dp * mContext.getResources().getDisplayMetrics().density;
	}

	// rootView to get W and H to work with (it's layout() method should be
	// called), use ViewTreeObserver
	public void updateRemotesToCoordinateSystem(View rootView) {

		// number of columns per row
		// available width in pixels we have to populate with buttons
		mViewWidth = rootView.getWidth();

		mGridSizeX = (int) dpToPx(mContext, GRID_SIZE_X);
		mGridSizeY = (int) dpToPx(mContext, GRID_SIZE_Y);

		mButtonMarginH = mContext.getResources().getDimensionPixelSize(
				R.dimen.remote_button_horizontal_margin);
		mButtonMarginV = mContext.getResources().getDimensionPixelSize(
				R.dimen.remote_button_vertical_margin);
		mActivityMarginH = mContext.getResources().getDimensionPixelOffset(
				R.dimen.activity_horizontal_margin);
		mActivityMarginV = mContext.getResources().getDimensionPixelOffset(
				R.dimen.activity_vertical_margin);

		mButtonHeight = mContext.getResources().getDimensionPixelSize(
				R.dimen.button_height);
		mSmallButtonHeight = mContext.getResources().getDimensionPixelSize(
				R.dimen.button_height_small);

		mButtonWidth = (mViewWidth - 2 * mActivityMarginH) / mCols - 2
				* mButtonMarginH;
		mButtonWidth = roundDown(mButtonWidth, mGridSizeX);
		// Reset activity margin
		// Log.d(TAG, "ViewWidth: " + mViewWidth);
		// Log.d(TAG, "Removing 3x buttonsize (" + mCols + "*" + mButtonWidth
		// + "): " + (mCols * mButtonWidth));
		// mViewWidth -= (mCols * mButtonWidth);
		// Log.d(TAG, "ViewWidth:" + mViewWidth);
		// Log.d(TAG, "Removing 2x margins: " + (mCols - 1 * mButtonMarginH));
		// mViewWidth -= mCols * mButtonWidth);
		mActivityMarginH = (mViewWidth - ((mCols - 1) * mButtonMarginH + mCols
				* mButtonWidth)) / 2;
		mActivityMarginH = round(mActivityMarginH, mGridSizeX);

		Log.d(TAG, "GridSize: " + mGridSizeX);
		Log.d(TAG, "Button width: " + mButtonWidth);
		Log.d(TAG, "LeftMargin: " + mActivityMarginH);

		for (String name : Remote.getNames(mContext)) {
			mRemote = Remote.load(mContext, name);
			setupButtons();
			mRemote.save(mContext);
		}
	}

	private int roundDown(float what, int to) {
		what = Math.round(what);
		return (int) (what - what % to);
	}

	private int round(float what, int to) {
		what = Math.round(what);
		final int mod = (int) what % to;
		if (mod >= to / 2) {
			what += to - mod;
		} else {
			what -= mod;
		}
		return (int) what;
	}

	int mTrackHeight;

	private void setupButtons() {
		mTrackHeight = mButtonMarginV * 4;
		addRow(false, Button.ID_POWER, 0, 0);
		addRow(false, Button.ID_CH_UP, Button.ID_NAV_UP, Button.ID_VOL_UP);
		addRow(false, Button.ID_NAV_LEFT, Button.ID_NAV_OK, Button.ID_NAV_RIGHT);
		addRow(false, Button.ID_CH_DOWN, Button.ID_NAV_DOWN, Button.ID_VOL_DOWN);
		//
		mTrackHeight += mButtonMarginV * 4;
		addRow(true, Button.ID_MUTE, Button.ID_CC, Button.ID_INPUT);
		addRow(true, Button.ID_INFO, Button.ID_CLEAR, Button.ID_EXIT);
		addRow(true, Button.ID_LAST, Button.ID_SMART, Button.ID_BACK);
		addRow(true, Button.ID_MENU, Button.ID_GUIDE, Button.ID_SLEEP);
		mTrackHeight += mButtonMarginV * 4;

		addRow(true, Button.ID_DIGIT_1, Button.ID_DIGIT_2, Button.ID_DIGIT_3);
		addRow(true, Button.ID_DIGIT_4, Button.ID_DIGIT_5, Button.ID_DIGIT_6);
		addRow(true, Button.ID_DIGIT_7, Button.ID_DIGIT_8, Button.ID_DIGIT_9);
		addRow(true, 0, Button.ID_DIGIT_0, 0);

		makeCircular(Button.ID_POWER, Button.ID_DIGIT_0, Button.ID_DIGIT_1,
				Button.ID_DIGIT_2, Button.ID_DIGIT_3, Button.ID_DIGIT_4,
				Button.ID_DIGIT_5, Button.ID_DIGIT_6, Button.ID_DIGIT_7,
				Button.ID_DIGIT_8, Button.ID_DIGIT_9);

		Button power = mRemote.getButton(Button.ID_POWER);
		if (power != null) {
			power.w = power.h;
		}

	}

	// TODO make this work
	private void makeCircular(int... ids) {
		for (int i = 0; i < ids.length; i++) {
			final Button b = mRemote.getButton(ids[i]);
			if (b != null)
				b.setCornerRadius(Integer.MAX_VALUE);
		}
	}

	private void addRow(boolean small, int... ids) {
		int height = small ? mSmallButtonHeight : mButtonHeight;
		for (int i = 0; i < ids.length; i++) {
			final Button b = mRemote.getButton(ids[i]);
			if (b != null) {
				b.x = mActivityMarginH + i
						* (mButtonWidth + mButtonMarginH * 2);
				b.y = mTrackHeight;
				b.h = height;
				b.w = mButtonWidth;
			}
		}
		mTrackHeight += height + mButtonMarginV * 2;
	}

}
