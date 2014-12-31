package org.twinone.irremote.providers;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnCloseListener;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import org.twinone.irremote.R;

public abstract class ProviderFragment extends Fragment {

    protected static final String ACTION_SAVE_REMOTE = ProviderActivity.ACTION_SAVE_REMOTE;
    protected static final String ACTION_GET_BUTTON = ProviderActivity.ACTION_GET_BUTTON;
    protected static final String EXTRA_RESULT_BUTTON = ProviderActivity.EXTRA_RESULT_BUTTON;
    protected ListView mListView;
    protected ListableAdapter mAdapter;
    protected SearchView mSearchView;
    private MySearchViewListener mSearchViewListener;
    private MenuItem mSearchMenuItem;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof ProviderActivity)) {
            throw new ClassCastException(
                    "ProviderFragment should be attached to a ProviderActivity");
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

    protected void setupSearchView(Menu menu) {
        mSearchMenuItem = menu.findItem(R.id.menu_db_search);
        mSearchView = (SearchView) mSearchMenuItem.getActionView();
        // mSearchView = (SearchView) MenuItemCompat
        // .getActionView(mSearchMenuItem);
        mSearchViewListener = new MySearchViewListener();
        mSearchView.setOnQueryTextListener(mSearchViewListener);
        mSearchView.setOnCloseListener(mSearchViewListener);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }

    private class MySearchViewListener implements OnQueryTextListener,
            OnCloseListener {

        @Override
        public boolean onQueryTextChange(String text) {
            if (mAdapter != null)
                mAdapter.getFilter().filter(text);
            return true;
        }

        @Override
        public boolean onQueryTextSubmit(String query) {
            if (mAdapter != null)
                mAdapter.getFilter().filter(query);
            return true;
        }

        @Override
        public boolean onClose() {
            return false;
        }

    }

    private AlertDialog mLoadingDialog;
    protected void showLoadingDialog() {
        hideLoadingDialog();
        AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
        ab.setCancelable(false);
        ab.setTitle(R.string.loading);
        ab.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                onCancelLoading();
                getActivity().onNavigateUp();
            }
        });
        mLoadingDialog = ab.create();
        mLoadingDialog.show();
    }
    protected void hideLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.cancel();
        }
        mLoadingDialog = null;
    }
    protected void onCancelLoading() {

    }

}
