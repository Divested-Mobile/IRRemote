/*
 * Copyright 2014 Luuk Willemsen (Twinone)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.twinone.irremote.ui;

import java.util.ArrayList;

import org.twinone.irremote.R;
import org.twinone.irremote.Remote;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SelectRemoteListView extends ListView implements OnClickListener {

	private static final String TAG = "SelectRemoteListView";
	private boolean mShowAddRemote = false;

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
	}

	/**
	 * Update the list of remotes after it has been changed on disk
	 */
	public void updateRemotesList() {
		mItems = (ArrayList<String>) Remote.getNames(getContext());
		mItems.add(getContext().getString(R.string.add_remote));
		mAdapter = new MyAdapter();
		setAdapter(mAdapter);
		setItemChecked(mSelectedItemPosition, true);
	}

	public void setShowAddRemote(boolean showAddRemote) {
		if (mShowAddRemote != showAddRemote)
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
			Log.d("", "getView");
			TextView view = (TextView) convertView;
			if (view == null)
				view = (TextView) mInflater.inflate(
						R.layout.select_remote_item, parent, false);
			view.setText(mItems.get(position));
			view.setOnClickListener(SelectRemoteListView.this);
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

	public void selectRemote(String remoteName) {
		if (remoteName == null || remoteName.isEmpty())
			return;
		if (mItems.contains(remoteName)) {
			selectRemote(mItems.indexOf(remoteName));
		} else {
			Log.w(TAG, "Attempted to select remote \"" + remoteName
					+ "\", but was not in list!");
		}
	}

	public void selectRemote(int position) {
		if (position == mSelectedItemPosition)
			return;
		mSelectedItemPosition = position;
		Log.d("", "SetChecked: " + position);
		setItemChecked(position, true);
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

	public interface OnSelectListener {
		public void onRemoteSelected(int position, String remoteName);

		public void onAddRemoteSelected();
	}

}
