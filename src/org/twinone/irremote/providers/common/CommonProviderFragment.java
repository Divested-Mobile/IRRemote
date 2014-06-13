package org.twinone.irremote.providers.common;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import org.twinone.irremote.Button;
import org.twinone.irremote.ButtonUtils;
import org.twinone.irremote.FileUtils;
import org.twinone.irremote.R;
import org.twinone.irremote.Remote;
import org.twinone.irremote.ir.Signal;
import org.twinone.irremote.providers.BaseListable;
import org.twinone.irremote.providers.BaseProviderFragment;
import org.twinone.irremote.providers.ListableAdapter;
import org.twinone.irremote.providers.globalcache.GCProviderActivity;

import android.content.Intent;
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

public class CommonProviderFragment extends BaseProviderFragment implements
		OnItemClickListener, OnItemLongClickListener {

	private static final String COMMON_TV_NAME = "TV";
	private static final String COMMON_BLURAY_NAME = "BluRay";
	private static final String COMMON_CABLE_NAME = "Cable";
	private static final String COMMON_AUDIO_AMPLIFIER = "Audio";

	private ListView mListView;
	private ListableAdapter mAdapter;

	public static final String ARG_DATA = "arg.data";
	private Data mTarget;

	public static class Data implements Serializable {

		public Data() {
			targetType = TARGET_DEVICE_TYPE;
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = -1889643026103427356L;

		public static final int TARGET_DEVICE_TYPE = 0;
		public static final int TARGET_DEVICE_NAME = 1;
		public static final int TARGET_IR_CODE = 2;

		int targetType;
		String deviceType;
		String deviceName;

		public Data clone() {
			Data d = new Data();
			d.targetType = targetType;
			d.deviceType = deviceType;
			d.deviceName = deviceName;
			return d;
		}

	}

	private int getDeviceTypeInt(String deviceType) {
		if (COMMON_TV_NAME.equals(deviceType)) {
			return Remote.DEVICE_TYPE_TV;
		}
		if (COMMON_CABLE_NAME.equals(deviceType)) {
			return Remote.DEVICE_TYPE_CABLE;
		}
		if (COMMON_BLURAY_NAME.equals(deviceType)) {
			return Remote.DEVICE_TYPE_BLURAY;
		}
		if (COMMON_AUDIO_AMPLIFIER.equals(deviceType)) {
			return Remote.DEVICE_TYPE_AUDIO_AMPLIFIER;
		}
		throw new IllegalArgumentException("WTF, no such type" + deviceType);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null && getArguments().containsKey(ARG_DATA)) {
			mTarget = (Data) getArguments().getSerializable(ARG_DATA);
			Log.d("", "mTarget.deviceType = " + mTarget.deviceType);
		} else {
			mTarget = new Data();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		setHasOptionsMenu(true);

		// For navigation
		setCurrentType(mTarget.targetType);

		ViewGroup rootView = (ViewGroup) inflater.inflate(
				R.layout.fragment_listable, container, false);

		mListView = (ListView) rootView.findViewById(R.id.lvElements);
		mListView.setOnItemClickListener(this);
		mListView.setOnItemLongClickListener(this);

		mAdapter = new ListableAdapter(getActivity(), getItems(getDBPath()));
		mListView.setAdapter(mAdapter);

		String name = getDataName(" > ");
		if (name == null) {
			getActivity().setTitle(R.string.db_select_device_type);
		} else {
			getActivity().setTitle(name);
		}
		return rootView;
	}

	private String getDBPath() {
		final String name = getDataName(File.separator);
		if (name == null) {
			return "db";
		}
		return "db" + File.separator + name;
	}

	private String getDataName(String separator) {
		StringBuilder path = new StringBuilder();
		if (mTarget.deviceType == null)
			return null;
		path.append(mTarget.deviceType);
		if (mTarget.deviceName == null)
			return path.toString();
		path.append(separator).append(mTarget.deviceName);
		return path.toString();
	}

	@SuppressWarnings("serial")
	private class MyListable extends BaseListable {

		public MyListable(String text) {
			this.text = text;
		}

		private String text;

		@Override
		public String getDisplayName() {
			return text;
		}

	}

	private MyListable[] getItems(String path) {
		ArrayList<MyListable> items = new ArrayList<MyListable>();
		for (String s : listAssets(path)) {
			items.add(new MyListable(s));
		}
		return items.toArray(new MyListable[items.size()]);
	}

	private String[] listAssets(String path) {
		try {
			return getActivity().getAssets().list(path);
		} catch (Exception e) {
			return new String[] {};
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.common_menu_todb:
			Intent i = new Intent(getActivity(), GCProviderActivity.class);
			startActivity(i);
			getActivity().finish();
		}
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		BaseListable item = (BaseListable) mListView.getAdapter().getItem(
				position);
		if (mTarget.targetType == Data.TARGET_DEVICE_TYPE) {
			Data clone = mTarget.clone();
			clone.deviceType = item.getDisplayName();
			clone.targetType = Data.TARGET_DEVICE_NAME;
			((CommonProviderActivity) getActivity()).addFragment(clone);
		} else {
			mTarget.targetType = Data.TARGET_IR_CODE;
			mTarget.deviceName = item.getDisplayName();
			saveRemote();
		}
	}

	private void saveRemote() {
		Remote r = new Remote();
		r.name = mTarget.deviceName + " " + mTarget.deviceType;
		final String remotedir = getDBPath();
		for (String name : listAssets(getDBPath())) {
			Button b = new Button();
			b.code = FileUtils.read(getActivity().getAssets(), remotedir
					+ File.separator + name);
			b.id = Integer.parseInt(name.substring(2).split("\\.")[0]);
			b.format = Signal.FORMAT_AUTO;
			b.common = true;
			b.text = ButtonUtils
					.getCommonButtonDisplyaName(b.id, getActivity());
			r.addButton(b);
		}
		r.options.type = getDeviceTypeInt(mTarget.deviceType);
		getProvider().save(r);
	}

	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		mListView.setItemChecked(position, true);
		return true;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.common_menu, menu);
	}

}
