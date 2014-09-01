package org.twinone.irremote.ui;

import java.util.ArrayList;

import org.twinone.irremote.R;
import org.twinone.irremote.components.Button;

import android.content.ClipData;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnDragListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

public class EditRemoteFragment extends BaseRemoteFragment implements
		OnDragListener {

	private ArrayList<ButtonView> mButtons;

	private static int AUTOSCROLL_PERCENTAGE = 15;
	private static int SCROLL_DP = 3; // converts to mScrollPixels
	private static int SCROLL_DELAY = 15;

	private int mScrollPixels;

	private static int DEFAULT_GRID_SIZE_X = 16;// in dp
	private static int DEFAULT_GRID_SIZE_Y = 16;// in dp
	private boolean mModified;

	private int mGridSizeX;
	private int mGridSizeY;

	private RelativeLayout mContainer;
	private ScrollView mScroll;

	public boolean isModified() {
		return mModified;
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
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			ClipData data = ClipData.newPlainText("", "");
			DragShadowBuilder shadowBuilder = new DragShadowBuilder(v);
			v.startDrag(data, shadowBuilder, v, 0);

			v.setVisibility(View.INVISIBLE);
			// Log.d(TAG, "Touched " + ((ButtonView) v).getButton().text);
			return true;
		}
		return false;
	}

	@Override
	public boolean onDrag(View v, DragEvent event) {
		switch (event.getAction()) {

		case DragEvent.ACTION_DRAG_ENTERED:
			break;

		case DragEvent.ACTION_DRAG_LOCATION:
			final int height = mScroll.getHeight();
			final float percent = height * AUTOSCROLL_PERCENTAGE / 100;
			final float ypos = (int) event.getY();
			if (ypos < percent) {
				// Log.d(TAG, "ypos:" + ypos + " percent:" + percent + "y/p: "
				// + (ypos / percent));
				float speed = 1 - ypos / percent;
				// Log.d(TAG, "Speed: " + speed);
				startScrolling((int) (-mScrollPixels * speed * 2));
			} else if (ypos > height - percent) {
				float speed = (ypos - height + percent) / percent;
				// Log.d(TAG, "Speed: " + speed);
				startScrolling((int) (mScrollPixels * speed * 2));
			} else {
				stopScrolling();
			}
			break;
		case DragEvent.ACTION_DROP:

			View view = (View) event.getLocalState();
			if (view == null)
				break;

			// ViewGroup parent = (ViewGroup) view.getParent();
			// parent.removeView(view);

			float x = (event.getX() - (view.getWidth() / 2));
			float y = (event.getY() - (view.getHeight() / 2));
			// ScrollView support
			y += mScroll.getScrollY();

			view.setX(round(x, mGridSizeX));
			view.setY(round(y, mGridSizeY));

//			mContainer.addView(view);
			view.setVisibility(View.VISIBLE);
			stopScrolling();

			resizeContainer();

			break;
		}
		return true;
	}

	private void resizeContainer() {

		int max = 0;
		// Tmp
		ButtonView btm = null;
		//
		for (ButtonView bv : mButtons) {

			// tmp
			int bottom = bv.getBottom();
			if (bottom > max) {
				btm = bv;
			}
			max = Math.max(max, (int) (bv.getBottom() + bv.getTranslationY()));
			//

		}
		Log.d(TAG, "Bottom bv: " + btm.getButton().text + " at " + max + " px");
		Log.d(TAG, "Current height of the view: " + mContainer.getHeight());
		int h = (int) max;
		int w = mContainer.getWidth();
		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(w, h);
		mContainer.setLayoutParams(lp);

		Log.d(TAG, "Contained: " + mButtons.contains(btm));

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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		if (mRemote == null) {
			return new View(getActivity());
		}

		mScroll = (ScrollView) inflater.inflate(R.layout.fragment_remote_new,
				container, false);

		mContainer = (RelativeLayout) mScroll.findViewById(R.id.container);

		mButtons = new ArrayList<ButtonView>(mRemote.buttons.size());
		for (Button b : mRemote.buttons) {
			ButtonView bv = new ButtonView(getActivity());
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
					(int) b.w, (int) b.h);
			// bv.setX(b.x);
			// bv.setY(b.y);
			lp.topMargin = (int) b.y;
			lp.leftMargin = (int) b.x;
			bv.setLayoutParams(lp);
			bv.setButton(b);
			bv.setOnTouchListener(this);
			mButtons.add(bv);
			mContainer.addView(bv);
		}

		mScroll.setOnDragListener(this);

		return mScroll;
	}

	private float dpToPx(float dp) {
		return dp * getActivity().getResources().getDisplayMetrics().density;
	}

}