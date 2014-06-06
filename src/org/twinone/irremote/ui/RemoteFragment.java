package org.twinone.irremote.ui;

import java.util.ArrayList;
import java.util.List;

import org.twinone.androidlib.AdMobBannerBuilder;
import org.twinone.irremote.ButtonUtils;
import org.twinone.irremote.R;
import org.twinone.irremote.Remote;
import org.twinone.irremote.ir.Transmitter;

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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mTransmitter = new Transmitter(getActivity());
		mButtonUtils = new ButtonUtils(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.fragment_remote, container, false);

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
					Log.d("", "adding button (id="
							+ getResources().getResourceEntryName(id));
				}
			}
		}

		setup();
		return view;
	}

	protected void setup() {
		Log.w("", "id: " + getResources().getResourceEntryName(R.id.button_guide));
		if (mRemote == null || mButtons == null)
			return;
		for (Button b : mButtons) {
			int buttonId = mButtonUtils.getButtonId(b.getId());
			b.setVisibility(View.VISIBLE);
			if (mRemote.contains(true, buttonId)) {
				b.setText(mRemote.getButton(true, buttonId).getDisplayName());
				Log.d("", "id: " + buttonId + " text: "
						+ b.getText().toString());

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
		mTransmitter.transmit(mRemote.getButton(common, id).getSignal());
	}

	public void setRemote(String remoteName) {
		Remote remote = Remote.load(getActivity(), remoteName);
		if (remote == null) {
			Log.i(TAG, "Remote was null, clearing buttons");
			for (Button b : mButtons) {
				b.setVisibility(View.GONE);
			}
		}
		mRemote = remote;
		setup();
	}

	public Remote getRemote() {
		return mRemote;
	}

}
