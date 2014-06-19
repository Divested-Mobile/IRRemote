package org.twinone.irremote.ui;

import org.twinone.irremote.R;
import org.twinone.irremote.ir.Signal;
import org.twinone.irremote.ir.io.Receiver;
import org.twinone.irremote.ir.io.Receiver.OnLearnListener;
import org.twinone.irremote.ir.io.Transmitter;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.TextView;
import de.passsy.holocircularprogressbar.HoloCircularProgressBar;

public class LearnFragment extends Fragment implements View.OnClickListener,
		OnLearnListener, AnimatorListener {

	private static final String TAG = "LearnFragment";
	private static final int TIMEOUT_SECONDS = 10;

	private HoloCircularProgressBar mProgress;
	private ObjectAnimator mAnimator;

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

		mProgress = (HoloCircularProgressBar) view
				.findViewById(R.id.learn_progress);

		mAnimator = ObjectAnimator.ofFloat(mProgress, "float", 1f);
		mAnimator.setDuration(TIMEOUT_SECONDS * 1000);

		mAnimator.setInterpolator(new LinearInterpolator());
		mAnimator.addUpdateListener(new AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				mProgress.setProgress((Float) animation.getAnimatedValue());
			}
		});

		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.learn_start:
			learnStart();
			break;
		case R.id.learn_cancel:
			learnStop();
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

		Log.d(TAG, "OnError Starting learn!!!: " + errorCode);
		mReceiver.learn(TIMEOUT_SECONDS);
	}

	@Override
	public void onTimeout() {
	}

	@Override
	public void onCancel() {
		Log.d(TAG, "OnCancel");
	}

	@Override
	public void onLearn(Signal s) {
		Log.d(TAG, "onLearn");
		mSignal = s;
		mStatus.setText(R.string.learn_learned);
		learnConfirm();

	}

	private void learnStart() {
		Log.d(TAG, "LearnStart");
		mReceiver.learn(TIMEOUT_SECONDS);

		mProgress.setThumbEnabled(true);
		mProgress.setProgress(0.0F);
		mProgress.setProgressColor(getResources().getColor(R.color.main_red));
		if (mAnimator.isRunning()) {
			mAnimator.cancel();
		}
		mAnimator.addListener(this);
		mAnimator.start();
		mStatus.setText(R.string.learn_learning);

		mLearn.setEnabled(false);
		mCancel.setEnabled(true);
		mTest.setEnabled(false);

	}

	private void learnStop() {
		Log.d(TAG, "LearnStop");
		Log.d(TAG, "Cancelling receiver");
		mReceiver.cancel();

		mAnimator.removeAllListeners();
		mAnimator.cancel();
		mProgress.setThumbEnabled(false);
		mProgress.setProgress(0.0F);
		mProgress.setProgressColor(getResources().getColor(R.color.main_red));
		mStatus.setText(R.string.learn_ready);

		mLearn.setEnabled(true);
		mCancel.setEnabled(false);
		mTest.setEnabled(false);

	}

	private void learnConfirm() {
		Log.d(TAG, "LearnConfirm");
		mAnimator.cancel();
		mReceiver.cancel();
		mProgress.setThumbEnabled(false);
		mProgress.setProgress(1.0F);
		mProgress.setProgressColor(getResources().getColor(
				R.color.green_learned));
		mStatus.setText(R.string.learn_learned);

		mLearn.setEnabled(true);
		mCancel.setEnabled(false);
		mTest.setEnabled(true);

	}

	@Override
	public void onAnimationCancel(Animator animation) {
	}

	@Override
	public void onAnimationEnd(Animator animation) {
		Log.d(TAG, "Animation end");
		learnStop();
		mStatus.setText("Timed out\nTouch Learn to try again");
	}

	@Override
	public void onAnimationRepeat(Animator animation) {
	}

	@Override
	public void onAnimationStart(Animator animation) {
	}

}
