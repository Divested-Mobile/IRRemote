package org.twinone.irremote.ui;

import java.util.ArrayList;
import java.util.List;

import org.twinone.irremote.R;
import org.twinone.irremote.components.Remote;
import org.twinone.irremote.ir.io.Transmitter;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

/**
 * Displays the remote.
 * 
 * @author twinone
 * 
 */
public abstract class BaseRemoteFragment extends Fragment {

	protected static final String TAG = "RemoteFragment";
	private static final String SAVE_REMOTE = "save_remote";

	protected Handler mHandler = new Handler();

	protected Remote mRemote;
	private Transmitter mTransmitter;
	protected List<ButtonView> mButtons = new ArrayList<ButtonView>();
	// protected ComponentUtils mComponentUtils;

	protected RemoteView mRemoteView;
	protected ScrollView mScroll;

	private static final String ARG_REMOTE_NAME = "arg_remote_name";

	public final void showFor(Activity a, String remoteName) {
		showFor(a, remoteName, null);
	}

	/** Use this method just after calling the constructor */
	public final void showFor(Activity a, String remoteName, String tag) {

		Bundle b = new Bundle();
		b.putSerializable(ARG_REMOTE_NAME, remoteName);
		setArguments(b);
		a.getFragmentManager().beginTransaction()
				.replace(R.id.container, this, tag).commit();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() == null
				|| !getArguments().containsKey(ARG_REMOTE_NAME)) {
			throw new RuntimeException(
					"You should create this fragment with the showFor method");
		}

		if (savedInstanceState != null) {
			Log.d(TAG, "Retrieving remote from savedInstanceState");
			mRemote = (Remote) savedInstanceState.getSerializable(SAVE_REMOTE);
		} else {
			mRemote = Remote.load(getActivity(), (String) getArguments()
					.getSerializable(ARG_REMOTE_NAME));
		}
		mTransmitter = Transmitter.getInstance(getActivity());

		// mComponentUtils = new ComponentUtils(getActivity());

	}

	private int getThemeIdFromPrefs() {
		SharedPreferences sp = SettingsActivity.getPreferences(getActivity());
		String theme = sp.getString(getString(R.string.pref_key_theme),
				getString(R.string.pref_def_theme));
		if (theme.equals(getString(R.string.pref_val_theme_sl))) {
			return R.style.theme_solid;
		}
		return R.style.theme_transparent;

	}

	/**
	 * Call super.onCreateView for theming and optionsMenu
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		getActivity().setTheme(getThemeIdFromPrefs());

		setHasOptionsMenu(true);

		if (mRemote == null) {
			return new View(getActivity());
		}

		mScroll = (ScrollView) inflater.inflate(R.layout.fragment_remote_new,
				container, false);

		mRemoteView = (RemoteView) mScroll.findViewById(R.id.container);
		mRemoteView.setRemote(mRemote);
		setupButtons();

		return mScroll;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(SAVE_REMOTE, mRemote);
		super.onSaveInstanceState(outState);
	}

	protected void setupButtons() {
		mRemoteView.removeAllViews();
		mButtons = new ArrayList<ButtonView>(mRemote.buttons.size());
		for (org.twinone.irremote.components.Button b : mRemote.buttons) {
			ButtonView bv = new ButtonView(getActivity());
			bv.setButton(b);

			mButtons.add(bv);
			mRemoteView.addView(bv);
			// bv.setX(b.x);
			// bv.setY(b.y);
			bv.getLayoutParams().width = (int) b.w;
			bv.getLayoutParams().height = (int) b.h;
			bv.requestLayout();

		}
	}

	protected Transmitter getTransmitter() {
		return mTransmitter;
	}

	public Remote getRemote() {
		return mRemote;
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

}
