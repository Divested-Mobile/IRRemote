package org.twinone.irremote.providers.common;

import org.twinone.irremote.R;
import org.twinone.irremote.ui.BaseListable;
import org.twinone.irremote.ui.ListableAdapter;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

public class CommonProviderFragment extends Fragment implements
		OnItemClickListener, OnItemLongClickListener {

	private ListView mListView;
	private ListableAdapter mAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		setHasOptionsMenu(true);

		View rootView = inflater.inflate(R.layout.fragment_listable, container,
				false);
		mListView = (ListView) rootView.findViewById(R.id.lvElements);
		mListView.setOnItemClickListener(this);
		mListView.setOnItemLongClickListener(this);

		mAdapter = new ListableAdapter(getActivity(), getItems());

		return rootView;
	}

	private String[] getItems() {
		return new String[] { "Sams", "Phil" };
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_db_save:
		}
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		BaseListable item = (BaseListable) mListView.getAdapter().getItem(position);

	}

	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		mListView.setItemChecked(position, true);
		return true;
	}

}
