package org.twinone.androidlib.compat;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.twinone.androidlib.R;

public abstract class ToolbarActivity extends AppCompatActivity {

    private static final String TOOLBAR_ACTIVITY_TITLE = "toolbar_activity_title";
    private static final String TOOLBAR_ACTIVITY_SUBTITLE = "toolbar_activity_subtitle";

    private Toolbar mToolbar;
    private CharSequence mTitle;
    private CharSequence mSubtitle;
    private CharSequence mPendingTitle;
    private CharSequence mPendingSubtitle;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        setupToolbar();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        setupToolbar();
    }

    @Override
    public void setContentView(View view, LayoutParams params) {
        super.setContentView(view, params);
        setupToolbar();
    }

    protected void setupToolbar() {
        mToolbar = findViewById(R.id.androidlib_toolbar);
        setSupportActionBar(mToolbar);
        if (mPendingTitle != null) {
            setTitle(mPendingTitle);
            mPendingTitle = null;
        }
        if (mPendingSubtitle != null) {
            setSubtitle(mPendingSubtitle);
            mPendingSubtitle = null;
        }
    }

    public void setSubtitle(CharSequence subtitle) {
        this.mSubtitle = subtitle;
        if (getToolbar() != null) {
            getToolbar().setSubtitle(subtitle);
        } else {
            mPendingSubtitle = subtitle;
        }
    }

    public void setSubtitle(int resId) {
        setSubtitle(getString(resId));
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        setTitle(savedInstanceState.getString(TOOLBAR_ACTIVITY_TITLE));
        setSubtitle(savedInstanceState.getString(TOOLBAR_ACTIVITY_SUBTITLE));
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(TOOLBAR_ACTIVITY_TITLE, (String) mTitle);
        outState.putString(TOOLBAR_ACTIVITY_SUBTITLE, (String) mSubtitle);
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        mTitle = title;
        if (getToolbar() != null) {
            getToolbar().setTitle(title);
        } else {
            mPendingTitle = title;
        }
    }
}
