package org.twinone.irremote.compat;

import org.twinone.irremote.R;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class ToolbarActivity extends ActionBarActivity {

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
		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(mToolbar);
	}

	public Toolbar getToolbar() {
		return mToolbar;
	}

}
