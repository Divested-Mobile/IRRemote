package org.twinone.irremote.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.twinone.androidlib.NavigationFragment;
import org.twinone.irremote.R;
import org.twinone.irremote.components.Remote;
import org.twinone.irremote.ui.SelectRemoteListView.OnRemoteSelectedListener;

import java.util.List;

public class MainNavFragment extends NavigationFragment implements
        OnRemoteSelectedListener {

    // private static final String PREF_FILENAME = "nav";
    // Keep track of the user's last selected remote
    public static final String PREF_KEY_LAST_REMOTE = "org.twinone.irremote.pref.key.save_remote_name";

    private DrawerLayout mDrawerLayout;
    private SelectRemoteListView mRemotesListView;
    private View mFragmentContainerView;
    private TextView mInfoTextView;
    // -1 when none selected
    private int mTargetRemotePosition = -1;
    private boolean mAddRemoteSelected;

    public MainNavFragment() {
    }


    public void update() {
        mRemotesListView.updateRemotesList();

        // select the appropriate remote
        List<String> names = Remote.getNames(getActivity());
        String lastSelectedRemotePref = Remote
                .getPersistedRemoteName(getActivity());
        if (names.contains(lastSelectedRemotePref)) {
            mRemotesListView.selectRemote(lastSelectedRemotePref, false);
        } else if (names.size() > 0) {
            mRemotesListView.selectRemote(0, false);
        } else {
            mRemotesListView.selectRemote(-1, false);
        }
        updateTitle();
        updateInfoTextView();
    }

    private void updateInfoTextView() {
        if (mRemotesListView.getCount() == 0) {
            mInfoTextView.setVisibility(View.VISIBLE);
        } else {
            mInfoTextView.setVisibility(View.GONE);
        }
    }

    void updateTitle() {
        Log.d("", "isOpen: " + isOpen());
        if (!isOpen()) {
            getSupportActionBar().setTitle(getSelectedRemoteName());
        } else {
            getSupportActionBar().setTitle(R.string.my_remotes);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(getActivity() instanceof OnRemoteSelectedListener)) {
            throw new ClassCastException(
                    "Activity should implement SelectRemoteListView.OnSelectListener");
        }
    }

    @Override
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        super.setUp(fragmentId, drawerLayout);
        update();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RelativeLayout root = (RelativeLayout) inflater.inflate(
                R.layout.fragment_nav_main, container, false);

        mRemotesListView = (SelectRemoteListView) root
                .findViewById(R.id.select_remote_listview);

        mRemotesListView.setShowAddRemote(false);
        mRemotesListView.setOnSelectListener(this);
        mInfoTextView = (TextView) root
                .findViewById(R.id.select_remote_empty_info);
        updateInfoTextView();
        return root;
    }

    /**
     * This will select the remote in the list, it will also make a call to the
     * listener
     *
     * @param position
     */
    public void select(int position) {
        mRemotesListView.selectRemote(position, true);
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null
                && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    public String getSelectedRemoteName() {
        return mRemotesListView.getSelectedRemoteName();
    }

    public int getSelectedRemotePosition() {
        return mRemotesListView.getSelectedItemPosition();
    }

    @Override
    public void onRemoteSelected(int position, String remoteName) {
        Remote.setLastUsedRemoteName(getActivity(), remoteName);
        mTargetRemotePosition = position;
        close();

    }

    @Override
    public void onAddRemoteSelected() {
        mAddRemoteSelected = true;
        close();
    }

    @Override
    protected void onOpen() {
        updateTitle();
        ((MainActivity) getActivity()).showAddRemoteButton();
    }

    @Override
    protected void onClose() {
        // We should provide navigation after the drawer has been closed,
        // because of animations
        if (mTargetRemotePosition != -1) {
            ((OnRemoteSelectedListener) getActivity()).onRemoteSelected(
                    mTargetRemotePosition,
                    mRemotesListView.getRemoteName(mTargetRemotePosition));
            mTargetRemotePosition = -1;
        } else if (mAddRemoteSelected) {
            ((OnRemoteSelectedListener) getActivity()).onAddRemoteSelected();
            mAddRemoteSelected = false;
        }

        updateTitle();
        ((MainActivity) getActivity()).hideAddRemoteButton();

    }

}
