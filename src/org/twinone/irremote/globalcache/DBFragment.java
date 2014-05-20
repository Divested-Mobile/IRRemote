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
import org.twinone.irremote.ir.Signal;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class DBFragment extends Fragment implements DBConnector.Listener,
		OnItemClickListener {

	private static final String TAG = "DBFragment";

	public static final String ARG_URI_DATA = "com.twinone.irremote.arg.uri_data";

	private ListView mListView;

	private DBConnector mConnector;
	private IRManager mIrManager;

	private int mTitle = R.string.db_select_manufacturer;
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
			mMenuBackButtonEnabled = true;
			mUriData = (UriData) getArguments().getSerializable(ARG_URI_DATA);
			switch (mUriData.target) {
			case UriData.TYPE_DEVICE_TYPE:
				mTitle = R.string.db_select_device_type;
				break;
			case UriData.TYPE_CODESET:
				mTitle = R.string.db_select_codeset;
				break;
			case UriData.TYPE_IR_CODE:
				mTitle = R.string.db_select_ir;
				break;
			}
		} else {
			Log.w(TAG, "CREATING NEW URI DATA");
			mUriData = new UriData();
			mTitle = R.string.db_select_manufacturer;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		setHasOptionsMenu(true);

		View rootView = inflater.inflate(R.layout.fragment_listable, container,
				false);

		mListView = (ListView) rootView.findViewById(R.id.lvElements);
		mListView.setOnItemClickListener(this);

		mConnector = new DBConnector(getActivity(), this);

		// Adapter stuff
		if (mCreated) {
			mListView.setAdapter(mAdapter);
		} else if (mUriData.isAvailableInCache(getActivity())) {
			queryServer(false);
		} else {
			queryServer(true);
		}

		getActivity().setTitle(mTitle);
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

	private boolean mMenuBackButtonEnabled = false;

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);

		inflater.inflate(R.menu.db_fragment, menu);
		menu.findItem(R.id.menu_db_back).setVisible(mMenuBackButtonEnabled);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
//		Log.d(TAG, "mUriData: " + mUriData.toString());

		switch (item.getItemId()) {
		case R.id.menu_db_back:
			navigateBack();
			return true;
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
	public void onReceiveData(int type, Object[] data) {
		if (!isAdded())
			return;

		cancelDialog();

		if (data == null) {
			Toast.makeText(getActivity(), "Oops! There was an error",
					Toast.LENGTH_SHORT).show();
			return;
		}

		mAdapter = new ListableAdapter(getActivity(), data);
		mListView.setAdapter(mAdapter);
	}

	@Override
	public void onPause() {
		cancelDialog();
		super.onPause();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Listable item = (Listable) mListView.getAdapter().getItem(position);
		if (item.getType() == UriData.TYPE_IR_CODE) {
			mIrManager.transmit((Signal) item.getData());
		} else {
			UriData clone = mUriData.clone();
			select(clone, item);
			((DBActivity) getActivity()).addFragment(clone);
		}
	}

	public static void select(UriData data, Listable listable) {
		data.target = UriData.TYPE_MANUFACTURER;
		if (listable != null) {
			if (listable.getType() == UriData.TYPE_MANUFACTURER) {
				data.manufacturer = (Manufacturer) listable;
				data.target = UriData.TYPE_DEVICE_TYPE;
			} else if (listable.getType() == UriData.TYPE_DEVICE_TYPE) {
				data.deviceType = (DeviceType) listable;
				data.target = UriData.TYPE_CODESET;
			} else if (listable.getType() == UriData.TYPE_CODESET) {
				data.codeset = (Codeset) listable;
				data.target = UriData.TYPE_IR_CODE;
			}
		}
	}

}
