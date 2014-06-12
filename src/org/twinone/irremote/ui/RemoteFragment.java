package org.twinone.irremote.ui;

import java.util.ArrayList;
import java.util.List;

import org.twinone.androidlib.AdMobBannerBuilder;
import org.twinone.irremote.ButtonUtils;
import org.twinone.irremote.R;
import org.twinone.irremote.Remote;
import org.twinone.irremote.ir.Signal;
import org.twinone.irremote.ir.Transmitter;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class RemoteFragment extends Fragment implements View.OnClickListener {

	private static final String TAG = "RemoteFragment";

	private Remote mRemote;
	private Transmitter mTransmitter;
	protected List<Button> mButtons = new ArrayList<Button>();

	private ButtonUtils mButtonUtils;

	private ViewGroup mAdViewContainer;

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

		// TODO fix crash in remote.options = null?
		int resId = R.layout.fragment_remote_tv;
		if (mRemote.options == null
				|| mRemote.options.type == Remote.DEVICE_TYPE_CABLE) {
			resId = R.layout.fragment_remote_cable;
		}

		View view = inflater.inflate(resId, container, false);

		Log.d("", "OnCreateView for type: " + mRemote.options.type);
		// Show ads
		mAdViewContainer = (ViewGroup) view.findViewById(R.id.ad_container);
		AdMobBannerBuilder builder = new AdMobBannerBuilder();
		builder.setParent(mAdViewContainer);
		builder.addTestDevice("896CB3D3288417013D38303D179FD45B");
		builder.setAdUnitId("ca-app-pub-5756278739960648/2006850014");
		builder.show();

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
				b.setOnClickListener(this);
				b.setEnabled(true);
			} else {
				b.setEnabled(false);
				b.setOnClickListener(null);
				b.setText(null);
			}
		}
		getActivity().setTitle(mRemote.name);
	}

	@Override
	public void onClick(View v) {
		transmit(true, mButtonUtils.getButtonId(v.getId()));
	}

	public void transmit(boolean common, int id) {
		final Signal s = mRemote.getButton(common, id).getSignal();

		mTransmitter.transmit(s);
	}

	private void replaceFragmentForNewRemote(String remoteName) {
		Log.d("", "Replacing fragment!");
	}

	public Remote getRemote() {
		return mRemote;
	}

}
