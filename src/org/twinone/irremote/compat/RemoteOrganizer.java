package org.twinone.irremote.compat;

import org.twinone.irremote.components.Button;
import org.twinone.irremote.components.Remote;

import android.content.Context;
import android.graphics.Point;
import android.view.WindowManager;

public class RemoteOrganizer {

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

	public RemoteOrganizer(Context c) {
		mContext = c;
		mGridSizeX = (int) dpToPx(GRID_SIZE_X);
		mGridSizeY = (int) dpToPx(GRID_SIZE_Y);

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

		float available = (mDeviceWidth - mActivityMarginH * 2)
				+ mButtonSpacingH;
		available = roundDown(available / mCols, mGridSizeX);
		mButtonWidth = available - mButtonSpacingH;
	}

	public float getButtonWidth() {
		return mButtonWidth;
	}

	public float getButtonHeight() {
		return mButtonHeight;
	}

	public float getButtonHeightSmall() {
		return mButtonHeightSmall;
	}

	private float dpToPx(float dp) {
		return dp * mContext.getResources().getDisplayMetrics().density;
	}

	public void updateWithoutSaving(String name) {
		updateWithoutSaving(Remote.load(mContext, name));
	}

	// Base method
	public void updateWithoutSaving(Remote remote) {
		if (remote == null) {
			return;
		}
		mRemote = remote;
		setupButtons();
		mRemote.options.w = mDeviceWidth;
		mRemote.options.h = (int) mTrackHeight;
	}

	public void updateAndSave(String remoteName) {
		updateAndSave(Remote.load(mContext, remoteName));
	}

	public void updateAndSave(Remote remote) {
		updateWithoutSaving(remote);
		mRemote.save(mContext);
	}

	public void updateAndSaveAll() {
		for (String name : Remote.getNames(mContext)) {
			updateAndSave(name);
		}
	}

	private float roundDown(float what, int to) {
		return (what - what % to);
	}

	float mTrackHeight;

	private void setupButtons() {
		mTrackHeight = mActivityMarginV;
		addRowById(false, Button.ID_POWER, 0, 0);
		addRowById(false, Button.ID_CH_UP, Button.ID_NAV_UP, Button.ID_VOL_UP);
		addRowById(false, Button.ID_NAV_LEFT, Button.ID_NAV_OK,
				Button.ID_NAV_RIGHT);
		addRowById(false, Button.ID_CH_DOWN, Button.ID_NAV_DOWN,
				Button.ID_VOL_DOWN);
		//
		mTrackHeight += mGridSizeY;
		if (mRemote.options.type == Remote.TYPE_TV) {
			addRowById(true, Button.ID_MUTE, Button.ID_CC, Button.ID_INPUT);
			addRowById(true, Button.ID_INFO, Button.ID_CLEAR, Button.ID_EXIT);
			addRowById(true, Button.ID_LAST, Button.ID_SMART, Button.ID_BACK);
			addRowById(true, Button.ID_MENU, Button.ID_GUIDE, Button.ID_SLEEP);
		} else if (mRemote.options.type == Remote.TYPE_CABLE
				|| mRemote.options.type == Remote.TYPE_BLURAY) {
			addRowById(true, Button.ID_PREV, Button.ID_PLAY, Button.ID_NEXT);
			addRowById(true, Button.ID_RWD, Button.ID_PAUSE, Button.ID_FFWD);
			addRowById(true, Button.ID_MUTE, Button.ID_STOP, Button.ID_INFO);
			addRowById(true, Button.ID_MENU, Button.ID_REC, 0);
		}
		mTrackHeight += mGridSizeY;

		addRowById(true, Button.ID_DIGIT_1, Button.ID_DIGIT_2,
				Button.ID_DIGIT_3);
		addRowById(true, Button.ID_DIGIT_4, Button.ID_DIGIT_5,
				Button.ID_DIGIT_6);
		addRowById(true, Button.ID_DIGIT_7, Button.ID_DIGIT_8,
				Button.ID_DIGIT_9);
		addRowById(true, 0, Button.ID_DIGIT_0, 0);

		makeCircular(Button.ID_POWER, Button.ID_DIGIT_0, Button.ID_DIGIT_1,
				Button.ID_DIGIT_2, Button.ID_DIGIT_3, Button.ID_DIGIT_4,
				Button.ID_DIGIT_5, Button.ID_DIGIT_6, Button.ID_DIGIT_7,
				Button.ID_DIGIT_8, Button.ID_DIGIT_9);

		Button power = mRemote.getButtonById(Button.ID_POWER);
		if (power != null) {
			power.w = dpToPx(80 - BUTTON_SPACING_H);
		}

		int[] uids = new int[mCols];
		int i = 0;
		// Add uncommon buttons
		for (Button b : mRemote.buttons) {
			if (!b.isCommon()) {
				uids[i] = b.uid;
				i++;
				if (i == mCols) {
					addRow(true, uids);
					uids = new int[mCols];
					i = 0;
				}
			}
		}
		// Add non-full rows
		if (i != 0)
			addRow(true, uids);

	}

	// TODO make this work
	private void makeCircular(int... ids) {
		for (int i = 0; i < ids.length; i++) {
			final Button b = mRemote.getButtonById(ids[i]);
			if (b != null)
				b.setCornerRadius(Integer.MAX_VALUE);
		}
	}

	private void addRow(boolean small, int... uids) {
		float height = small ? mButtonHeightSmall : mButtonHeight;
		boolean hasButton = false;
		for (int i = 0; i < uids.length; i++) {
			final Button b = mRemote.getButton(uids[i]);
			if (b != null) {

				hasButton = true;
				b.x = mActivityMarginH + i * (mButtonWidth + mButtonSpacingH);
				b.y = mTrackHeight;
				b.h = height;
				b.w = mButtonWidth;
			}
		}
		if (hasButton)
			mTrackHeight += height + mButtonSpacingV;
	}

	private void addRowById(boolean small, int... ids) {
		float height = small ? mButtonHeightSmall : mButtonHeight;
		boolean hasButton = false;
		for (int i = 0; i < ids.length; i++) {
			final Button b = mRemote.getButtonById(ids[i]);
			if (b != null) {
				hasButton = true;
				b.x = mActivityMarginH + i * (mButtonWidth + mButtonSpacingH);
				b.y = mTrackHeight;
				b.h = height;
				b.w = mButtonWidth;
			}
		}
		if (hasButton)
			mTrackHeight += height + mButtonSpacingV;
	}

}
