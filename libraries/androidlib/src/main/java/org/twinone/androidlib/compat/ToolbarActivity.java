package org.twinone.androidlib.compat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import org.twinone.androidlib.R;

public abstract class ToolbarActivity extends AppCompatActivity {

    private Toolbar mToolbar;

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
        mToolbar = (Toolbar) findViewById(R.id.androidlib_toolbar);
        setSupportActionBar(mToolbar);
        setPendingTitle();
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

    private CharSequence mPendingTitle;

    private void setPendingTitle() {
        if (mPendingTitle != null) {
            mToolbar.setTitle(mPendingTitle);
            mPendingTitle = null;
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        if (getToolbar() != null) {
            getToolbar().setTitle(title);
        } else {
            mPendingTitle = title;
        }
    }
}
