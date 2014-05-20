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
package org.twinone.irremote;

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

import org.twinone.irremote.R;
import org.twinone.irremote.globalcache.DBConnector;
import org.twinone.irremote.globalcache.DBConnector.UriData;
import org.twinone.irremote.ir.IRManager;
import org.twinone.irremote.ir.Signal;

public class DBFragment extends Fragment implements DBConnector.Listener,
		OnItemClickListener {

	private static final String TAG = "MainFragment";

	public static final String ARG_URI_DATA = "com.twinone.irremote.arg.uri_data";

	private ListView mListView;

	private DBConnector mConnector;
	private IRManager mIrManager;

	public DBFragment() {
	}

	private AlertDialog mDialog;
	private ListableAdapter mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		setHasOptionsMenu(true);
		View rootView = inflater.inflate(R.layout.fragment_listable, container,
				false);

		mListView = (ListView) rootView.findViewById(R.id.lvElements);
		mListView.setOnItemClickListener(this);

		mIrManager = new IRManager(getActivity());

		if (mAdapter != null) {
			mListView.setAdapter(mAdapter);
		} else {
			UriData data = new UriData();
			if (getArguments() != null) {
				hasBackButton = true;
				data = (UriData) getArguments().getSerializable(ARG_URI_DATA);
			}

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

			mConnector = new DBConnector(getActivity(), this);
			mConnector.setOnDataReceivedListener(this);
			mConnector.query(data);
		}
		return rootView;
	}

	private boolean hasBackButton = false;

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		Log.d(TAG, "onCreateOptionsMenu");
		super.onCreateOptionsMenu(menu, inflater);

		inflater.inflate(R.menu.db_fragment, menu);
		menu.findItem(R.id.menu_db_back).setVisible(hasBackButton);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_db_back) {
			navigateBack();
			return true;
		}
		return false;
	}

	private void navigateBack() {
		((DBActivity) getActivity()).popFragment();
	}

	@Override
	public void onReceiveData(int type, Object[] data) {
		Log.d(TAG, "onReceive data: Fragment ID =" + this.hashCode());
		if (!isAdded())
			return;

		if (data == null) {
			Toast.makeText(getActivity(), "Oops! There was an error",
					Toast.LENGTH_SHORT).show();
			return;
		}

		if (mDialog != null && mDialog.isShowing()) {
			mDialog.cancel();
		}
		mAdapter = new ListableAdapter(getActivity(), data);
		mListView.setAdapter(mAdapter);
	}

	@Override
	public void onPause() {
		if (mDialog != null && mDialog.isShowing()) {
			mDialog.cancel();
		}
	
		super.onPause();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Listable item = (Listable) mListView.getAdapter().getItem(position);
		if (item.getType() == DBConnector.TYPE_IR_CODE) {
			mIrManager.transmit((Signal) item.getData());
		} else {
			mConnector.select(item);
			((DBActivity) getActivity()).addFragment(mConnector.getQueryData());
			// mConnector.getNextList(item);
		}
	}

}
