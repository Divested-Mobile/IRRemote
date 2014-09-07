package org.twinone.irremote.ui;

import org.twinone.irremote.R;
import org.twinone.irremote.compat.RemoteOrganizer;
import org.twinone.irremote.components.AnimHelper;
import org.twinone.irremote.components.Button;
import org.twinone.irremote.providers.ProviderActivity;
import org.twinone.irremote.providers.common.CommonProviderActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.NumberPicker;
import android.widget.Toast;

public class EditRemoteFragment extends BaseRemoteFragment implements
		OnDragListener, OnLongClickListener, OnClickListener {

	private static final String SAVE_EDITED = "save_edited";

	private static final int REQ_GET_NEW_BUTTON = 0;
	private static final int REQ_GET_BUTTON_CODE_FOR_EXISTING_BUTTON = 1;

	private static final int OPTION_TEXT = 0;
	private static final int OPTION_SIZE = 1;
	// private static final int OPTION_ICON = 2;
	// private static final int OPTION_COLOR = 3;
	// private static final int OPTION_CODE = 4;
	// private static final int OPTION_REMOVE = 5;

	private static final int OPTION_CODE = 2;
	private static final int OPTION_REMOVE = 3;

	private boolean mIsEdited;

	private boolean mSnapToGrid;

	private static int AUTOSCROLL_PERCENTAGE = 15;
	private static int SCROLL_DP = 3; // converts to mScrollPixels
	private static int SCROLL_DELAY = 15;

	private int mScrollPixels;

	private static int DEFAULT_GRID_SIZE_X = 16;// in dp
	private static int DEFAULT_GRID_SIZE_Y = 16;// in dp

	private int mGridSizeX;
	private int mGridSizeY;

	public boolean isEdited() {
		return mIsEdited;
	}

	private float mActivityMarginH;
	private float mActivityMarginV;

	private boolean mScrolling;
	private Runnable mScrollRunnable;

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
					mRemoteView.getRemote().options.h += pixels;
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
		ClipData data = ClipData.newPlainText("", "");
		DragShadowBuilder shadowBuilder = new DragShadowBuilder(v);
		v.startDrag(data, shadowBuilder, v, 0);
		v.setVisibility(View.GONE);

		return true;
	}

	private String getEditTitle(ButtonView v) {
		return getString(R.string.edit_button_title, v.getButton().text);
	}

	@Override
	public void onClick(View v) {
		if (v instanceof ButtonView) {
			final ButtonView bv = (ButtonView) v;
			AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());

			ab.setTitle(getEditTitle(bv));
			ab.setItems(R.array.edit_button_options,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							switch (which) {
							case OPTION_TEXT:
								editText(bv);
								break;
							case OPTION_SIZE:
								editSize(bv);
								break;
							// case OPTION_ICON:
							// editIcon(bv);
							// break;
							// case OPTION_COLOR:
							// editColor(bv);
							// break;
							case OPTION_CODE:
								editIrCode(bv);
								break;
							case OPTION_REMOVE:
								removeButton(bv);
								break;
							}
						}
					});
			AnimHelper.showDialog(ab);
		}
	}

	private void editText(final ButtonView v) {
		final AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
		ab.setTitle(getEditTitle(v));
		final EditText et = new EditText(getActivity());
		et.setText(v.getButton().text);

		et.setSelectAllOnFocus(true);
		ab.setView(et);
		ab.setNegativeButton(android.R.string.cancel, null);
		ab.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						v.getButton().text = et.getText().toString();
						refreshButtonsLayout();
					}
				});
		AnimHelper.showDialog(ab);

	}

	private void editSize(final ButtonView v) {
		final AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
		ab.setTitle(getEditTitle(v));
		LayoutInflater li = LayoutInflater.from(getActivity());
		View sizeView = li.inflate(R.layout.dialog_size_picker, null);
		final int w = (int) v.getButton().w / mGridSizeX;
		final int h = (int) v.getButton().h / mGridSizeY;
		final NumberPicker npw = (NumberPicker) sizeView
				.findViewById(R.id.sizepicker_width);
		npw.setMaxValue(40);
		npw.setMinValue(1);
		npw.setValue(w);
		final NumberPicker nph = (NumberPicker) sizeView
				.findViewById(R.id.sizepicker_height);
		nph.setMaxValue(40);
		nph.setMinValue(1);
		nph.setValue(h);

		ab.setView(sizeView);

		ab.setNegativeButton(android.R.string.cancel, null);
		ab.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						int width = (int) v.getButton().w
								+ (npw.getValue() - w) * mGridSizeX;
						int height = (int) v.getButton().h
								+ (nph.getValue() - h) * mGridSizeY;
						// v.getButton().w += (npw.getValue() - w) * mGridSizeX;
						// v.getButton().h += (nph.getValue() - h) * mGridSizeY;
						v.setWidth(width);
						v.setHeight(height);
						v.requestLayout();
						// ((ViewGroup) v.getParent()).removeView(v);
						// mRemoteView.addView(v);

						refreshButtonsLayout();
						adjustRemoteLayoutHeightToButtons();
					}
				});
		AnimHelper.showDialog(ab);

	}

	private void editIcon(ButtonView v) {

	}

	private void editColor(ButtonView v) {

	}

	private int mRequestCodeChangeButtonUID;

	private void editIrCode(ButtonView v) {
		Intent i = new Intent(getActivity(), CommonProviderActivity.class);
		i.setAction(ProviderActivity.ACTION_GET_BUTTON);
		mRequestCodeChangeButtonUID = v.getButton().uid;
		startActivityForResult(i, REQ_GET_BUTTON_CODE_FOR_EXISTING_BUTTON);
	}

	private void requestNewButton() {
		Intent i = new Intent(getActivity(), CommonProviderActivity.class);
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
			final Button b = mRemote.getButton(mRequestCodeChangeButtonUID);

			Button result = (Button) data
					.getSerializableExtra(ProviderActivity.EXTRA_RESULT_BUTTON);
			b.code = result.code;
			Toast.makeText(getActivity(),
					getString(R.string.edit_button_code_updated, b.text),
					Toast.LENGTH_SHORT).show();
			setEdited(true);
		} else if (requestCode == REQ_GET_NEW_BUTTON) {
			Button result = (Button) data
					.getSerializableExtra(ProviderActivity.EXTRA_RESULT_BUTTON);
			addNewButton(result);
		}
	}

	/** Add a new button to the remote without saving */
	private void addNewButton(Button b) {
		RemoteOrganizer ro = new RemoteOrganizer(getActivity());
		b.id = Button.ID_NONE;
		b.w = ro.getButtonWidth();
		b.h = ro.getButtonHeight();
		mRemote.addButton(b);
		refreshButtonsLayout();
	}

	private void removeButton(ButtonView v) {
		mRemote.removeButton(v.getButton());
		refreshButtonsLayout();
		adjustRemoteLayoutHeightToButtons();
	}

	@Override
	public boolean onDrag(View v, DragEvent event) {
		View view;
		switch (event.getAction()) {

		case DragEvent.ACTION_DRAG_ENTERED:
			break;

		case DragEvent.ACTION_DRAG_LOCATION:
			final int height = mScroll.getHeight();
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

		if (mSnapToGrid) {
			view.setX(round(x, mGridSizeX));
			view.setY(round(y, mGridSizeY));
		} else {
			view.setX(x);
			view.setY(y);
		}
	}

	private void organizeButtons() {
		new RemoteOrganizer(getActivity()).updateWithoutSaving(mRemote);
		refreshButtonsLayout();
		adjustRemoteLayoutHeightToButtons();
	}

	/**
	 * Refresh the buttons' positions and set the remote to edited state.
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

	private void setEdited(boolean edited) {
		if (mMenuSave != null) {
			mMenuSave.setVisible(edited);
		}
		mIsEdited = edited;
	}

	private void adjustRemoteLayoutHeightToButtons() {
		int max = 0;
		ButtonView bottomView = null;
		for (ButtonView bv : mButtons) {
			float bottom = bv.getBottom() + bv.getTranslationY();
			bottom = bv.getButton().y + bv.getButton().h;
			if (bottom > max) {
				bottomView = bv;
			}
			max = Math.max(max, (int) bottom);

		}

		Log.d(TAG,
				"BottomView (" + bottomView.getButton().text + " y: "
						+ bottomView.getY() + " translationY: "
						+ bottomView.getTranslationY() + ", bottom: "
						+ bottomView.getBottom());
		int h = (int) (max + mActivityMarginV);
		int w = mRemoteView.getWidth();
		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(w, h);
		mRemoteView.setLayoutParams(lp);
		mRemote.options.h = h;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivityMarginH = getResources().getDimensionPixelOffset(
				R.dimen.activity_horizontal_margin);
		mActivityMarginV = getResources().getDimensionPixelOffset(
				R.dimen.activity_vertical_margin);

		mGridSizeX = (int) dpToPx(DEFAULT_GRID_SIZE_X);
		mGridSizeY = (int) dpToPx(DEFAULT_GRID_SIZE_Y);

		mScrollPixels = (int) dpToPx(SCROLL_DP);

		if (savedInstanceState != null) {
			mIsEdited = savedInstanceState.getBoolean(SAVE_EDITED);
		}

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(SAVE_EDITED, mIsEdited);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.edit_remote, menu);
		mMenuSave = menu.findItem(R.id.menu_edit_save);
		mMenuSave.setVisible(mIsEdited);
		mSnapToGrid = menu.findItem(R.id.menu_edit_snap).isChecked();
	}

	private MenuItem mMenuSave;

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.menu_edit_add_button:
			requestNewButton();
			break;
		case R.id.menu_edit_organize:
			organizeButtons();
			break;
		case R.id.menu_edit_snap:
			item.setChecked(!item.isChecked());
			mSnapToGrid = item.isChecked();
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
	protected void setupButtons() {
		super.setupButtons();
		for (ButtonView bv : mButtons) {
			bv.setOnLongClickListener(this);
			bv.setOnClickListener(this);
		}
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

	// @Override
	// public View onCreateView(LayoutInflater inflater, ViewGroup container,
	// Bundle savedInstanceState) {
	// super.onCreateView(inflater, container, savedInstanceState);
	// if (mRemote == null) {
	// return new View(getActivity());
	// }
	//
	// mScroll = (ScrollView) inflater.inflate(R.layout.fragment_remote_new,
	// container, false);
	//
	// mContainer = (RelativeLayout) mScroll.findViewById(R.id.container);
	//
	// mButtons = new ArrayList<ButtonView>(mRemote.buttons.size());
	// for (Button b : mRemote.buttons) {
	// ButtonView bv = new ButtonView(getActivity());
	// RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
	// (int) b.w, (int) b.h);
	// // bv.setX(b.x);
	// // bv.setY(b.y);
	// lp.topMargin = (int) b.y;
	// lp.leftMargin = (int) b.x;
	// bv.setLayoutParams(lp);
	// bv.setButton(b);
	// bv.setOnTouchListener(this);
	// mButtons.add(bv);
	// mContainer.addView(bv);
	// }
	//
	// mScroll.setOnDragListener(this);
	//
	// return mScroll;
	// }

	private float dpToPx(float dp) {
		return dp * getActivity().getResources().getDisplayMetrics().density;
	}

}