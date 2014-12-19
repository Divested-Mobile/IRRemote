package org.twinone.irremote.providers;

import org.twinone.irremote.R;

import android.app.Activity;
import android.app.Fragment;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnCloseListener;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

public abstract class ProviderFragment extends Fragment {

	protected ListView mListView;

	protected ListableAdapter mAdapter;

	protected static final String ACTION_SAVE_REMOTE = ProviderActivity.ACTION_SAVE_REMOTE;
	protected static final String ACTION_GET_BUTTON = ProviderActivity.ACTION_GET_BUTTON;
	protected static final String EXTRA_RESULT_BUTTON = ProviderActivity.EXTRA_RESULT_BUTTON;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (!(activity instanceof ProviderActivity)) {
			throw new ClassCastException(
					"BaseProviderFragment should be attached to a BaseProviderActivity");
		}
	}

	protected void setCurrentState(int state) {
		getProvider().setCurrentState(state);
	}

	protected void setExitState(int state) {
		getProvider().setExitState(state);
	}

	protected ProviderActivity getProvider() {
		return (ProviderActivity) getActivity();
	}

	protected MySearchViewListener mSearchViewListener;
	protected MenuItem mSearchMenuItem;

	protected SearchView mSearchView;

	public void setupSearchView(Menu menu, MenuInflater inflater) {
		mSearchMenuItem = (MenuItem) menu.findItem(R.id.menu_db_search);
		mSearchView = (SearchView) mSearchMenuItem.getActionView();
		// mSearchView = (SearchView) MenuItemCompat
		// .getActionView(mSearchMenuItem);
		mSearchViewListener = new MySearchViewListener();
		mSearchView.setOnQueryTextListener(mSearchViewListener);
		mSearchView.setOnCloseListener(mSearchViewListener);
	}

	protected class MySearchViewListener implements OnQueryTextListener,
			OnCloseListener {

		@Override
		public boolean onQueryTextChange(String text) {
			// Android calls this when navigating to a new fragment, adapter =
			// null
			Log.d("", "test");
			if (mAdapter != null)
				mAdapter.getFilter().filter(text);
			return true;
		}

		@Override
		public boolean onQueryTextSubmit(String query) {
			// Android calls this when navigating to a new fragment, adapter =
			// null
			Log.d("", "test");

			if (mAdapter != null)
				mAdapter.getFilter().filter(query);
			return true;
		}

		@Override
		public boolean onClose() {
			return false;
		}

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		menu.clear();
	}

}
