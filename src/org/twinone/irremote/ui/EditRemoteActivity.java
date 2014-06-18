package org.twinone.irremote.ui;

import org.twinone.irremote.R;
import org.twinone.irremote.ir.io.Receiver;

import android.content.pm.ActivityInfo;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;

public class EditRemoteActivity extends ActionBarActivity {
	private NavFragment mNavFragment;

	public static final String EXTRA_REMOTE = "org.twinone.irremote.intent.extra.remote";

	@Override
	protected void onCreate(android.os.Bundle savedInstanceState) {
		setContentView(R.layout.activity_main);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		mNavFragment = (NavFragment) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mNavFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
		mNavFragment.setEdgeSizeDp(30);

	}

	@Override
	public boolean onNavigateUp() {
		finish();
		return true;
	}

}
