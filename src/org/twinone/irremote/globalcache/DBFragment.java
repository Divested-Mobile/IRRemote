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
package org.twinone.irremote.globalcache;

import org.twinone.irremote.DBActivity;
import org.twinone.irremote.Listable;
import org.twinone.irremote.ListableAdapter;
import org.twinone.irremote.R;
import org.twinone.irremote.ir.IRManager;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnCloseListener;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

public class DBFragment extends Fragment implements
		DBConnector.OnDataReceivedListener, OnItemClickListener,
		OnItemLongClickListener {

	private static final String TAG = "DBFragment";

	public static final String ARG_URI_DATA = "com.twinone.irremote.arg.uri_data";

	private ListView mListView;

	private DBConnector mConnector;
	private IRManager mIrManager;

	private boolean mCreated;
	private AlertDialog mDialog;
	private ListableAdapter mAdapter;

	private UriData mUriData;

	public DBFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mIrManager = new IRManager(getActivity());

		if (getArguments() != null) {
			mUriData = (UriData) getArguments().getSerializable(ARG_URI_DATA);
		} else {
			mUriData = new UriData();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		Log.w(TAG, "onCreateView");
		setHasOptionsMenu(true);

		View rootView = inflater.inflate(R.layout.fragment_listable, container,
				false);

		mListView = (ListView) rootView.findViewById(R.id.lvElements);
		mListView.setOnItemClickListener(this);
		mListView.setOnItemLongClickListener(this);

		mConnector = new DBConnector(getActivity(), this);

		// Adapter stuff
		if (mCreated) {
			mListView.setAdapter(mAdapter);
			Log.d(TAG, "onCreateView restoring dataset");
			mAdapter.restoreOriginalDataSet();
		} else if (mUriData.isAvailableInCache(getActivity())) {
			queryServer(false);
		} else {
			queryServer(true);
		}

		getActivity().setTitle(mUriData.getTitle(getActivity(), " > "));

		mCreated = true;
		return rootView;
	}

	private void queryServer(boolean showDialog) {
		mListView.setAdapter(null);

		if (showDialog)
			showDialog();

		if (mConnector != null)
			mConnector.cancelQuery();

		mConnector = new DBConnector(getActivity(), this);
		mConnector.setOnDataReceivedListener(this);
		mConnector.query(mUriData.clone());
	}

	private void cancelDialog() {
		if (mDialog != null && mDialog.isShowing()) {
			mDialog.cancel();
		}
	}

	private void showDialog() {
		cancelDialog();
		AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
		ab.setCancelable(false);
		ab.setTitle("Loading...");
		ab.setNegativeButton("Cancel", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				mConnector.cancelQuery();
				navigateBack();
			}
		});
		mDialog = ab.create();
		mDialog.show();
	}

	private MySearchViewListener mSearchViewListener;
	private MenuItem mSearchMenuItem;
	private SearchView mSearchView;

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		Log.w(TAG, "onCreateOptionsMenu");
		inflater.inflate(R.menu.db_menu, menu);
		mSearchMenuItem = (MenuItem) menu.findItem(R.id.menu_db_search);
		mSearchView = (SearchView) mSearchMenuItem.getActionView();
		mSearchViewListener = new MySearchViewListener();
		mSearchView.setOnQueryTextListener(mSearchViewListener);
		mSearchView.setOnCloseListener(mSearchViewListener);
	}

	private class MySearchViewListener implements OnQueryTextListener,
			OnCloseListener {

		@Override
		public boolean onQueryTextChange(String text) {
			// Android calls this when navigating to a new fragment, adapter =
			// null
			if (mAdapter != null)
				mAdapter.getFilter().filter(text);
			return true;
		}

		@Override
		public boolean onQueryTextSubmit(String query) {
			mAdapter.getFilter().filter(query);
			return true;
		}

		@Override
		public boolean onClose() {
			return false;
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.menu_db_refresh:
			mUriData.removeFromCache(getActivity());
			queryServer(true);
			return true;
		}
		return false;
	}

	private void navigateBack() {
		((DBActivity) getActivity()).popFragment();
	}

	@Override
	public void onDataReceived(int type, Object[] data) {
		if (!isAdded())
			return;

		cancelDialog();

		if (data == null) {
			Toast.makeText(getActivity(), "Oops! There was an error",
					Toast.LENGTH_SHORT).show();
			mListView.setAdapter(null);
			return;
		}

		mAdapter = new ListableAdapter(getActivity(), data);
		mListView.setAdapter(mAdapter);
	}

	@Override
	public void onPause() {
		cancelDialog();

		// SearchView is so crappy that invalidateOptionsMenu will
		// not remove the keyboard, we have to use this "hack"
		// The null check is because the user could presses back very quickly
		if (mSearchView != null) {
			mSearchView.setQuery("", false);
			mSearchView.clearFocus();
		}

		super.onPause();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Listable item = (Listable) mListView.getAdapter().getItem(position);
		if (item.getType() == UriData.TYPE_IR_CODE) {
			mIrManager.transmit(((IrCode) item).getSignal());
		} else {
			UriData clone = mUriData.clone();
			select(clone, item);
			((DBActivity) getActivity()).addFragment(clone);
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		Listable item = (Listable) mListView.getAdapter().getItem(position);
		if (item.getType() == UriData.TYPE_IR_CODE) {
			// TODO
		}
		return false;
	}

	public static void select(UriData data, Listable listable) {

		data.targetType = UriData.TYPE_MANUFACTURER;
		if (listable != null) {
			if (listable.getType() == UriData.TYPE_MANUFACTURER) {
				data.manufacturer = (Manufacturer) listable;
				data.targetType = UriData.TYPE_DEVICE_TYPE;
			} else if (listable.getType() == UriData.TYPE_DEVICE_TYPE) {
				data.deviceType = (DeviceType) listable;
				data.targetType = UriData.TYPE_CODESET;
			} else if (listable.getType() == UriData.TYPE_CODESET) {
				data.codeset = (Codeset) listable;
				data.targetType = UriData.TYPE_IR_CODE;
			}
		}
	}
}
