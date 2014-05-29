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

import org.twinone.irremote.Button;
import org.twinone.irremote.DBActivity;
import org.twinone.irremote.Listable;
import org.twinone.irremote.ListableAdapter;
import org.twinone.irremote.R;
import org.twinone.irremote.Remote;
import org.twinone.irremote.SaveButtonDialogFragment;
import org.twinone.irremote.SaveRemoteDialogFragment;
import org.twinone.irremote.SaveRemoteDialogFragment.OnRemoteSavedListener;
import org.twinone.irremote.ir.IRTransmitter;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
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

	public static final String ARG_URI_DATA = "com.twinone.irremote.arg.uri_data";

	private ListView mListView;

	private DBConnector mConnector;

	private boolean mCreated;
	private AlertDialog mDialog;
	private ListableAdapter mAdapter;
	private IRTransmitter mTransmitter;

	private UriData mUriData;

	public DBFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments() != null) {
			mUriData = (UriData) getArguments().getSerializable(ARG_URI_DATA);
		} else {
			mUriData = new UriData();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mTransmitter = new IRTransmitter(getActivity());
		mTransmitter.setShowBlinker(true);

		setHasOptionsMenu(true);
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(
				mUriData.targetType != UriData.TYPE_MANUFACTURER);

		View rootView = inflater.inflate(R.layout.fragment_listable, container,
				false);
		mListView = (ListView) rootView.findViewById(R.id.lvElements);
		mListView.setOnItemClickListener(this);
		mListView.setOnItemLongClickListener(this);

		mConnector = new DBConnector(getActivity(), this);

		// Adapter stuff
		if (mCreated) {
			mListView.setAdapter(mAdapter);
			mAdapter.restoreOriginalDataSet();
		} else if (mUriData.isAvailableInCache(getActivity())) {
			queryServer(false);
		} else {
			queryServer(true);
		}

		String title = mUriData.getFullyQualifiedName(" > ");
		if (title == null) {
			title = getString(R.string.db_select_manufacturer);
		}
		getActivity().setTitle(title);

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
		inflater.inflate(R.menu.db_menu, menu);
		mSearchMenuItem = (MenuItem) menu.findItem(R.id.menu_db_search);
		mSearchView = (SearchView) mSearchMenuItem.getActionView();
		mSearchViewListener = new MySearchViewListener();
		mSearchView.setOnQueryTextListener(mSearchViewListener);
		mSearchView.setOnCloseListener(mSearchViewListener);
		mSearchView.setQueryHint(getSearchHint(mUriData));

		if (mUriData.targetType == UriData.TYPE_IR_CODE) {
			menu.findItem(R.id.menu_db_save).setVisible(true);
		}

	}

	private String getSearchHint(UriData data) {
		if (data.targetType == UriData.TYPE_MANUFACTURER) {
			return getString(R.string.search_hint_manufacturers);
		} else if (data.targetType == UriData.TYPE_IR_CODE) {
			return getString(R.string.search_hint_buttons);
		} else {
			return getString(R.string.search_hint_custom,
					data.getFullyQualifiedName(" "));
		}
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
		case R.id.menu_db_save:
			if (mData != null) {
				Remote remote = IrCode.toRemote(
						mUriData.manufacturer.Manufacturer + " "
								+ mUriData.deviceType.DeviceType,
						(IrCode[]) mData);
				SaveRemoteDialogFragment dialog = SaveRemoteDialogFragment
						.newInstance(remote);
				dialog.setListener(new OnRemoteSavedListener() {

					@Override
					public void onRemoteSaved(String name) {
						// Finish the activity, we've saved the remote
						getActivity().finish();
						Toast.makeText(getActivity(),
								R.string.remote_saved_toast, Toast.LENGTH_SHORT);
					}
				});
				dialog.show(getActivity());
			}
			return true;
		}
		return false;
	}

	private void navigateBack() {
		((DBActivity) getActivity()).popFragment();
	}

	private Object[] mData;

	@Override
	public void onDataReceived(int type, Object[] data) {
		if (!isAdded())
			return;

		cancelDialog();
		mData = data;
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
		mTransmitter.pause();

		super.onPause();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Listable item = (Listable) mListView.getAdapter().getItem(position);
		if (item.getType() == UriData.TYPE_IR_CODE) {
			mTransmitter.transmit(((IrCode) item).getSignal());
		} else {
			UriData clone = mUriData.clone();
			select(clone, item);
			((DBActivity) getActivity()).addFragment(clone);
		}
	}

	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		mListView.setItemChecked(position, true);
		if (mUriData.targetType == UriData.TYPE_IR_CODE) {
			// When the user long presses the button he can save it
			Button b = IrCode.toButton((IrCode) mData[position]);
			SaveButtonDialogFragment.showFor(getActivity(), b);
		}
		return true;
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
