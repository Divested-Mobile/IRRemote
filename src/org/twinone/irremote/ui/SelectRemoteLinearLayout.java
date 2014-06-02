package org.twinone.irremote.ui;

import java.util.ArrayList;

import org.twinone.irremote.R;
import org.twinone.irremote.Remote;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;

// Alternative to SelectRemoteListView that is much more efficient because it doesn't scroll
public class SelectRemoteLinearLayout extends LinearLayout implements
		OnClickListener {

	private ArrayList<View> mViews;
	private LayoutInflater mInflater;
	private ArrayList<String> mItems;

	private int mSelectedItemPosition = -1;

	public SelectRemoteLinearLayout(Context context) {
		super(context);
		init();
	}

	public SelectRemoteLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		mViews = new ArrayList<View>();
		mInflater = LayoutInflater.from(getContext());
		mItems = (ArrayList<String>) Remote.getNames(getContext());
		mItems.add(getContext().getString(R.string.add_remote));
		setOrientation(VERTICAL);

		loadViews();
	}

	@Override
	public void addView(View child) {
		super.addView(child);
		mViews.add(child);
	}

	private void loadViews() {
		for (int i = 0; i < mItems.size(); i++) {
			View recycleView = i < mViews.size() ? mViews.get(i) : null;
			if (recycleView == null) {
				addView(getView(i, recycleView, this));
			} else {
				getView(i, recycleView, this);
			}
		}
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		CheckedTextView view = (CheckedTextView) convertView;
		if (view == null)
			view = (CheckedTextView) mInflater.inflate(
					R.layout.select_remote_item, parent, false);
		view.setChecked(position == mSelectedItemPosition);
		view.setText(mItems.get(position));
		view.setOnClickListener(this);
		view.setId(position);
		return view;
	}

	public void selectRemote(int position) {
		if (position == mSelectedItemPosition)
			return;
		mSelectedItemPosition = position;
		loadViews();
	}

	public int getSelectedRemotePosition() {
		return mSelectedItemPosition;
	}

	public String getSelectedRemoteName() {
		if (isRemoteSelected()) {
			return mItems.get(mSelectedItemPosition);
		} else {
			return null;
		}
	}

	public boolean isRemoteSelected() {
		return mSelectedItemPosition >= 0
				&& mSelectedItemPosition < mItems.size();
	}

	public boolean isAddRemoteSelected() {
		return mSelectedItemPosition == mItems.size();
	}

	@Override
	public void onClick(View view) {
		int position = view.getId();
		if (position == mItems.size() - 1) {
			if (mListener != null) {
				mListener.onAddRemoteSelected();
			}
		} else {
			selectRemote(position);
			if (mListener != null) {
				mListener.onRemoteSelected(position, mItems.get(position));
			}
		}
	}

	private OnSelectListener mListener;

	public void setOnSelectListener(OnSelectListener listener) {
		this.mListener = listener;
	}

	public interface OnSelectListener extends
			SelectRemoteListView.OnSelectListener {
	}

}
