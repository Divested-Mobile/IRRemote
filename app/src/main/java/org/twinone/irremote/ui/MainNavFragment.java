package org.twinone.irremote.ui;

import android.app.Activity;
import android.os.Bundle;
import androidx.drawerlayout.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.twinone.androidlib.NavigationFragment;
import org.twinone.irremote.R;
import org.twinone.irremote.components.Remote;

import java.util.List;

public class MainNavFragment extends NavigationFragment {

    // private static final String PREF_FILENAME = "nav";
    // Keep track of the user's last selected menu_main
    public static final String PREF_KEY_LAST_REMOTE = "org.twinone.irremote.pref.key.save_remote_name";


    private SelectRemoteListView mRemotesListView;
    private View mFragmentContainerView;
    private TextView mInfoTextView;
    // -1 when none selected
    private int mTargetRemotePosition = -1;

    public MainNavFragment() {
    }


    public void update() {
        mRemotesListView.updateRemotesList();

        // select the appropriate menu_main
        List<String> names = Remote.getNames(getActivity());
        String lastSelectedRemotePref = Remote
                .getPersistedRemoteName(getActivity());
        if (names.contains(lastSelectedRemotePref)) {
            mRemotesListView.selectRemote(lastSelectedRemotePref);
        } else if (!names.isEmpty()) {
            mRemotesListView.selectRemote(0);
        } else {
//            mRemotesListView.selectRemote(-1, false);
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
        if (!(getActivity() instanceof MainActivity)) {
            throw new ClassCastException(
                    "MainNavFragment should be attached to MainActivity");
        }
    }


    int mDrawerOffset;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RelativeLayout root = (RelativeLayout) inflater.inflate(
                R.layout.fragment_nav_main, container, false);

        mRemotesListView = (SelectRemoteListView) root
                .findViewById(R.id.select_remote_listview);
        mRemotesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onRemoteSelected(position);
            }

        });
        mInfoTextView = (TextView) root
                .findViewById(R.id.select_remote_empty_info);
        updateInfoTextView();
        return root;
    }

    private void onRemoteSelected(int position) {
        String remoteName = mRemotesListView.getRemoteName(position);
        Remote.setLastUsedRemoteName(getActivity(), remoteName);
        mTargetRemotePosition = position;
        close();
    }

    /**
     * This will select the menu_main in the list, it will also make a call to the
     * listener
     *
     * @param position
     */
    public void selectRemote(int position) {
        mRemotesListView.selectRemote(position);
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
    protected void onOpen() {
        updateTitle();
        getMainActivity().showAddRemoteButton();
    }

    private MainActivity getMainActivity() {
        return (MainActivity) getActivity();
    }

    @Override
    protected void onClose() {
        // We should provide navigation after the drawer has been closed,
        // because of animations
        if (mTargetRemotePosition != -1) {
            String name = mRemotesListView.getSelectedRemoteName();
            getMainActivity().setRemote(name);
            mTargetRemotePosition = -1;
        }
        updateTitle();
        getMainActivity().hideAddRemoteButton();
    }

}
