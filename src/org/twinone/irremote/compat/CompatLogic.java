package org.twinone.irremote.compat;

import org.twinone.irremote.components.Button;
import org.twinone.irremote.components.Remote;

import android.content.Context;
import android.graphics.Point;
import android.view.WindowManager;

public class CompatLogic {

	private final Context mContext;

	private static final int GRID_SIZE_X = 16;// dp
	private static final int GRID_SIZE_Y = 16;// dp

	private static final int BUTTON_SPACING_H = 8;// dp
	private static final int BUTTON_SPACING_V = 8;// dp

	private static final int ACTIVITY_MARGIN_H = 16; // dp
	private static final int ACTIVITY_MARGIN_V = 16;// dp

	private static final int BUTTON_HEIGHT = 64 - BUTTON_SPACING_V;// dp
	private static final int BUTTON_HEIGHT_SMALL = 48 - BUTTON_SPACING_V;// dp

	// all in px
	private float mButtonWidth;
	private float mButtonHeight;
	private float mButtonHeightSmall;
	private float mActivityMarginH;
	private float mActivityMarginV;
	private float mButtonSpacingH;
	private float mButtonSpacingV;
	private int mDeviceWidth;

	private int mGridSizeX;
	private int mGridSizeY;

	private Remote mRemote;

	private int mCols = 3;

	public void setCols(int cols) {
		mCols = cols;
	}

	public CompatLogic(Context c) {
		mContext = c;
		mGridSizeX = (int) dpToPx(GRID_SIZE_X);
		mGridSizeY = (int) dpToPx(GRID_SIZE_Y);
	}

	private static final String TAG = "CompatLogic";

	private float pxToDp(float px) {
		return px / mContext.getResources().getDisplayMetrics().density;
	}

	private float dpToPx(float dp) {
		return dp * mContext.getResources().getDisplayMetrics().density;
	}

	public void updateRemotesToCoordinateSystem() {

		WindowManager wm = (WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE);
		Point p = new Point();
		wm.getDefaultDisplay().getSize(p);
		mDeviceWidth = p.x;

		mActivityMarginH = dpToPx(ACTIVITY_MARGIN_H);
		mActivityMarginV = dpToPx(ACTIVITY_MARGIN_V);

		mButtonSpacingH = dpToPx(BUTTON_SPACING_H);
		mButtonSpacingV = dpToPx(BUTTON_SPACING_V);

		mButtonHeight = dpToPx(BUTTON_HEIGHT);
		mButtonHeightSmall = dpToPx(BUTTON_HEIGHT_SMALL);

		// What i need to fill with mCols*button + (mCols-1)*margin
		// Also need to align to the grid

		// float available = round((mViewWidth - mActivityMarginH * 2),
		// mGridSizeX) / mCols;
		// available = roundDown(available, mGridSizeX);
		// mButtonWidth = available - mButtonSpacingH;

		float available = (mDeviceWidth - mActivityMarginH * 2)
				+ mButtonSpacingH;
		available = roundDown(available / mCols, mGridSizeX);
		mButtonWidth = available - mButtonSpacingH;

		// boolean passed = false;
		// if (mButtonWidth + mButtonSpacingH % mGridSizeX == 0) {
		// passed = true;
		// }
		// Log.d(TAG, "Check passed: " + passed);
		for (String name : Remote.getNames(mContext)) {
			mRemote = Remote.load(mContext, name);
			setupButtons();
			mRemote.save(mContext);
		}
	}

	private float roundDown(float what, int to) {
		return (what - what % to);
	}

	private float round(float what, int to) {
		final int mod = (int) what % to;
		if (mod >= to / 2) {
			what += to - mod;
		} else {
			what -= mod;
		}
		return what;
	}

	float mTrackHeight;

	private void setupButtons() {
		mTrackHeight = mActivityMarginV;
		addRow(false, Button.ID_POWER, 0, 0);
		addRow(false, Button.ID_CH_UP, Button.ID_NAV_UP, Button.ID_VOL_UP);
		addRow(false, Button.ID_NAV_LEFT, Button.ID_NAV_OK, Button.ID_NAV_RIGHT);
		addRow(false, Button.ID_CH_DOWN, Button.ID_NAV_DOWN, Button.ID_VOL_DOWN);
		//
		mTrackHeight += mGridSizeY;
		addRow(true, Button.ID_MUTE, Button.ID_CC, Button.ID_INPUT);
		addRow(true, Button.ID_INFO, Button.ID_CLEAR, Button.ID_EXIT);
		addRow(true, Button.ID_LAST, Button.ID_SMART, Button.ID_BACK);
		addRow(true, Button.ID_MENU, Button.ID_GUIDE, Button.ID_SLEEP);
		mTrackHeight += mGridSizeY;

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
			power.w = dpToPx(128 - BUTTON_SPACING_H);
		}

		int[] ids = new int[mCols];
		int i = 0;
		// Add uncommon buttons
		for (Button b : mRemote.buttons) {
			if (!b.isCommon()) {
				ids[i] = b.id;
				i++;
				if (i == mCols) {
					addRow(true, ids);
					i = 0;
				}
			}
		}
		// Add non-full rows
		if (i != 0)
			addRow(true, ids);

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
		float height = small ? mButtonHeightSmall : mButtonHeight;
		for (int i = 0; i < ids.length; i++) {
			final Button b = mRemote.getButton(ids[i]);
			if (b != null) {
				b.x = mActivityMarginH + i * (mButtonWidth + mButtonSpacingH);
				b.y = mTrackHeight;
				b.h = height;
				b.w = mButtonWidth;
			}
		}
		mTrackHeight += height + mButtonSpacingV;
	}

}
