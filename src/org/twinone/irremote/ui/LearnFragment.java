package org.twinone.irremote.ui;

import org.twinone.irremote.R;
import org.twinone.irremote.ir.Signal;
import org.twinone.irremote.ir.io.Receiver;
import org.twinone.irremote.ir.io.Receiver.OnLearnListener;
import org.twinone.irremote.ir.io.Transmitter;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class LearnFragment extends Fragment implements View.OnClickListener,
		OnLearnListener {

	private static final int TIMEOUT = 10;

	private Transmitter mTransmitter;
	private Receiver mReceiver;

	private Button mLearn;
	private Button mCancel;
	private Button mTest;

	private TextView mStatus;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mTransmitter = Transmitter.getInstance(getActivity());
		mReceiver = Receiver.getInstance(getActivity());
		mReceiver.setListener(this);
		mReceiver.start();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_learn, container, false);
		mLearn = (Button) view.findViewById(R.id.learn_start);
		mCancel = (Button) view.findViewById(R.id.learn_cancel);
		mTest = (Button) view.findViewById(R.id.learn_test);
		mStatus = (TextView) view.findViewById(R.id.learn_status);

		mLearn.setOnClickListener(this);
		mCancel.setOnClickListener(this);
		mTest.setOnClickListener(this);
		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.learn_start:
			mReceiver.learn(TIMEOUT);
			break;
		case R.id.learn_cancel:
			if (mReceiver.isReceiving()) {
				mReceiver.cancel();
			}
			break;
		case R.id.learn_test:
			if (mSignal != null) {
				mTransmitter.transmit(mSignal);
			}
			break;
		}

	}

	private Signal mSignal;

	@Override
	public void onError(int errorCode) {
		mReceiver.learn(TIMEOUT);
	}

	@Override
	public void onTimeout() {

	}

	@Override
	public void onCancel() {
		mStatus.setText("Canceled!");

	}

	@Override
	public void onLearn(Signal s) {
		mSignal = s;
		mStatus.setText("Code learned!!!");
	}

}
