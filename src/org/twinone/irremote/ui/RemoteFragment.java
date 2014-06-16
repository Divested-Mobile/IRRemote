package org.twinone.irremote.ui;

import java.util.ArrayList;
import java.util.List;

import org.twinone.irremote.ButtonUtils;
import org.twinone.irremote.R;
import org.twinone.irremote.Remote;
import org.twinone.irremote.ir.Signal;
import org.twinone.irremote.ir.Transmitter;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class RemoteFragment extends Fragment implements View.OnTouchListener,
		Transmitter.OnTransmitListener {

	private static final String TAG = "RemoteFragment";

	private Remote mRemote;
	private Transmitter mTransmitter;
	protected List<Button> mButtons = new ArrayList<Button>();

	private ButtonUtils mButtonUtils;

	private static final String ARG_REMOTE_NAME = "arg_remote_name";

	public static final void showFor(Activity a, String remoteName) {

		final RemoteFragment frag = new RemoteFragment();
		Bundle b = new Bundle();
		b.putSerializable(ARG_REMOTE_NAME, remoteName);
		frag.setArguments(b);
		a.getFragmentManager().beginTransaction().replace(R.id.container, frag)
				.commit();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mTransmitter = new Transmitter(getActivity());
		mTransmitter.setListener(this);
		mButtonUtils = new ButtonUtils(getActivity());

		if (getArguments() == null
				|| !getArguments().containsKey(ARG_REMOTE_NAME)) {
			throw new RuntimeException(
					"You create this fragment with the showFor method");
		}
		mRemote = Remote.load(getActivity(), (String) getArguments()
				.getSerializable(ARG_REMOTE_NAME));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// If no remote specified, just cancel
		if (mRemote == null)
			return new View(getActivity());

		setHasOptionsMenu(true);

		int resId = R.layout.fragment_remote_tv;
		if (mRemote.options == null
				|| mRemote.options.type == Remote.TYPE_CABLE) {
			resId = R.layout.fragment_remote_cable;
		}

		View view = inflater.inflate(resId, container, false);

		SparseIntArray ids = mButtonUtils.getArray();
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
			int buttonId = mButtonUtils.getButtonId(b.getId());
			b.setVisibility(View.VISIBLE);
			if (mRemote.contains(true, buttonId)) {
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

	private boolean mFingerDown;
	private int mFingerDownId;

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {

		case MotionEvent.ACTION_DOWN:

			if (!mFingerDown) {
				mFingerDownId = event.getPointerId(0);
				final Signal s = mRemote.getButton(true,
						mButtonUtils.getButtonId(v.getId())).getSignal();
				mTransmitter.startTransmitting(s);
				mFingerDown = true;
				return false;
			}

			break;

		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			if (mFingerDown && event.getPointerId(0) == mFingerDownId) {
				boolean atLeastOnce = event.getAction() != MotionEvent.ACTION_CANCEL;
				mTransmitter.stopTransmitting(atLeastOnce);
				mFingerDown = false;
				return false;
			}
			break;
		}
		// Block multiple fingers from appearing as clicked
		return true;
	}

	public void transmit(boolean common, int id) {
		final Signal s = mRemote.getButton(common, id).getSignal();
		mTransmitter.transmit(s);
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
	private Handler mHandler = new Handler();
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
}
