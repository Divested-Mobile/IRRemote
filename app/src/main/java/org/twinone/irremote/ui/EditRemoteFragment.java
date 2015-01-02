package org.twinone.irremote.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.twinone.irremote.R;
import org.twinone.irremote.compat.ToolbarActivity;
import org.twinone.irremote.components.AnimHelper;
import org.twinone.irremote.components.Button;
import org.twinone.irremote.components.Remote;
import org.twinone.irremote.components.RemoteOrganizer;
import org.twinone.irremote.providers.ProviderActivity;
import org.twinone.irremote.ui.dialogs.EditColorDialog;
import org.twinone.irremote.ui.dialogs.EditColorDialog.OnColorSelectedListener;
import org.twinone.irremote.ui.dialogs.EditCornersDialog;
import org.twinone.irremote.ui.dialogs.EditCornersDialog.OnCornersEditedListener;
import org.twinone.irremote.ui.dialogs.EditIconDialog;
import org.twinone.irremote.ui.dialogs.EditIconDialog.OnIconSelectedListener;
import org.twinone.irremote.ui.dialogs.EditSizeDialog;
import org.twinone.irremote.ui.dialogs.EditSizeDialog.OnSizeChangedListener;
import org.twinone.irremote.ui.dialogs.EditTextDialog;
import org.twinone.irremote.ui.dialogs.EditTextDialog.OnTextChangedListener;
import org.twinone.irremote.ui.dialogs.EditTextSizeDialog;
import org.twinone.irremote.ui.dialogs.EditTextSizeDialog.OnTextSizeChangedListener;
import org.twinone.irremote.ui.dialogs.OrganizeDialog;
import org.twinone.irremote.ui.dialogs.OrganizeDialog.OrganizeListener;
import org.twinone.irremote.ui.dialogs.RenameRemoteDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Multi-edit
 * <p/>
 * TODO: SelectIconDialogFragment
 * <p/>
 * TODO: In ProviderActivity for {@link ProviderActivity#ACTION_GET_BUTTON},
 * allow direct reading (???)
 */
public class EditRemoteFragment extends BaseRemoteFragment implements
        OnDragListener, OnLongClickListener, OnClickListener, Callback, RenameRemoteDialog.OnRemoteRenamedListener {

    private static final String SAVE_EDITED = "save_edited";
    private static final String SAVE_TARGETS = "save_targets";

    private static final int REQ_GET_NEW_BUTTON = 0;
    private static final int REQ_GET_BUTTON_CODE_FOR_EXISTING_BUTTON = 1;

    // private static final int OPTION_TEXT = 0;
    private static final int OPTION_TEXT_SIZE = 0;
    // private static final int OPTION_SIZE = 2;
    private static final int OPTION_ICON = 1;
    private static final int OPTION_COLOR = 2;
    private static final int OPTION_CORNERS = 3;
    private static final int OPTION_CODE = 4;
    private static final int OPTION_REMOVE = 5;
    private static final int SCROLL_DELAY = 15;
    private final MyEditTypeListener mEditTypeListener = new MyEditTypeListener();
    private boolean mIsEdited;
    private int mScrollPixels;
    private int mGridSizeX;
    private int mGridSizeY;
    private int mGridMarginX;
    private int mGridMarginY;
    private int mMarginLeft;
    private int mMarginTop;
    private boolean mScrolling;
    private Runnable mScrollRunnable;
    private ArrayList<Integer> mTargetInts = new ArrayList<>();
    private MenuItem mMenuSave;
    private ActionMode mActionMode;
    private MenuItem mSelectAll;
    private MenuItem mSelectNone;

    public boolean isEdited() {
        return mIsEdited;
    }

    private void setEdited(boolean edited) {
        if (mMenuSave != null) {
            mMenuSave.setVisible(edited);
        }
        mIsEdited = edited;
    }

    /**
     * Return the nth target
     *
     * @param index
     * @return
     */
    private ButtonView getTarget(int index) {
        return mRemoteView.findButtonViewById(mTargetInts.get(index));
    }

    private void toggleTarget(final ButtonView bv) {
        // TODO increment counter...
        final int uid = bv.getButton().uid;
        if (mTargetInts.contains(uid)) {
            removeTarget(bv);
        } else {
            addTarget(bv);
        }
        updateButtonCount();
    }

    private void updateButtonCount() {
        if (isInActionMode()) {
            int size = mTargetInts.size();
            mActionMode.setTitle(getResources().getQuantityString(
                    R.plurals.selected, size, size));
            boolean all = mTargetInts.size() == getRemote().buttons.size();
            mSelectAll.setVisible(!all);
            mSelectNone.setVisible(all);
        }
    }

    private void addTarget(final ButtonView bv) {
        final int uid = bv.getButton().uid;
        if (!mTargetInts.contains(uid)) {
            mTargetInts.add(bv.getButton().uid);
            bv.setPressed(true);
            bv.setPressLock(true);
        }
    }

    private void removeTarget(final ButtonView bv) {
        final int uid = bv.getButton().uid;
        if (mTargetInts.contains(uid)) {
            mTargetInts.remove(mTargetInts.indexOf(uid));
            bv.setPressLock(false);
            bv.setPressed(false);
        }
        if (mTargetInts.size() == 0) {
            exitActionMode();
        }
    }

    private ArrayList<ButtonView> getTargets() {
        ArrayList<ButtonView> result = new ArrayList<>();
        for (int uid : mTargetInts) {
            result.add(mRemoteView.findButtonViewById(uid));
        }
        return result;
    }

    private void startScrolling(final int pixels) {
        if (mScrolling) {
            stopScrolling();
        }
        mScrollRunnable = new Runnable() {

            @Override
            public void run() {
                mScroll.scrollBy(0, pixels);
                mHandler.postDelayed(mScrollRunnable, SCROLL_DELAY);
                if (pixels > 0) {
                    mRemote.details.h += pixels;
                    mRemoteView.requestLayout();
                }
            }
        };
        mHandler.post(mScrollRunnable);
        mScrolling = true;
    }

    private void stopScrolling() {
        if (mScrolling && mScrollRunnable != null) {
            mHandler.removeCallbacks(mScrollRunnable);
            mScrollRunnable = null;
            mScrolling = false;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (!isInActionMode()) {
            ClipData data = ClipData.newPlainText("", "");
            DragShadowBuilder shadowBuilder = new DragShadowBuilder(v);
            v.startDrag(data, shadowBuilder, v, 0);
            v.setVisibility(View.GONE);
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        if (!(v instanceof ButtonView)) {
            return;
        }
        final ButtonView bv = (ButtonView) v;
        if (isInActionMode()) {
            toggleTarget(bv);
        } else {
            enterActionMode();
            addTarget(bv);
            updateButtonCount();
            // mTargetInts.clear();
            // mTargetInts.add(bv.getButton().uid);
            // showEditDialog();
        }
    }

    private void showEditDialog() {
        if (mTargetInts.size() == 0)
            return;
        AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
        ab.setTitle(R.string.edit_button_title);
        ab.setItems(R.array.edit_button_options, mEditTypeListener);
        AnimHelper.showDialog(ab).setCanceledOnTouchOutside(true);
    }

    /**
     * Should be called when a single edit action (such as changing text or
     * icon) is finished
     */
    private void onEditFinished() {
        exitActionMode();
    }

    private void editText() {
        String initialText = null;
        if (mTargetInts.size() == 1) {
            initialText = getTarget(0).getButton().text;
        }
        EditTextDialog d = EditTextDialog.newInstance(initialText);
        d.setListener(new OnTextChangedListener() {

            @Override
            public void onTextChanged(String newText) {
                for (ButtonView v : getTargets()) {
                    v.setText(newText, true);
                }
                refreshButtonsLayout();
                onEditFinished();

            }
        });
        d.show(getActivity());
    }

    private void editTextSize() {
        int size = 0;
        final List<ButtonView> targets = getTargets();
        for (ButtonView bv : targets) {
            size += bv.getButton().getTextSize();
        }
        size /= targets.size();

        EditTextSizeDialog d = EditTextSizeDialog.newInstance(size);
        d.setListener(new OnTextSizeChangedListener() {

            @Override
            public void onTextSizeChanged(int newSize) {
                for (ButtonView v : getTargets()) {
                    v.getButton().setTextSize(newSize);
                }
                refreshButtonsLayout();
                onEditFinished();

            }
        });
        d.show(getActivity());
    }

    private void editSize() {

        float totalW = 0;
        float totalH = 0;
        for (ButtonView v : getTargets()) {
            totalW += v.getButton().w;
            totalH += v.getButton().h;
        }
        totalW /= mTargetInts.size();
        totalH /= mTargetInts.size();
        final int w = (int) (totalW / mGridSizeX) + 1;
        final int h = (int) (totalH / mGridSizeY) + 1;
        EditSizeDialog d = EditSizeDialog.newInstance(w, h);
        d.setListener(new OnSizeChangedListener() {

            @Override
            public void onSizeChanged(int blocksW, int blocksH) {
                int width = blocksW * mGridSizeX - mGridMarginX;
                int height = blocksH * mGridSizeY - mGridMarginY;
                for (ButtonView v : getTargets()) {
                    v.setWidth(width);
                    v.setHeight(height);
                    v.requestLayout();
                }
                refreshButtonsLayout();
                adjustRemoteLayoutHeightToButtons();
                onEditFinished();
            }
        });
        d.show(getActivity());

    }

    private void editIcon() {
        EditIconDialog d = EditIconDialog.newInstance(0);

        d.setListener(new OnIconSelectedListener() {

            @Override
            public void onIconSelected(int iconId) {

                for (ButtonView v : getTargets()) {
                    v.setIcon(iconId);
                    // v.setText(null, true);
                }
                refreshButtonsLayout();
                onEditFinished();
            }
        });
        d.show(getActivity());

    }

    private void editColor() {
        EditColorDialog d = EditColorDialog.newInstance(0);

        d.setListener(new OnColorSelectedListener() {

            @Override
            public void onColorSelected(int color) {
                for (ButtonView v : getTargets()) {
                    v.setBackground(color);
                }
                refreshButtonsLayout();
                onEditFinished();
            }
        });
        d.show(getActivity());

    }

    private void editCorners() {
        final ArrayList<ButtonView> targets = getTargets();
        EditCornersDialog d = new EditCornersDialog();

        d.setListener(new OnCornersEditedListener() {

            @Override
            public void onCornersEdited(float cornerRadius) {
                for (ButtonView v : targets) v.getButton().setCornerRadius(cornerRadius);
                refreshButtonsLayout();
                onEditFinished();
            }
        });
        d.show(getActivity());
    }

    private void editCode() {
        Intent i = new Intent(getActivity(), ProviderActivity.class);
        i.setAction(ProviderActivity.ACTION_GET_BUTTON);
        startActivityForResult(i, REQ_GET_BUTTON_CODE_FOR_EXISTING_BUTTON);
    }

    private void requestNewButton() {
        Intent i = new Intent(getActivity(), ProviderActivity.class);
        i.setAction(ProviderActivity.ACTION_GET_BUTTON);
        startActivityForResult(i, REQ_GET_NEW_BUTTON);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQ_GET_BUTTON_CODE_FOR_EXISTING_BUTTON) {

            Button result = (Button) data
                    .getSerializableExtra(ProviderActivity.EXTRA_RESULT_BUTTON);
            for (ButtonView v : getTargets()) {
                v.getButton().code = result.code;
                v.getButton().text = result.text;
                refreshButtonsLayout();
            }
            Toast.makeText(getActivity(), R.string.edit_button_code_updated,
                    Toast.LENGTH_SHORT).show();
        } else if (requestCode == REQ_GET_NEW_BUTTON) {
            Button result = (Button) data
                    .getSerializableExtra(ProviderActivity.EXTRA_RESULT_BUTTON);
            addNewButton(result);
        }
    }

    /**
     * Add a new button to the menu_main without saving
     */
    private void addNewButton(Button b) {
        RemoteOrganizer ro = new RemoteOrganizer(getActivity());
        ro.setupNewButton(b);
        getRemote().addButton(b);
        refreshButtonsLayout();
    }

    private void editRemove() {
        // boolean action = isInActionMode();
        for (ButtonView v : getTargets()) {
            getRemote().removeButton(v.getButton());
            removeTarget(v);
            // if (action) {
            // final int uid = v.getButton().uid;
            // if (mTargetInts.contains(uid))
            // mTargetInts.remove(mTargetInts.);
            // }
        }

        refreshButtonsLayout();
        adjustRemoteLayoutHeightToButtons();
        onEditFinished();
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        View view;
        switch (event.getAction()) {

            case DragEvent.ACTION_DRAG_ENTERED:
                break;

            case DragEvent.ACTION_DRAG_LOCATION:
                final int height = mScroll.getHeight();
                int AUTOSCROLL_PERCENTAGE = 15;
                final float percent = height * AUTOSCROLL_PERCENTAGE / 100;
                final float ypos = (int) event.getY();
                if (ypos < percent) {
                    float speed = 1 - ypos / percent;
                    startScrolling((int) (-mScrollPixels * speed * 2));
                } else if (ypos > height - percent) {
                    float speed = (ypos - height + percent) / percent;
                    startScrolling((int) (mScrollPixels * speed * 2));
                } else {
                    stopScrolling();
                }
                break;
            case DragEvent.ACTION_DROP:
                view = (View) event.getLocalState();
                if (view == null)
                    break;

                centerButtonAt(view, event.getX(), event.getY());
                setEdited(true);

                break;
            case DragEvent.ACTION_DRAG_ENDED:
                view = (View) event.getLocalState();
                if (view != null) {
                    view.setVisibility(View.VISIBLE);
                    view.bringToFront();
                }
                stopScrolling();

                adjustRemoteLayoutHeightToButtons();

                break;
        }

        return true;
    }

    private void centerButtonAt(View view, float x, float y) {
        x = (x - (view.getWidth() / 2));
        y = (y - (view.getHeight() / 2));
        // ScrollView support
        y += mScroll.getScrollY();

        view.setX(round(x, mGridSizeX, mMarginLeft));
        view.setY(round(y, mGridSizeY, mMarginTop));
    }

    private void showOrganizeDialog() {
        OrganizeDialog od = new OrganizeDialog();
        od.setListener(new OrganizeListener() {

            @Override
            public void onOrganize(int flags) {
                organizeButtons(flags);
            }
        });
        od.show(getActivity());
    }

    private void organizeButtons(int flags) {
        RemoteOrganizer ro = new RemoteOrganizer(getActivity());
        ro.setFlags(flags);
        ro.updateWithoutSaving(getRemote());

        setupMargins();
        refreshButtonsLayout();
        adjustRemoteLayoutHeightToButtons();
    }

    private void setupMargins() {
        mMarginLeft = mRemote.details.marginLeft;
        mMarginTop = mRemote.details.marginTop;
    }

    /**
     * Refresh the buttons' positions and set the menu_main to edited state.
     */
    private void refreshButtonsLayout() {
        setupButtons();
        setEdited(true);
    }

    public void saveRemote() {
        if (isEdited()) {
            getRemote().save(getActivity());
            Toast.makeText(getActivity(), R.string.remote_saved_toast,
                    Toast.LENGTH_SHORT).show();
            setEdited(false);
        }
    }

    private void adjustRemoteLayoutHeightToButtons() {
        int max = 0;
        // ButtonView bottomView = null;
        for (ButtonView bv : mButtons) {
            float bottom = bv.getButton().y + bv.getButton().h;
            max = Math.max(max, (int) bottom);

        }

        // Log.d(TAG,
        // "BottomView (" + bottomView.getButton().text + " y: "
        // + bottomView.getY() + " translationY: "
        // + bottomView.getTranslationY() + ", bottom: "
        // + bottomView.getBottom());
        int h = max + mMarginTop;
        int w = mRemoteView.getWidth();
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(w, h);
        mRemoteView.setLayoutParams(lp);
        getRemote().details.h = h;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupMargins();

        mGridSizeX = getResources().getDimensionPixelSize(R.dimen.grid_size_x);
        mGridSizeY = getResources().getDimensionPixelSize(R.dimen.grid_size_y);
        mGridMarginX = getResources().getDimensionPixelSize(
                R.dimen.grid_spacing_x);
        mGridMarginY = getResources().getDimensionPixelSize(
                R.dimen.grid_spacing_y);

        int SCROLL_DP = 3;
        mScrollPixels = (int) dpToPx(SCROLL_DP);

        if (savedInstanceState != null) {
            mIsEdited = savedInstanceState.getBoolean(SAVE_EDITED);
            mTargetInts = savedInstanceState
                    .getIntegerArrayList(SAVE_TARGETS);
        }

        autoHelpDialogIfNeeded();
        setupTitle();
    }

    private void setupTitle() {
        getActivity().setTitle(getString(R.string.edit_activity_title, mRemote.name));
    }

    private void autoHelpDialogIfNeeded() {
        SharedPreferences sp = getActivity().getSharedPreferences("edit",
                Context.MODE_PRIVATE);
        if (!sp.contains("hide_help_at_startup")) {
            showHelpDialog();
            sp.edit().putBoolean("hide_help_at_startup", true).apply();
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(SAVE_EDITED, mIsEdited);
        outState.putIntegerArrayList(SAVE_TARGETS, mTargetInts);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.edit, menu);
        mMenuSave = menu.findItem(R.id.menu_edit_save);
        mMenuSave.setVisible(mIsEdited);
        // mSnapToGrid = menu.findItem(R.id.menu_edit_snap).isChecked();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_action_delete:
                showDeleteRemoteDialog();
                return true;
            case R.id.menu_action_rename:
                showRenameRemoteDialog();
                break;
            case R.id.menu_edit_add_button:
                requestNewButton();
                break;
            case R.id.menu_edit_organize:
                showOrganizeDialog();
                break;
            case R.id.menu_edit_save:
                saveRemote();
                break;
            case R.id.menu_edit_help:
                showHelpDialog();
                break;
            default:
                return false;
        }
        return true;
    }

    private void showRenameRemoteDialog() {
        RenameRemoteDialog d = RenameRemoteDialog
                .newInstance(mRemote.name);
        d.setOnRemoteRenamedListener(this);
        d.show(getActivity());
    }

    private void showDeleteRemoteDialog() {
        final String remoteName = mRemote.name;
        AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
        ab.setTitle(R.string.delete_remote_title);
        ab.setMessage(getString(R.string.delete_remote_message, remoteName));
        ab.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                Remote.remove(getActivity(), remoteName);
                getActivity().finish();
            }
        });
        ab.setNegativeButton(android.R.string.cancel, null);
        AnimHelper.showDialog(ab);
    }

    private void showHelpDialog() {
        AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
        ab.setTitle(R.string.edit_helpdlg_tit);
        ab.setMessage(R.string.edit_helpdlg_msg);
        ab.setPositiveButton(android.R.string.ok, null);
        AnimHelper.showDialog(ab);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        v.setOnDragListener(this);
        return v;
    }

    @Override
    void setupButtons() {
        super.setupButtons();
        for (ButtonView bv : mButtons) {
            bv.setOnLongClickListener(this);
            bv.setOnClickListener(this);
        }
    }

    private int round(float what, int to, int offset) {
        // http://stackoverflow.com/questions/16338162/round-to-nearest-multiple-with-offset-in-js
        offset %= to;
        return (Math.round((what - offset) / to) * to) + offset;
    }

	/* ACTION MODE */

    private float dpToPx(float dp) {
        return dp * getActivity().getResources().getDisplayMetrics().density;
    }

    private float pxToDp(float px) {
        return px / getActivity().getResources().getDisplayMetrics().density;
    }

    private boolean isInActionMode() {
        return mActionMode != null;
    }

    private void enterActionMode() {
        if (mActionMode == null) {
            mTargetInts.clear();
            getActivity().startActionMode(this);
        }
    }

    private void exitActionMode() {
        if (mActionMode != null)
            mActionMode.finish();
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_edit_cab_edit:
                showEditDialog();
                return true;
            case R.id.menu_edit_cab_sel_all:
                setAllSelected(true);
                return true;
            case R.id.menu_edit_cab_sel_none:
                setAllSelected(false);
                return true;
            default:
                return false;
        }
    }

    private void setAllSelected(boolean select) {
        mSelectAll.setVisible(!select);
        mSelectNone.setVisible(select);

        if (select) {
            for (ButtonView bv : mButtons) {
                addTarget(bv);
            }
        } else {
            for (ButtonView bv : mButtons) {
                removeTarget(bv);
            }
        }
        updateButtonCount();
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        getToolbar().setVisibility(View.INVISIBLE);
        mActionMode = mode;
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.menu_edit_cab, menu);
        return true;
    }

    private Toolbar getToolbar() {
        return ((ToolbarActivity) getActivity()).getToolbar();
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        getToolbar().setVisibility(View.VISIBLE);
        for (ButtonView bv : getTargets()) {
            bv.setPressLock(false);
            bv.setPressed(false);
        }
        mActionMode = null;
        mSelectAll = null;
        mSelectNone = null;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        mSelectAll = menu.findItem(R.id.menu_edit_cab_sel_all);
        mSelectNone = menu.findItem(R.id.menu_edit_cab_sel_none);
        return false;
    }

    private class MyEditTypeListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                // case OPTION_TEXT:
                // editText();
                // break;
                // case OPTION_SIZE:
                // editSize();
                // break;
                case OPTION_TEXT_SIZE:
                    editTextSize();
                    break;
                case OPTION_ICON:
                    editIcon();
                    break;
                case OPTION_COLOR:
                    editColor();
                    break;
                case OPTION_CORNERS:
                    editCorners();
                    break;
                case OPTION_CODE:
                    editCode();
                    break;
                case OPTION_REMOVE:
                    editRemove();
                    break;
            }
        }
    }

    @Override
    public void onRemoteRenamed(String newName) {
        mRemote.name = newName;
        setupTitle();
    }
}