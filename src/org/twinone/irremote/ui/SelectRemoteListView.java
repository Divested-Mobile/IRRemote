package org.twinone.irremote.ui;

import java.util.ArrayList;

import org.twinone.irremote.R;
import org.twinone.irremote.components.Remote;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SelectRemoteListView extends ListView implements
		android.widget.AdapterView.OnItemClickListener {

	private static final String TAG = "SelectRemoteListView";
	private boolean mShowAddRemote = true;

	private int mSelectedItemPosition = -1;

	private LayoutInflater mInflater;
	private ArrayList<String> mItems;
	private MyAdapter mAdapter;

	public SelectRemoteListView(Context context) {
		super(context);
		init();
	}

	public SelectRemoteListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		setChoiceMode(CHOICE_MODE_SINGLE);
		mInflater = LayoutInflater.from(getContext());
		updateRemotesList();
		setOnItemClickListener(this);
		// setSelector(new ColorDrawable(getResources().getColor(
		// R.color.material_red_500)));
	}

	/**
	 * Update the list of remotes after it has been changed on disk
	 */
	public void updateRemotesList() {
		mItems = (ArrayList<String>) Remote.getNames(getContext());
		if (mShowAddRemote) {
			mItems.add(getContext().getString(R.string.add_remote));
		}
		mAdapter = new MyAdapter();
		setAdapter(mAdapter);
		setItemChecked(mSelectedItemPosition, true);
	}

	public void setShowAddRemote(boolean showAddRemote) {
		if (mShowAddRemote == showAddRemote)
			return;
		if (showAddRemote) {
			mItems.add(getContext().getString(R.string.add_remote));
		} else {
			// Add should always be on last position
			mItems.remove(mItems.size() - 1);
		}
		mShowAddRemote = showAddRemote;
		mAdapter.notifyDataSetChanged();
	}

	private class MyAdapter extends BaseAdapter {

		public View getView(int position, View convertView, ViewGroup parent) {
			TextView view = (TextView) convertView;
			if (view == null)
				view = (TextView) mInflater.inflate(
						R.layout.select_remote_item, parent, false);
			view.setText(mItems.get(position));
			// view.setOnClickListener(SelectRemoteListView.this);
			view.setId(position);
			return view;
		}

		@Override
		public int getCount() {
			return mItems.size();
		}

		@Override
		public Object getItem(int position) {
			return mItems.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}
	}

	public void selectRemote(String remoteName, boolean notifyListener) {
		if (remoteName == null || remoteName.isEmpty())
			return;
		if (mItems.contains(remoteName)) {
			selectRemote(mItems.indexOf(remoteName), notifyListener);
		} else {
			Log.w(TAG, "Attempted to select remote \"" + remoteName
					+ "\", but was not in list!");
		}
	}

	public void selectRemote(int position, boolean notifyListener) {
		if (position == mSelectedItemPosition)
			return;
		mSelectedItemPosition = position;
		setItemChecked(position, true);
		if (notifyListener && mListener != null)
			mListener.onRemoteSelected(position, mItems.get(position));
	}

	public String getRemoteName(int position) {
		return ((String) getItemAtPosition(position));
	}

	public String getSelectedRemoteName() {
		if (isRemoteSelected()) {
			Log.d("",
					"SelectedRemoteName: " + mItems.get(mSelectedItemPosition));
			return mItems.get(mSelectedItemPosition);
		} else {
			Log.d("", "SelectedRemoteName: null");
			return null;
		}
	}

	public boolean isRemoteSelected() {
		if (mSelectedItemPosition < 0)
			return false;
		if (mSelectedItemPosition >= mItems.size())
			return false;
		if (mShowAddRemote && mSelectedItemPosition >= mItems.size() - 1)
			return false;
		return true;
	}

	public boolean isAddRemoteSelected() {
		return mShowAddRemote && mSelectedItemPosition == mItems.size() - 1;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (isAddRemoteSelected()) {
			if (mListener != null) {
				mListener.onAddRemoteSelected();
			}
		} else {
			selectRemote(position, true);
		}

	}

	// @Override
	// public void onClick(View view) {
	// int position = view.getId();
	// if (position == mItems.size() - 1) {
	// if (mListener != null) {
	// mListener.onAddRemoteSelected();
	// }
	// } else {
	// selectRemote(position, true);
	// }
	// }

	private OnRemoteSelectedListener mListener;

	public void setOnSelectListener(OnRemoteSelectedListener listener) {
		this.mListener = listener;
	}

	public interface OnRemoteSelectedListener {
		public void onRemoteSelected(int position, String remoteName);

		public void onAddRemoteSelected();
	}

}
