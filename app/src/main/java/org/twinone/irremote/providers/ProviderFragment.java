package org.twinone.irremote.providers;

import android.app.Activity;
import android.app.Fragment;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.SearchView.OnCloseListener;
import androidx.appcompat.widget.SearchView.OnQueryTextListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import com.afollestad.materialdialogs.MaterialDialog;

import org.twinone.irremote.R;
import org.twinone.irremote.compat.Compat;

public abstract class ProviderFragment extends Fragment {

    protected static final String ACTION_SAVE_REMOTE = DefaultProviderActivity.ACTION_SAVE_REMOTE;
    protected static final String ACTION_GET_BUTTON = DefaultProviderActivity.ACTION_GET_BUTTON;
    protected static final String EXTRA_RESULT_BUTTON = DefaultProviderActivity.EXTRA_RESULT_BUTTON;
    protected ListView mListView;
    protected ListableAdapter mAdapter;
    protected SearchView mSearchView;
    private MySearchViewListener mSearchViewListener;
    private MenuItem mSearchMenuItem;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof DefaultProviderActivity)) {
            throw new ClassCastException(
                    "ProviderFragment should be attached to a DefaultProviderActivity");
        }
    }

    protected void setCurrentState(int state) {
        getProvider().setCurrentState(state);
    }

    protected void setExitState(int state) {
        getProvider().setExitState(state);
    }

    protected DefaultProviderActivity getProvider() {
        return (DefaultProviderActivity) getActivity();
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

    private MaterialDialog mLoadingDialog;

    protected void showLoadingDialog() {
        hideLoadingDialog();
        MaterialDialog.Builder mb = Compat.getMaterialDialogBuilder(getActivity());
        mb.cancelable(false);
        mb.title(R.string.loading);
        mb.negativeText(android.R.string.cancel);
        mb.callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onNegative(MaterialDialog dialog) {
                super.onNegative(dialog);
                onCancelLoading();
                getActivity().onNavigateUp();
            }
        });
        mLoadingDialog = mb.show();
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
