package org.twinone.androidlib;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.customview.widget.ViewDragHelper;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBarDrawerToggle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.twinone.irremote.R;

import java.lang.reflect.Field;

public abstract class NavigationFragment extends Fragment {

    /**
     * Recommended edge size in dp so that any user will be easily able to open
     * the drawer
     */
    public static final int EDGE_SIZE_RECOMMENDED = 40;
    private static final String PREF_KEY_USER_LEARNED_DRAWER = "navigation_drawer_learned";
    private static final String PREF_FILENAME = "nav";
    protected DrawerLayout mDrawerLayout;

    private View mFragmentContainerView;
    private ActionBarDrawerToggle mDrawerToggle;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;
    private boolean mIsSetUp;
    private NavigationListener mListener;
    private boolean mHomeButtonLocked = false;

    public NavigationFragment() {
    }

    SharedPreferences getPreferences() {
        return getActivity().getSharedPreferences(PREF_FILENAME,
                Context.MODE_PRIVATE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserLearnedDrawer = getPreferences().getBoolean(
                PREF_KEY_USER_LEARNED_DRAWER, false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return new LinearLayout(getActivity());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
                GravityCompat.START);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout,
                0, 0) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().supportInvalidateOptionsMenu();
                onClose();
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

                getActivity().supportInvalidateOptionsMenu();
                onOpen();
            }
        };

        mDrawerLayout.addDrawerListener(mDrawerToggle);

        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            open();
        }

        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mIsSetUp = true;
    }

    public boolean isOpen() {
        return mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    public void open(boolean callListener) {
        open();
        if (callListener && mListener != null)
            mListener.onNavigationOpened();
    }

    public void close(boolean callListener) {
        close();
        if (callListener && mListener != null)
            mListener.onNavigationClosed();
    }

    void open() {
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

    // Extended functionality below

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // if (mDrawerLayout != null && isDrawerOpen()) {
        // showGlobalContextActionBar();
        // }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Only allow toggle from ActionBar title click if not locked!
        return !mHomeButtonLocked && mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    protected ActionBar getSupportActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    /**
     * Called when the navigation is opened
     */
    protected void onOpen() {
        if (mListener != null)
            mListener.onNavigationOpened();
    }

    /**
     * Called when the navigation is closed
     */
    protected void onClose() {
        if (mListener != null)
            mListener.onNavigationClosed();
    }

    public void setNavigationListener(NavigationListener listener) {
        mListener = listener;
    }

    private void setSlidingLockMode(int mode) {
        mDrawerLayout.setDrawerLockMode(mode, mFragmentContainerView);
        if (mode == DrawerLayout.LOCK_MODE_UNLOCKED) {
            mDrawerLayout.setFocusableInTouchMode(true);
        } else {
            mDrawerLayout.setFocusableInTouchMode(false);
        }
    }

    /**
     * Lock the drawer into the open state. The user will not be able to slide
     * to close
     */
    public void lockOpen(boolean alsoLockHomeButton) {
        setSlidingLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
        setHomeButtonLocked(alsoLockHomeButton);
        onOpen();
    }

    /**
     * Lock the drawer into the closed state. The user will not be able to slide
     * to open<br>
     * This will not disable the menu button
     */
    public void lockClose(boolean alsoLockHomeButton) {
        setSlidingLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        setHomeButtonLocked(alsoLockHomeButton);
        onClose();
    }

    /**
     * Unlock the drawer
     *
     * @see {@link #lockOpen()} {@link #lockClose()}
     */
    public void unlock() {
        setSlidingLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        setHomeButtonLocked(false);
    }

    /**
     * Set the lock mode for the home button in the ActionBar<br>
     * true if you want the action bar button to be ignored
     */
    void setHomeButtonLocked(boolean locked) {
        mHomeButtonLocked = locked;
        getSupportActionBar().setHomeButtonEnabled(!locked);
    }

    /**
     * @return true if the drawer is locked (the user cannot open it with his
     * finger)
     */
    public boolean isLocked() {
        return (mDrawerLayout.getDrawerLockMode(mFragmentContainerView) != DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    /**
     * Get the current edge size in dp
     *
     * @see #setLeftEdgeSizeDp(int)
     */
    public int getEdgeSizeDp() {
        try {
            Field dragger = mDrawerLayout.getClass().getDeclaredField(
                    "mLeftDragger");
            dragger.setAccessible(true);
            ViewDragHelper helper = (ViewDragHelper) dragger.get(mDrawerLayout);
            Field mEdgeSize = helper.getClass().getDeclaredField("mEdgeSize");
            mEdgeSize.setAccessible(true);
            int px = mEdgeSize.getInt(helper);
            float density = getResources().getDisplayMetrics().density;
            return (int) (px / density + 0.5f);
        } catch (Exception e) {
            throw new RuntimeException("Error getting edge size");
        }

    }

    public void setEdgeSizeDp(int dp) {
        float density = getResources().getDisplayMetrics().density;
        int px = (int) (dp * density + 0.5f);
        setEdgeSizePx(px, true);
        setEdgeSizePx(px, false);

    }

    /**
     * Set the size of the region in which the user can slide his finger to open
     * the drawer<br>
     * Android developers have super small fingers, so they recommend 20dp.<br>
     * Call me gorilla, but I fail to open in the first try with 20dp<br>
     * I suggest you use at least 100dp.
     *
     * @param dp
     * @see #EDGE_SIZE_RECOMMENDED
     */
    public void setLeftEdgeSizeDp(int dp) {
        float density = getResources().getDisplayMetrics().density;
        int px = (int) (dp * density + 0.5f);
        setEdgeSizePx(px, true);
    }

    /**
     * Set the size of the region in which the user can slide his finger to open
     * the drawer<br>
     * Android developers have super small fingers, so they recommend 20dp.<br>
     * Call me gorilla, but I fail to open in the first try with 20dp<br>
     * I suggest you use at least 100dp.<br>
     * <p/>
     * Note: This uses reflection, if you don't want the app to crash, add this
     * to your proguard-project.txt:<br>
     * <p/>
     * <pre>
     * -keepclassmembers class android.support.v4.widget.DrawerLayout {
     * 	private android.support.v4.widget.ViewDragHelper *;
     *  }
     *
     * -keepclassmembers class android.support.v4.widget.ViewDragHelper {
     * 	private int mEdgeSize;
     *  }
     * </pre>
     *
     * @param dp
     * @see #EDGE_SIZE_RECOMMENDED
     */
    private void setEdgeSizePx(int px, boolean left) {
        if (!mIsSetUp) {
            throw new IllegalStateException(
                    "You should call setUp() before setEdgeSizeDp()");
        }
//        try {
//            String field = left ? "mLeftDragger" : "mRightDragger";
//            Field dragger = mDrawerLayout.getClass().asSubclass(DrawerLayout.class).getField(field);
//            dragger.setAccessible(true);
//            ViewDragHelper helper = (ViewDragHelper) dragger.get(mDrawerLayout);
//            Field mEdgeSize = helper.getClass().getDeclaredField("mEdgeSize");
//            mEdgeSize.setAccessible(true);
//            mEdgeSize.setInt(helper, px);
//        } catch (Exception e) {
//            e.printStackTrace();
////            throw new RuntimeException("Error setting edge size", e);
//        }
    }

    public void setRightEdgeSizeDp(int dp) {
        float density = getResources().getDisplayMetrics().density;
        int px = (int) (dp * density + 0.5f);
        setEdgeSizePx(px, false);
    }

    public interface NavigationListener {
        public void onNavigationOpened();

        public void onNavigationClosed();
    }

}
