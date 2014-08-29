package org.twinone.irremote.ui;

import java.util.ArrayList;
import java.util.List;

import org.twinone.irremote.R;
import org.twinone.irremote.components.ComponentUtils;
import org.twinone.irremote.components.Remote;
import org.twinone.irremote.ir.io.Transmitter;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Displays the remote.
 * 
 * @author twinone
 * 
 */
public abstract class BaseRemoteFragment extends Fragment implements
		View.OnTouchListener, Transmitter.OnTransmitListener {

	private static final String TAG = "RemoteFragment";

	protected static final int DETECT_LONGPRESS_DELAY = 250; // ms

	protected Remote mRemote;
	protected Transmitter mTransmitter;
	protected List<Button> mButtons = new ArrayList<Button>();
	protected ComponentUtils mComponentUtils;

	private static final String ARG_REMOTE_NAME = "arg_remote_name";

	/** Use this method just after calling the constructor */
	public final void showFor(Activity a, String remoteName) {

		Bundle b = new Bundle();
		b.putSerializable(ARG_REMOTE_NAME, remoteName);
		setArguments(b);
		a.getFragmentManager().beginTransaction().replace(R.id.container, this)
				.commit();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() == null
				|| !getArguments().containsKey(ARG_REMOTE_NAME)) {
			throw new RuntimeException(
					"You should create this fragment with the showFor method");
		}
		mRemote = Remote.load(getActivity(), (String) getArguments()
				.getSerializable(ARG_REMOTE_NAME));

		mTransmitter = Transmitter.getInstance(getActivity());
		if (mTransmitter != null) {
			mTransmitter.setListener(this);
		}
		mComponentUtils = new ComponentUtils(getActivity());

	}

	private int getThemeIdFromPrefs() {
		SharedPreferences sp = SettingsActivity.getPreferences(getActivity());
		String theme = sp.getString(getString(R.string.pref_key_theme),
				getString(R.string.pref_def_theme));
		Log.d("", "Got theme: " + theme);
		if (theme.equals(getString(R.string.pref_val_theme_sl))) {
			return R.style.theme_solid;
		}
		return R.style.theme_transparent;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getActivity().setTheme(getThemeIdFromPrefs());

		// If no remote specified, just cancel
		if (mRemote == null) {
			Log.w(TAG, "null remote");
			return new View(getActivity());

		}
		setHasOptionsMenu(true);

		int resId = R.layout.fragment_remote_tv;
		try {
			resId = ComponentUtils.getLayout(mRemote.options.type);
		} catch (Exception e) {
			// Older versions don't have the options file in remote
		}

		View view = inflater.inflate(resId, container, false);

		SparseIntArray ids = mComponentUtils.getArray();
		for (int i = 0; i < ids.size(); i++) {
			final int id = ids.valueAt(i);
			if (id != 0) {
				Button b = (Button) view.findViewById(id);
				if (b != null) {
					mButtons.add((Button) view.findViewById(id));
				}
			}
		}

		setupButtons();
		return view;
	}

	protected void setupButtons() {
		if (mButtons == null)
			return;

		for (Button b : mButtons) {
			int buttonId = mComponentUtils.getButtonId(b.getId());
			b.setVisibility(View.VISIBLE);
			if (mRemote.contains(buttonId)) {
				b.setText(mRemote.getButton(true, buttonId).getDisplayName());
				b.setOnTouchListener(this);
				b.setEnabled(true);
			} else {
				b.setEnabled(false);
				b.setOnTouchListener(this);
				b.setText(null);
			}
		}
	}

	protected Transmitter getTransmitter() {
		return mTransmitter;
	}

	public Remote getRemote() {
		return mRemote;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		mMenuIcon = menu.findItem(R.id.menu_transmit_feedback);

	}

	private MenuItem mMenuIcon;
	private static final int MINIMUM_SHOW_TIME = 100; // ms
	protected Handler mHandler = new Handler();
	private Runnable mHideFeedbackRunnable = new HideFeedbackRunnable();

	private class HideFeedbackRunnable implements Runnable {
		@Override
		public void run() {
			// must be run on ui thread, use handlers
			mMenuIcon.setVisible(false);
		}
	}

	@Override
	public void onBeforeTransmit() {
		mHandler.removeCallbacks(mHideFeedbackRunnable);
		mMenuIcon.setVisible(true);
	}

	@Override
	public void onAfterTransmit() {
		mHandler.postDelayed(mHideFeedbackRunnable, MINIMUM_SHOW_TIME);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mTransmitter != null)
			mTransmitter.resume();

	}

	@Override
	public void onPause() {
		super.onPause();
		if (mTransmitter != null)
			mTransmitter.pause();
	}

	protected ComponentUtils getUtils() {
		return mComponentUtils;
	}
}
