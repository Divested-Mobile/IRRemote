package org.twinone.irremote.providers.learn;

import org.twinone.irremote.R;
import org.twinone.irremote.components.Button;
import org.twinone.irremote.components.ComponentUtils;
import org.twinone.irremote.components.Remote;
import org.twinone.irremote.ir.Signal;
import org.twinone.irremote.ir.SignalFactory;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import de.passsy.holocircularprogressbar.HoloCircularProgressBar;

public class LearnRemoteFragment extends BaseLearnFragment implements
		View.OnClickListener {

	private static final String ARG_REMOTE_TYPE = "arg_remote_type";

	private Remote mRemote;

	private TextView mTitle;
	private TextView mDescription;
	private TextView mFooter;
	private android.widget.Button mBottomLeft;
	private android.widget.Button mBottomRight;
	private android.widget.Button mBottomCenter;
	private android.widget.Button mCenterButton;

	private Signal mSignal;

	private String getCurrentButtonName() {
		return mRemote.buttons.get(mCurrentButtonIndex).getDisplayName();
	}

	private int mCurrentButtonIndex = 0;

	public static LearnRemoteFragment getInstance(int remoteType) {
		LearnRemoteFragment f = new LearnRemoteFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_REMOTE_TYPE, remoteType);
		f.setArguments(args);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments() == null
				|| !getArguments().containsKey(ARG_REMOTE_TYPE)) {
			throw new RuntimeException(
					"LearnRemoteFragment should be instantiated with getInstance(int)");
		}
		int type = getArguments().getInt(ARG_REMOTE_TYPE);

		mRemote = ComponentUtils.createEmptyRemote(getActivity(), type);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_learn_remote, container,
				false);

		mTitle = (TextView) view.findViewById(R.id.learn_remote_title);
		mDescription = (TextView) view
				.findViewById(R.id.learn_remote_description);
		mFooter = (TextView) view.findViewById(R.id.learn_remote_footer);

		mBottomLeft = (android.widget.Button) view
				.findViewById(R.id.learn_remote_button_left_bottom);
		mCenterButton = (android.widget.Button) view
				.findViewById(R.id.learn_remote_button_center);
		mBottomRight = (android.widget.Button) view
				.findViewById(R.id.learn_remote_button_right_bottom);
		mBottomCenter = (android.widget.Button) view
				.findViewById(R.id.learn_remote_button_center_bottom);

		mBottomLeft.setOnClickListener(this);
		mBottomCenter.setOnClickListener(this);
		mCenterButton.setOnClickListener(this);
		mBottomRight.setOnClickListener(this);

		HoloCircularProgressBar bar = (HoloCircularProgressBar) view
				.findViewById(R.id.learn_progress);
		setProgressBar(bar);

		setupLayout(State.READY);
		return view;

	}

	@Override
	protected void learnStart() {
		super.learnStart();
		setupLayout(State.LEARNING);
	}

	@Override
	protected void learnStop() {
		super.learnStop();
		setupLayout(State.READY);
	}

	private void setupButton(int index) {
		mCurrentButtonIndex = index;
		setFooterToButtonNavigation();
	}

	private void setFooterToButtonNavigation() {
		setFooterText(getString(R.string.learn_footer, mCurrentButtonIndex + 1,
				mRemote.buttons.size()));
	}

	private void setFooterText(String text) {
		mFooter.setVisibility(View.VISIBLE);
		mFooter.setText(text);

	}

	private void onPreviousClicked() {
		if (mCurrentButtonIndex > 0) {
			setupButton(mCurrentButtonIndex - 1);
		}
		learnStop();
	}

	private void onNextClicked() {
		if (mCurrentButtonIndex < mRemote.buttons.size() - 1) {
			setupButton(mCurrentButtonIndex + 1);
			learnStop();
		} else {
			getProvider().saveRemote(mRemote);
		}
	}

	private void setupLayout(State state) {
		switch (state) {
		case READY:
			mTitle.setText(getString(R.string.learn_tit_ready,
					getCurrentButtonName()));
			mDescription.setText(getString(R.string.learn_desc_ready,
					getCurrentButtonName()));

			mCenterButton.setEnabled(true);
			mCenterButton.setText(R.string.learn_button_ready);

			mBottomLeft.setVisibility(View.VISIBLE);
			mBottomCenter.setVisibility(View.GONE);
			mBottomRight.setVisibility(View.VISIBLE);

			mBottomLeft.setText(R.string.learn_button_back);
			if (mCurrentButtonIndex == mRemote.buttons.size() - 1) {
				mBottomRight.setText(R.string.learn_button_finish);
			} else {
				mBottomRight.setText(R.string.learn_button_skip);
			}

			setFooterToButtonNavigation();
			break;
		case LEARNING:

			mTitle.setText(R.string.learn_tit_learning);
			mDescription.setText(getString(R.string.learn_desc_learning,
					getCurrentButtonName()));
			mCenterButton.setText(R.string.learn_learning);
			mCenterButton.setEnabled(false);

			mBottomLeft.setVisibility(View.GONE);
			mBottomCenter.setVisibility(View.VISIBLE);
			mBottomRight.setVisibility(View.GONE);

			mBottomCenter.setText(R.string.learn_button_cancel);

			setFooterToButtonNavigation();
			break;
		case LEARNED:

			mTitle.setText(R.string.learn_tit_learned);
			mDescription.setText(getString(R.string.learn_desc_learned,
					getCurrentButtonName()));
			mCenterButton.setText(getCurrentButtonName());
			mCenterButton.setEnabled(true);

			mBottomLeft.setVisibility(View.INVISIBLE);
			mBottomCenter.setVisibility(View.INVISIBLE);
			mBottomRight.setVisibility(View.INVISIBLE);

			mFooter.setVisibility(View.INVISIBLE);
			break;

		}
	}

	private void setupLearnedBottomButtons() {
		mBottomLeft.setVisibility(View.VISIBLE);
		mBottomCenter.setVisibility(View.VISIBLE);
		mBottomRight.setVisibility(View.VISIBLE);

		mBottomLeft.setText(R.string.learn_button_skip);
		mBottomCenter.setText(R.string.learn_button_try_again);
		mBottomRight.setText(R.string.learn_button_it_works);

		setFooterText(getString(R.string.learn_footer_did_it_work));
	}

	@Override
	protected void learnConfirm(Signal s) {
		super.learnConfirm(s);
		mSignal = s;
		Log.d("", "Received: " + s.toString());
		setupLayout(State.LEARNED);
	}

	@Override
	protected void onLearnTimeout() {
		AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
		ab.setTitle(R.string.learn_help_tit);
		ab.setMessage(R.string.learn_help_msg);
		ab.setPositiveButton(android.R.string.ok, null);
		ab.show();
	}

	private void saveCurrentButton() {
		final String code = SignalFactory.toPronto(mSignal);
		Button b = mRemote.buttons.get(mCurrentButtonIndex);
		b.code = code;
	}

	@Override
	public void onClick(View v) {
		final State state = getState();
		final int id = v.getId();

		switch (state) {
		case READY:
			if (id == R.id.learn_remote_button_center) {
				learnStart();
			} else if (id == R.id.learn_remote_button_left_bottom) {
				onPreviousClicked();
			} else if (id == R.id.learn_remote_button_right_bottom) {
				onNextClicked();
			}
			break;
		case LEARNING:
			if (id == R.id.learn_remote_button_center_bottom) {
				learnStop();
			}
			break;
		case LEARNED:
			if (id == R.id.learn_remote_button_center) {
				if (mSignal != null) {
					getTransmitter().transmit(mSignal);
					setupLearnedBottomButtons();
				}
			} else if (id == R.id.learn_remote_button_left_bottom) {
				onNextClicked();
			} else if (id == R.id.learn_remote_button_center_bottom) {
				learnStart();
			} else if (id == R.id.learn_remote_button_right_bottom) {
				saveCurrentButton();
				onNextClicked();
			}
			break;

		}
	}
}
