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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.Toast;

public class SelectRemoteListView extends ListView implements
		OnItemClickListener {

	private SelectRemoteAdapter mAdapter;
	private LayoutInflater mInflater;
	private ArrayList<String> mRemoteNames;

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
		mInflater = LayoutInflater.from(getContext());
		mRemoteNames = (ArrayList<String>) Remote.getNames(getContext());
		mRemoteNames.add(getContext().getString(R.string.add_remote));
		setOnItemClickListener(this);
		mAdapter = new SelectRemoteAdapter();
		setAdapter(mAdapter);
	}

	private class SelectRemoteAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mRemoteNames.size();
		}

		@Override
		public Object getItem(int position) {
			return mRemoteNames.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			CheckedTextView view = (CheckedTextView) convertView;
			if (view == null)
				view = (CheckedTextView) mInflater.inflate(
						R.layout.select_remote_item, parent, false);
			view.setChecked(position == mSelectedRemotePosition);
			view.setText(mRemoteNames.get(position));
			return view;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position,
			long id) {
		if (position == mRemoteNames.size() - 1) {
			createRemote();
		} else {
			selectRemote(position);
		}
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
		mAdapter.notifyDataSetChanged();
	}

	public int getSelectedRemotePosition() {
		return mSelectedRemotePosition;
	}

	public String getSelectedRemoteName() {
		if (isRemoteSelected()) {
			return mRemoteNames.get(mSelectedRemotePosition);
		} else {
			return null;
		}
	}

	public boolean isRemoteSelected() {
		return mSelectedRemotePosition >= 0
				&& mSelectedRemotePosition < mRemoteNames.size();
	}

}
