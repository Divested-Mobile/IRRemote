package org.twinone.irremote.account;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.astuetz.PagerSlidingTabStrip;

import org.twinone.androidlib.compat.ToolbarActivity;
import org.twinone.irremote.R;
import org.twinone.irremote.components.AnimHelper;
import org.twinone.irremote.ui.SettingsActivity;

import java.util.ArrayList;

public class LoginRegisterActivity extends ToolbarActivity {

    /**
     * This boolean preference contains true if the user is registered and
     * verified
     */
    private static final String PREF_KEY_REGISTERED = "org.twinone.irremote.registered_user";

    /**
     * Returns true if this user is registered and verified
     */
    public static boolean isRegistered(Context c) {
        return SettingsActivity.getPreferences(c).getBoolean(
                PREF_KEY_REGISTERED, false);
    }

    private UserInfo mUserInfo;
    private PagerSlidingTabStrip mTabs;

    public UserInfo getUserInfo() {
        return mUserInfo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserInfo = UserInfo.load(this);

        if (isVerifyIntent()) {
            setContentView(R.layout.activity_empty);
            addFragment(new VerifyFragment());
        } else {
            setContentView(R.layout.activity_login_register);
            // Initialize the ViewPager and set an adapter
            ViewPager pager = (ViewPager) findViewById(R.id.register_pager);
            pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
            pager.setCurrentItem(FRAGMENT_INDEX_REGISTER);
            pager.setOnPageChangeListener(new MyPageListener());


            // Bind the tabs to the ViewPager
             mTabs = (PagerSlidingTabStrip) findViewById(R.id.register_tabs);
            mTabs.setViewPager(pager);

        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private ArrayList<OnUpdateListener> mListeners = new ArrayList<>();

    public void addOnPageSelectedListener(OnUpdateListener l) {
        mListeners.add(l);
    }

    public interface OnUpdateListener {
        public void onPageSelected(int position);

        public void onUpdate();
    }

    private class MyPageListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            for (OnUpdateListener l : mListeners) {
                l.onPageSelected(position);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    public void updateAllFragments() {
        for (OnUpdateListener l : mListeners) {
            l.onUpdate();
        }
    }

    public static int FRAGMENT_INDEX_LOGIN = 0;
    public static int FRAGMENT_INDEX_REGISTER = 1;

    public class MyPagerAdapter extends FragmentPagerAdapter {

        private String[] mTitles;

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
            mTitles = getResources().getStringArray(R.array.reg_titles);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }

        @Override
        public int getCount() {
            return mTitles.length;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == FRAGMENT_INDEX_LOGIN) return new LoginFragment();
            return new RegisterFragment();
        }

        public void oPageSelected(int position)
        {
            // Unfortunately when TabHost changes the current tab, it kindly
            // also takes care of putting focus on it when not in touch mode.
            // The jerk.
            // This hack tries to prevent this from pulling focus out of our
            // ViewPager.

        }
    }

    private boolean isVerifyIntent() {
        Uri data = getIntent().getData();
        return data != null && data.getQueryParameter("a").equals("verify");
    }

    void addFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment).commit();
    }

    @Override
    public void finish() {
        super.finish();
        AnimHelper.onFinish(this);
    }

}
