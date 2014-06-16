package org.twinone.irremote.providers;

import org.twinone.irremote.R;

import android.app.Activity;
import android.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.SearchView.OnCloseListener;
import android.widget.SearchView.OnQueryTextListener;

public class BaseProviderFragment extends Fragment {

	protected ListableAdapter mAdapter;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (!(activity instanceof BaseProviderActivity)) {
			throw new ClassCastException(
					"BaseProviderFragment should be attached to a BaseProviderActivity");
		}
	}

	protected void setCurrentType(int type) {
		getProvider().mCurrentType = type;
	}

	protected BaseProviderActivity getProvider() {
		return (BaseProviderActivity) getActivity();
	}

	protected MySearchViewListener mSearchViewListener;
	protected MenuItem mSearchMenuItem;
	protected SearchView mSearchView;

	public void prepareSearch(Menu menu, MenuInflater inflater) {
		mSearchMenuItem = (MenuItem) menu.findItem(R.id.menu_db_search);
		mSearchView = (SearchView) mSearchMenuItem.getActionView();
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

}
