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
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class SelectRemoteListView extends LinearLayout implements
		OnClickListener {

	private ArrayList<View> mViews;
	private LayoutInflater mInflater;
	private ArrayList<String> mItems;

	private int mSelectedRemotePosition = -1;

	public SelectRemoteListView(Context context) {
		super(context);
		init();
	}

	public SelectRemoteListView(Context context, AttributeSet attrs) {
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
		view.setChecked(position == mSelectedRemotePosition);
		view.setText(mItems.get(position));
		view.setOnClickListener(this);
		view.setId(position);
		return view;
	}

	private void createRemote() {
		// TODO
		Toast.makeText(getContext(),
				"Wow, it looks like this action is not available yet!",
				Toast.LENGTH_SHORT).show();
	}

	public void selectRemote(int position) {
		if (position == mSelectedRemotePosition)
			return;
		Log.d("", "Selecting position: " + position);
		mSelectedRemotePosition = position;
		loadViews();
	}

	public int getSelectedRemotePosition() {
		return mSelectedRemotePosition;
	}

	public String getSelectedRemoteName() {
		if (isRemoteSelected()) {
			return mItems.get(mSelectedRemotePosition);
		} else {
			return null;
		}
	}

	public boolean isRemoteSelected() {
		return mSelectedRemotePosition >= 0
				&& mSelectedRemotePosition < mItems.size();
	}

	@Override
	public void onClick(View view) {
		int position = view.getId();
		Log.d("", "Clicked view " + position);
		if (position == mItems.size() - 1) {
			createRemote();
		} else {
			selectRemote(position);
		}

	}

}
