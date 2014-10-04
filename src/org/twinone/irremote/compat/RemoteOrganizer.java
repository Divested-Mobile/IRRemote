package org.twinone.irremote.compat;

import java.util.ArrayList;

import org.twinone.irremote.R;
import org.twinone.irremote.components.Button;
import org.twinone.irremote.components.ComponentUtils;
import org.twinone.irremote.components.Remote;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.WindowManager;

public class RemoteOrganizer {

	private final Context mContext;

	private static final int DEFAULT_BUTTON_WIDTH = 5; // blocks
	private static final int DEFAULT_BUTTON_HEIGHT = 5; // blocks
	private static final int DEFAULT_CORNER_RADIUS = 16; // dp

	// all in px
	private int mMarginLeft; // px
	private int mMarginTop; // px
	private int mGridSpacingX;
	private int mGridSpacingY;
	// We can use width because we'll fill the whole screen's available width
	private int mDeviceWidth;
	private int mAvailableBlocksX;

	private int mGridSizeX;
	private int mGridSizeY;

	private int mBlocksPerButtonX;
	private int mBlocksPerButtonY;

	private int mCols;

	private Remote mRemote;
	/** List of buttons that are already organized */
	private ArrayList<Button> mOrganizedButtons = new ArrayList<Button>();

	/**
	 * Removes a button from the remote and adds it to the organized buttons
	 * list
	 */
	private void markAsOrganized(Button... buttons) {
		for (Button b : buttons) {
			// b can be null but we don't want it in the remote
			if (b != null) {
				mOrganizedButtons.add(b);
				mRemote.removeButton(b);
			}
		}
	}

	public RemoteOrganizer(Context c) {
		mContext = c;

		WindowManager wm = (WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE);
		Point p = new Point();
		wm.getDefaultDisplay().getSize(p);
		mDeviceWidth = p.x;
		mMarginLeft = c.getResources().getDimensionPixelSize(
				R.dimen.grid_min_margin_x);
		mMarginTop = c.getResources().getDimensionPixelSize(
				R.dimen.grid_min_margin_y);

		mGridSizeX = c.getResources()
				.getDimensionPixelSize(R.dimen.grid_size_x);
		mGridSizeY = c.getResources()
				.getDimensionPixelSize(R.dimen.grid_size_y);
		mGridSpacingX = c.getResources().getDimensionPixelSize(
				R.dimen.grid_spacing_x);
		mGridSpacingY = c.getResources().getDimensionPixelSize(
				R.dimen.grid_spacing_y);

		mBlocksPerButtonX = DEFAULT_BUTTON_WIDTH;
		mBlocksPerButtonY = DEFAULT_BUTTON_HEIGHT;

		int mAvailableScreenWidth = mDeviceWidth - mMarginLeft * 2
				+ mGridSpacingX;
		Log.d("RemoteOrganizer", "mAvailableScreenWidth: "
				+ pxToDp(mAvailableScreenWidth));

		mAvailableBlocksX = mAvailableScreenWidth / mGridSizeX;
		Log.d("RemoteOrganizer", "Av blockx: " + mAvailableBlocksX);

	}

	/**
	 * Set the margins according to how much block we're going to use
	 */
	private void adjustMarginToBlocks(int numBlocks) {
		mMarginLeft = (mDeviceWidth - (mGridSizeX * numBlocks - mGridSpacingX)) / 2;
		mAvailableBlocksX = numBlocks;
	}

	/**
	 * Set the offset of the button plus an additional offset in button's size
	 * 
	 * @param b
	 *            The button
	 * @param x
	 *            Offset in blocks from left
	 * @param y
	 *            Offset in blocks from top
	 * @param buttonX
	 *            Offset in button sizes from left
	 * @param buttonY
	 *            Offset in button sizes from right
	 */
	private void setButtonPosition(Button b, int x, int y, int buttonX,
			int buttonY) {
		if (b != null) {
			b.x = mMarginLeft + x * mGridSizeX
					+ (buttonX * mGridSizeX * mBlocksPerButtonX);
			b.y = mMarginTop + y * mGridSizeY
					+ (buttonY * mGridSizeY * mBlocksPerButtonY);
		}
	}

	private void setButtonPosition(Button b, int x, int y) {
		if (b != null) {
			b.x = mMarginLeft + x * mGridSizeX;
			b.y = mMarginTop + y * mGridSizeY;
		}
	}

	private void setButtonSize(Button b, int w, int h) {
		if (b != null) {
			b.w = w * mGridSizeX - mGridSpacingX;
			b.h = h * mGridSizeY - mGridSpacingY;
		}
	}

	private void setButtonCornerDp(Button b, int dp) {
		if (b != null) {
			b.setCornerRadius(dpToPx(dp));
		}
	}

	private float dpToPx(float dp) {
		return dp * mContext.getResources().getDisplayMetrics().density;
	}

	private float pxToDp(float px) {
		return px / mContext.getResources().getDisplayMetrics().density;
	}

	// Base method
	public void updateWithoutSaving(Remote remote) {
		if (remote == null) {
			return;
		}

		mRemote = remote;
		setupButtons();
	}

	/** Add default icons to this remote's buttons based on their ID's */
	public static void addIcons(Remote remote, boolean removeTextIfIconFound) {
		for (Button b : remote.buttons) {
			int icon = ComponentUtils.getIconIdForCommonButton(b.id);
			b.ic = icon;
			if (icon != 0 && removeTextIfIconFound)
				b.text = null;
		}
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

	/** Number of pixels we're away from the top */
	int mTrackHeight;

	private void setupButtons() {

		mTrackHeight = mMarginTop;
		setupSizes();
		setupCorners();
		setupColors();

		mCols = mAvailableBlocksX / mBlocksPerButtonX;
		mCols = 4;
		adjustMarginToBlocks(mCols * mBlocksPerButtonX);

		Log.w("RemoteOrganizer", "Custom columns set");
		Log.d("RemoteOrganizer", "Available: " + mCols + " cols");

		if (mCols >= 5) {
			setupLayout5Cols();
		} else if (mCols >= 4) {
			setupLayout4Cols();
		} else if (mCols >= 3) {
			setupLayout3Cols();
		}

		mRemote.buttons.addAll(mOrganizedButtons);

		mRemote.options.w = mDeviceWidth;
		mRemote.options.h = calculateHeightPx();
		mRemote.options.marginLeft = mMarginLeft;
		mRemote.options.marginTop = mMarginTop;
	}

	private void setupLayout3Cols() {

		addPowerButton(0, 0, 0, 0);
		int navHeight = addNavWidget(0, 0, 0, 0);
		addNumbersWidget(0, 1, 0, navHeight);

	}

	private void setupLayout4Cols() {

		int left = 0;
		int right = 0;
		left += addPowerButton(0, 0, 0, 0);
		right += addNavWidget(0, 0, 1, 0);
		right += addNumbersWidget(0, right, 1, 0);

		left += addColumns(0, 1 + left, 0, 0, 1, Button.ID_VOL_UP,
				Button.ID_VOL_DOWN, Button.ID_MUTE, Button.ID_CH_UP,
				Button.ID_CH_DOWN, Button.ID_MENU);

		addRemaining(0, 2 + Math.max(left, right), 0, 0, mCols);

	}

	private void setupLayout5Cols() {

		int grow = 2;
		enlargePower(grow);

		int left = 0;
		int right = 0;
		left += addPowerButton(-(mBlocksPerButtonX / 2 + grow / 2), 0 / 2, 1, 0);
		right += addNavWidget(0, 0, 2, 0);
		right += addNumbersWidget(0, 1 + right, 2, 0);

		left += addColumns(mBlocksPerButtonX / 2, 2 + left, 0, 0, 1,
				Button.ID_VOL_UP, Button.ID_VOL_DOWN, Button.ID_MUTE,
				Button.ID_CH_UP, Button.ID_CH_DOWN, Button.ID_MENU);

		addRemaining(0, Math.max(left + 3, right + 2), 0, 0, mCols);

	}

	private void enlargePower(int size) {
		final Button power = findId(Button.ID_POWER);
		if (power != null) {
			Log.d("RemoteOr", "Power != null");
			power.w += mGridSizeX * size;
			power.h += mGridSizeY * size;
		}
	}

	/**
	 * Set color by ID (NOT UID)
	 */
	private void setColor(int buttonId, int color) {
		Button b = findId(buttonId);
		if (b != null)
			b.bg = color;
	}

	private void setupSizes() {
		for (Button b : mRemote.buttons) {
			setButtonSize(b, mBlocksPerButtonX, mBlocksPerButtonY);
		}
	}

	private void setupCorners() {
		for (Button b : mRemote.buttons) {
			setButtonCornerDp(b, 16);
		}
		setButtonCornerDp(findId(Button.ID_POWER), 400);
	}

	private void setupColors() {
		int def = Button.BG_GREY;
		for (Button b : mRemote.buttons) {
			b.bg = def;
		}

		int vols = Button.BG_ORANGE;
		int power = Button.BG_RED;
		int nav = Button.BG_BLUE_GREY;
		int numbers = Button.BG_TEAL;

		setColor(Button.ID_VOL_UP, vols);
		setColor(Button.ID_VOL_DOWN, vols);
		setColor(Button.ID_MUTE, vols);

		setColor(Button.ID_POWER, power);
		setColor(Button.ID_NAV_DOWN, nav);
		setColor(Button.ID_NAV_UP, nav);
		setColor(Button.ID_NAV_LEFT, nav);
		setColor(Button.ID_NAV_RIGHT, nav);
		setColor(Button.ID_NAV_OK, nav);
		setColor(Button.ID_DIGIT_0, numbers);
		setColor(Button.ID_DIGIT_1, numbers);
		setColor(Button.ID_DIGIT_2, numbers);
		setColor(Button.ID_DIGIT_3, numbers);
		setColor(Button.ID_DIGIT_4, numbers);
		setColor(Button.ID_DIGIT_5, numbers);
		setColor(Button.ID_DIGIT_6, numbers);
		setColor(Button.ID_DIGIT_7, numbers);
		setColor(Button.ID_DIGIT_8, numbers);
		setColor(Button.ID_DIGIT_9, numbers);

	}

	private Button findId(int id) {
		return mRemote.getButtonById(id);
	}

	private int calculateHeightPx() {
		int max = 0;
		for (Button b : mRemote.buttons) {
			if (b != null)
				max = Math.max(max, (int) (b.y + b.h));
		}
		return max + mMarginTop;
	}

	private int addPowerButton(int x, int y, int buttonX, int buttonY) {
		x = buttonX * mBlocksPerButtonX + x;
		y = buttonY * mBlocksPerButtonY + y;
		final Button power = findId(Button.ID_POWER);
		setButtonPosition(power, x, y);
		markAsOrganized(power);
		return (int) power.h / mGridSizeY;
	}

	/**
	 * Adds a nav widget at the specified position
	 * 
	 * @return The height occupied by this widget
	 */
	private int addNavWidget(int x, int y, int buttonX, int buttonY) {
		return addColumns(x, y, buttonX, buttonY, 3, 0, Button.ID_NAV_UP, 0,
				Button.ID_NAV_LEFT, Button.ID_NAV_OK, Button.ID_NAV_RIGHT, 0,
				Button.ID_NAV_DOWN, 0);
	}

	private int addRemaining(int x, int y, int buttonX, int buttonY, int cols) {
		return addColumns(x, y, buttonX, buttonY, cols, getRemainingIds());
	}

	private int[] getRemainingIds() {
		int[] ids = new int[mRemote.buttons.size()];
		for (int i = 0; i < mRemote.buttons.size(); i++) {
			ids[i] = mRemote.buttons.get(i).id;
		}
		return ids;
	}

	private int addNumbersWidget(int x, int y, int buttonX, int buttonY) {
		return addColumns(x, y, buttonX, buttonY, 3, Button.ID_DIGIT_1,
				Button.ID_DIGIT_2, Button.ID_DIGIT_3, Button.ID_DIGIT_4,
				Button.ID_DIGIT_5, Button.ID_DIGIT_6, Button.ID_DIGIT_7,
				Button.ID_DIGIT_8, Button.ID_DIGIT_9, 0, Button.ID_DIGIT_0, 0);
	}

	private int addColumns(int x, int y, int buttonX, int buttonY, int cols,
			int... ids) {
		x = buttonX * mBlocksPerButtonX + x;
		y = buttonY * mBlocksPerButtonY + y;
		for (int i = 0; i < ids.length; i++) {
			final Button b = findId(ids[i]);
			if (b != null) {
				setButtonPosition(b, x, y, i % cols, i / cols);
				markAsOrganized(b);
			}
		}
		return (ids.length / cols + ids.length % cols) * mBlocksPerButtonY;
	}

	public void setupNewButton(Button b) {
		b.w = getButtonWidth();
		b.h = getButtonHeight();
		b.setCornerRadius(dpToPx(DEFAULT_CORNER_RADIUS));
		b.bg = Button.BG_GREY;
	}

	public int getButtonWidth() {
		return getButtonWidth(DEFAULT_BUTTON_WIDTH);
	}

	public int getButtonHeight() {
		return getButtonHeight(DEFAULT_BUTTON_HEIGHT);
	}

	/**
	 * Get the width for the specified amount of blocks
	 */
	private int getButtonWidth(int blocks) {
		return blocks * mGridSizeX - mGridSpacingX;
	}

	/**
	 * Get the height for the specified amount of blocks
	 */
	private int getButtonHeight(int blocks) {
		return blocks * mGridSizeY - mGridSpacingY;
	}

}
