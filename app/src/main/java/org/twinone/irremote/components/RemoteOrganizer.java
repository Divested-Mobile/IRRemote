package org.twinone.irremote.components;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import org.twinone.irremote.R;

import java.util.ArrayList;

public class RemoteOrganizer {

    public static final int FLAG_ICON = 1 << 1;
    public static final int FLAG_COLOR = 1 << 2;
    public static final int FLAG_POSITION = 1 << 3;
    public static final int FLAG_CORNERS = 1 << 5;
    private static final int DEFAULT_FLAGS = FLAG_ICON | FLAG_POSITION
            | FLAG_COLOR | FLAG_CORNERS;
    private int mFlags = DEFAULT_FLAGS;
    private static final int FLAG_TEXT = 1;
    private static final int FLAG_TEXT_SIZE = 1 << 4;
    private static final int DEFAULT_CORNER_RADIUS = 16; // dp
    private final Context mContext;
    private int mVerticalMargin; // px
    private int mButtonSpacingX;
    private int mButtonSpacingY;
    // We can use width because we'll fill the whole screen's available width
//    private int mAvailableWidth;
    private int mBlockSizePxX; // pixels a block takes
    private int mBlockSizePxY;

    private int mButtonSizeInBlocksX;
    private int mButtonSizeInBlocksY;


    /**
     * List of buttons that are already organized
     */
    private final ArrayList<Button> mOrganizedButtons = new ArrayList<>();
    /**
     * Number of pixels we're away from the top
     */
    private int mTrackHeight;
    private int mCurrentRowCount;
    // all in px
    private int mHorizontalMargin; // px
    //    private int mAvailableBlocksX;
    private int mCols;
    private Remote mRemote;

    public RemoteOrganizer(Context c) {
        mContext = c;
        mHorizontalMargin = c.getResources().getDimensionPixelSize(
                R.dimen.grid_min_margin_x);
        mVerticalMargin = c.getResources().getDimensionPixelSize(
                R.dimen.grid_min_margin_y);

        mBlockSizePxX = c.getResources()
                .getDimensionPixelSize(R.dimen.block_size_x);
        mBlockSizePxY = c.getResources()
                .getDimensionPixelSize(R.dimen.block_size_y);
        mButtonSpacingX = c.getResources().getDimensionPixelSize(
                R.dimen.button_spacing_x);
        mButtonSpacingY = c.getResources().getDimensionPixelSize(
                R.dimen.button_spacing_y);

        mButtonSizeInBlocksX = c.getResources().getInteger(R.integer.blocks_per_button_x);
        mButtonSizeInBlocksY = c.getResources().getInteger(R.integer.blocks_per_button_y);
    }

    private int getScreenWidth() {
        WindowManager wm = (WindowManager) mContext
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    public void setHorizontalMarginDp(int marginDp) {
        mHorizontalMargin = (int) dpToPx(marginDp);
    }

    public void setVerticalMarginDp(int marginDp) {
        mVerticalMargin = (int) dpToPx(marginDp);
    }


    /**
     * You should probably use device size-based resources to call this method with.
     */
    public void setButtonSizeDp(int buttonSizeDp) {
        mBlockSizePxX = mBlockSizePxY = (int) dpToPx(buttonSizeDp);
    }

    public void setButtonSpacingDp(int buttonSizeDp) {
        mButtonSpacingX = mButtonSpacingY = (int) dpToPx(buttonSizeDp);
    }


    /**
     * Add default icons to this menu_main's buttons based on their ID's
     */
    public static void addIcons(Remote remote, boolean removeTextIfIconFound) {
        for (Button b : remote.buttons) {
            int icon = ComponentUtils.getIconIdForCommonButton(b.id);
            b.ic = icon;
            if (icon != 0 && removeTextIfIconFound)
                b.text = null;
        }
    }

    public void setFlags(int flags) {
        mFlags = flags;
    }

    /**
     * Removes a button from the menu_main and adds it to the organized buttons
     * list
     */
    private void moveToOrganizedList(Button... buttons) {
        for (Button b : buttons) {
            // b can be null but we don't want it in the menu_main
            if (b != null) {
                mOrganizedButtons.add(b);
                mRemote.removeButton(b);
            }
        }
    }

//    private void useCols(int cols) {
//        mHorizontalMargin = (mAvailableWidth - (mBlockSizePxX * cols  /* *mBlocksPerButtonX*/ - mButtonSpacingX)) / 2;
////        mAvailableBlocksX = cols * mBlocksPerButtonX;
//        mCols = cols;
//    }


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

    public void updateWithoutSaving(Remote remote) {
        if (remote == null) {
            return;
        }

        mRemote = remote;
        organize();
    }

    private void organize() {

        mTrackHeight = mVerticalMargin;
        setupSizes();
        if ((mFlags & FLAG_COLOR) != 0)
            setupColor();

        if ((mFlags & FLAG_CORNERS) != 0)
            setupCorners();

        if ((mFlags & FLAG_ICON) != 0)
            setupIcon();
        if ((mFlags & FLAG_TEXT) != 0)
            setupText();

        if ((mFlags & FLAG_TEXT_SIZE) != 0)
            setupTextSize();

        if ((mFlags & FLAG_POSITION) != 0)
            setupPosition();
    }

    private void setupLayout4ColsNew() {
        addRow(Button.ID_POWER, 0, Button.ID_NAV_UP, 0);
        addRow(Button.ID_VOL_UP, Button.ID_NAV_LEFT, Button.ID_NAV_OK,
                Button.ID_NAV_RIGHT);
        addRow(Button.ID_VOL_DOWN, 0, Button.ID_NAV_DOWN, 0);
        addRow(Button.ID_MUTE, Button.ID_DIGIT_1, Button.ID_DIGIT_2,
                Button.ID_DIGIT_3);
        addRow(Button.ID_CH_UP, Button.ID_DIGIT_4, Button.ID_DIGIT_5,
                Button.ID_DIGIT_6);
        addRow(Button.ID_CH_DOWN, Button.ID_DIGIT_7, Button.ID_DIGIT_8,
                Button.ID_DIGIT_9);
        addRow(Button.ID_MENU, 0, Button.ID_DIGIT_0, 0);
        addRow(Button.ID_RED, Button.ID_GREEN, Button.ID_BLUE, Button.ID_YELLOW);

        int type = mRemote.details.type;
        if (type == Remote.TYPE_CABLE || type == Remote.TYPE_BLU_RAY) {
            addRow(Button.ID_RWD, Button.ID_PLAY, Button.ID_FFWD, Button.ID_REC);
            addRow(Button.ID_PREV, Button.ID_PAUSE, Button.ID_NEXT,
                    Button.ID_STOP);
        }
        addUncommonRows();
    }

    /**
     * Set color by ID (NOT UID)
     */
    private void setColor(int buttonId, int background, int foreground) {
        Button b = findId(buttonId);
        if (b != null)
        {
            b.bg = background;
            b.fg = foreground;
        }
    }

    private void setColor(int buttonId, int background) {
        setColor(buttonId, background, Button.BG_WHITE);
    }

    private void setupSizes() {
        int def = (int) pxToDp(mContext.getResources().getDimensionPixelSize(
                R.dimen.default_text_size));
        for (Button b : mRemote.buttons) {
            setButtonSize(b, mButtonSizeInBlocksX, mButtonSizeInBlocksY /*mBlocksPerButtonX, mBlocksPerButtonY*/);
            b.setTextSize(def);
        }
    }

    private void setupPosition() {
//        useCols(4);
        mCols = 4;
        setupLayout4ColsNew();

        mRemote.buttons.addAll(mOrganizedButtons);

        mRemote.details.w = calculateWidthPx();
        mRemote.details.h = calculateHeightPx();
        mRemote.details.marginLeft = mHorizontalMargin;
        mRemote.details.marginTop = mVerticalMargin;
        Log.d("RemoteOrganizer", "Width : " + mRemote.details.w);
        Log.d("RemoteOrganizer", "Height: " + mRemote.details.h);
        Log.d("RemoteOrganizer", "Marg H: " + mRemote.details.marginLeft);
        Log.d("RemoteOrganizer", "Marg V: " + mRemote.details.marginTop);
    }

    private void setupIcon() {
        for (Button b : mRemote.buttons) {
            b.ic = ComponentUtils.getIconIdForCommonButton(b.id);
        }
    }

    private void setupText() {
        for (Button b : mRemote.buttons) {
            b.text = ComponentUtils.getCommonButtonDisplayName(b.id, mContext);
        }
    }

    private void setupTextSize() {
        int size = mContext.getResources().getDimensionPixelSize(
                R.dimen.default_text_size);
        size = (int) pxToDp(size);
        for (Button b : mRemote.buttons) {
            b.setTextSize(size);
        }
    }

    private void setupCorners() {
        for (Button b : mRemote.buttons) {
            setButtonCornerDp(b, 16);
        }
        setButtonCornerDp(findId(Button.ID_POWER), 400);
        setButtonCornerDp(findId(Button.ID_DIGIT_0), 400);
        setButtonCornerDp(findId(Button.ID_DIGIT_1), 400);
        setButtonCornerDp(findId(Button.ID_DIGIT_2), 400);
        setButtonCornerDp(findId(Button.ID_DIGIT_3), 400);
        setButtonCornerDp(findId(Button.ID_DIGIT_4), 400);
        setButtonCornerDp(findId(Button.ID_DIGIT_5), 400);
        setButtonCornerDp(findId(Button.ID_DIGIT_6), 400);
        setButtonCornerDp(findId(Button.ID_DIGIT_7), 400);
        setButtonCornerDp(findId(Button.ID_DIGIT_8), 400);
        setButtonCornerDp(findId(Button.ID_DIGIT_9), 400);

    }

    private void setupColor() {
        for (Button b : mRemote.buttons) {
            b.fg = Button.BG_WHITE;
            b.bg = Button.BG_BLUE;
        }

        int vols = Button.BG_ORANGE;
        int media = Button.BG_GREY;
        int power = Button.BG_RED;
        int nav = Button.BG_BLUE_GREY;
        int numbers = Button.BG_TEAL;
        int channels = Button.BG_GREY;

        setColor(Button.ID_VOL_UP, vols);
        setColor(Button.ID_VOL_DOWN, vols);
        setColor(Button.ID_MUTE, vols);

        setColor(Button.ID_CH_UP, channels);
        setColor(Button.ID_CH_DOWN, channels);
        setColor(Button.ID_MENU, channels);

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

        setColor(Button.ID_REC, media);
        setColor(Button.ID_STOP, media);
        setColor(Button.ID_PREV, media);
        setColor(Button.ID_NEXT, media);
        setColor(Button.ID_FFWD, media);
        setColor(Button.ID_RWD, media);
        setColor(Button.ID_PLAY, media);
        setColor(Button.ID_PAUSE, media);

        setColor(Button.ID_RED, Button.BG_GREY, Button.BG_RED);
        setColor(Button.ID_GREEN, Button.BG_GREY, Button.BG_GREEN);
        setColor(Button.ID_BLUE, Button.BG_GREY, Button.BG_BLUE);
        setColor(Button.ID_YELLOW, Button.BG_GREY, Button.BG_YELLOW);
    }

    private Button findId(int id) {
        return mRemote.getButtonById(id);
    }

    private int calculateWidthPx() {
        Log.d("RemoteOrganizer", "CalcWidthPx: marginH * 2 + 4*blockSizePx - spacing");
        Log.d("RemoteOrganizer", "CalcWidthPx: "+ mHorizontalMargin + " * 2 + 4 * " + mBlockSizePxX + " - " + mButtonSpacingX);
        return 2 * mHorizontalMargin + mCols * mBlockSizePxX - mButtonSpacingX;
    }

    private int calculateHeightPx() {
        int max = 0;
        for (Button b : mRemote.buttons) {
            if (b != null)
                max = Math.max(max, (int) (b.y + b.h));
        }
        return max + mVerticalMargin;
    }

    private int[] getRemainingIds() {
        int[] ids = new int[mRemote.buttons.size()];
        for (int i = 0; i < mRemote.buttons.size(); i++) {
            ids[i] = mRemote.buttons.get(i).id;
        }
        return ids;
    }

    private void addUncommonRows() {
        int[] ids = getRemainingIds();
        Log.d("Organizer", "Adding uncommon rows");

        for (int i = 0; i < ids.length; i += mCols) {
            int[] row = new int[mCols];
            for (int j = 0; j < mCols; j++) {
                if (i + j < ids.length)
                    row[j] = ids[i + j];
            }
            addUncommonRow(row);
        }
    }

    /**
     * Adds a row of 4 uncommon buttons
     */
    private void addUncommonRow(int... ids) {
        for (int i = 0; i < Math.min(ids.length, mCols); i++) {
            final Button b = findId(ids[i]);
            if (b != null) {
                setButtonPosition(b, i, mCurrentRowCount);
                moveToOrganizedList(b);
            }
        }
        mCurrentRowCount++;
    }

    /**
     * Adds a row of 4 buttons
     */
    private void addRow(int... ids) {
        for (int i = 0; i < Math.min(ids.length, mCols); i++) {
            if (ids[i] != 0) {
                final Button b = findId(ids[i]);
                if (b != null) {
                    setButtonPosition(b, i, mCurrentRowCount);
                    moveToOrganizedList(b);
                }
            }
        }
        mCurrentRowCount++;
    }


    public void setupNewButton(Button b) {
        b.w = getButtonWidthPixels();
        b.h = getButtonHeightPixels();
        b.setCornerRadius(dpToPx(DEFAULT_CORNER_RADIUS));
        b.bg = Button.BG_GREY;
    }

    int getButtonWidthPixels() {
        return getButtonWidthPixels(mButtonSizeInBlocksX);
    }

    int getButtonHeightPixels() {
        return getButtonHeightPixels(mButtonSizeInBlocksY);
    }

    private void setButtonSize(Button b, int w, int h) {
        if (b != null) {
            b.w = getButtonWidthPixels(w);
            b.h = getButtonHeightPixels(h);
        }
    }

    /**
     * Set the offset of the button plus an additional offset in button's size
     *
     * @param b The button
     * @param x The x in the button coordinate system
     * @param y The y in the button coordinate system
     */
    private void setButtonPosition(Button b, int x, int y) {
        if (b != null) {
            b.x = mHorizontalMargin + (x * mBlockSizePxX);
            b.y = mVerticalMargin + (y * mBlockSizePxY);
            Log.d("RemoteOrganizer", "Setting position ("+b.text + "): (" + b.x + "," + b.y + ")");
        }
    }

    /**
     * Get the width for the specified amount of blocks
     */
    private int getButtonWidthPixels(int blocks) {
        return blocks * mBlockSizePxX - mButtonSpacingX;
    }

    /**
     * Get the height for the specified amount of blocks
     */
    private int getButtonHeightPixels(int blocks) {
        return blocks * mBlockSizePxY - mButtonSpacingY;
    }

}
