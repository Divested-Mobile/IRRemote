package org.twinone.irremote;

import java.util.List;

import org.twinone.irremote.ui.SelectRemoteListView;
import org.twinone.irremote.ui.SelectRemoteListView.OnSelectListener;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

/**
 * Fragment used for managing interactions for and presentation of a navigation
 * drawer. See the <a href=
 * "https://developer.android.com/design/patterns/navigation-drawer.html#Interaction"
 * > design guidelines</a> for a complete explanation of the behaviors
 * implemented here.
 */
public class NavFragment extends Fragment {

	/**
	 * Per the design guidelines, you should show the drawer on launch until the
	 * user manually expands it. This shared preference tracks this.
	 */
	private static final String PREF_KEY_USER_LEARNED_DRAWER = "navigation_drawer_learned";

	private static final String PREF_FILENAME = "nav";
	// Keep track of the user's last selected remote
	private static final String PREF_KEY_LAST_REMOTE = "org.twinone.irremote.pref.key.save_remote_name";

	/**
	 * Helper component that ties the action bar to the navigation drawer.
	 */
	private ActionBarDrawerToggle mDrawerToggle;

	private DrawerLayout mDrawerLayout;
	private SelectRemoteListView mRemotesListView;
	private View mFragmentContainerView;

	private boolean mFromSavedInstanceState;
	private boolean mUserLearnedDrawer;

	public NavFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Read in the flag indicating whether or not the user has demonstrated
		// awareness of the
		// drawer. See PREF_USER_LEARNED_DRAWER for details.
		mUserLearnedDrawer = getPreferences().getBoolean(
				PREF_KEY_USER_LEARNED_DRAWER, false);
	}

	private SharedPreferences getPreferences() {
		return getActivity().getSharedPreferences(PREF_FILENAME,
				Context.MODE_PRIVATE);
	}

	public void update() {
		mRemotesListView.updateRemotesList();

		// select the appropriate remote
		if (getPreferences().contains(PREF_KEY_LAST_REMOTE)) {
			mRemotesListView.selectRemote(getPreferences().getString(
					PREF_KEY_LAST_REMOTE, null));
		} else {
			List<String> names = Remote.getNames(getActivity());
			if (names.size() > 0) {
				mRemotesListView.selectRemote(0);
			}
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
		if (!(getActivity() instanceof OnSelectListener)) {
			throw new ClassCastException(
					"Activity should implement SelectRemoteListView.OnSelectListener");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRemotesListView = (SelectRemoteListView) inflater.inflate(
				R.layout.fragment_nav, container, false);
		mRemotesListView.setShowAddRemote(true);
		mRemotesListView.setOnSelectListener((OnSelectListener) getActivity());

		return mRemotesListView;
	}

	public void select(int position) {
		mRemotesListView.selectRemote(position);
		String name = mRemotesListView.getSelectedRemoteName();
		getPreferences().edit().putString(PREF_KEY_LAST_REMOTE, name).apply();
	}

	public boolean isDrawerOpen() {
		return mDrawerLayout != null
				&& mDrawerLayout.isDrawerOpen(mFragmentContainerView);
	}

	public void setUp(int fragmentId, DrawerLayout drawerLayout) {
		mFragmentContainerView = getActivity().findViewById(fragmentId);
		mDrawerLayout = drawerLayout;

		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout,
				R.drawable.ic_drawer, 0, 0) {
			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
				if (!isAdded()) {
					return;
				}

				getActivity().invalidateOptionsMenu();
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				if (!isAdded()) {
					return;
				}

				if (!mUserLearnedDrawer) {
					mUserLearnedDrawer = true;

					getPreferences().edit()
							.putBoolean(PREF_KEY_USER_LEARNED_DRAWER, true)
							.commit();
				}

				getActivity().invalidateOptionsMenu();
			}
		};

		if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
			mDrawerLayout.openDrawer(mFragmentContainerView);
		}

		mDrawerLayout.post(new Runnable() {
			@Override
			public void run() {
				mDrawerToggle.syncState();
			}
		});

		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

	public void open() {
		mDrawerLayout.openDrawer(mFragmentContainerView);
	}

	public void close() {
		mDrawerLayout.closeDrawer(mFragmentContainerView);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Forward the new configuration the drawer toggle component.
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// if (mDrawerLayout != null && isDrawerOpen()) {
		// showGlobalContextActionBar();
		// }
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Options for nav fragment here...

		return super.onOptionsItemSelected(item);
	}

	private void showGlobalContextActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setTitle(getActivity().getTitle());
	}

	private ActionBar getActionBar() {
		return ((ActionBarActivity) getActivity()).getActionBar();
	}

	public String getSelectedRemoteName() {
		return mRemotesListView.getSelectedRemoteName();
	}

	public int getSelectedRemotePosition() {
		return mRemotesListView.getSelectedItemPosition();
	}

}
