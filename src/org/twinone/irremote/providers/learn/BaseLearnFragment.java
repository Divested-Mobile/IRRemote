package org.twinone.irremote.providers.learn;

import org.twinone.irremote.R;
import org.twinone.irremote.ir.Signal;
import org.twinone.irremote.ir.io.Receiver;
import org.twinone.irremote.ir.io.Receiver.OnLearnListener;
import org.twinone.irremote.ir.io.Transmitter;
import org.twinone.irremote.providers.BaseProviderFragment;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import de.passsy.holocircularprogressbar.HoloCircularProgressBar;

public abstract class BaseLearnFragment extends BaseProviderFragment implements
		OnLearnListener, AnimatorListener {

	private static final String TAG = "LearnFragment";
	private static final int TIMEOUT_SECONDS = 10;

	private HoloCircularProgressBar mProgress;
	private ObjectAnimator mAnimator;

	private Transmitter mTransmitter;
	private Receiver mReceiver;

	private State mCurrentState = State.READY;

	protected State getState() {
		return mCurrentState;
	}

	protected enum State {
		READY, LEARNING, LEARNED
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mTransmitter = Transmitter.getInstance(getActivity());
		mReceiver = Receiver.getInstance(getActivity());
	}

	@Override
	public void onPause() {
		super.onPause();
		mReceiver.stop();
	}

	@Override
	public void onResume() {
		super.onResume();
		mReceiver.setListener(this);
		mReceiver.start();
	}

	protected void setProgressBar(HoloCircularProgressBar bar) {
		mProgress = bar;
		mAnimator = ObjectAnimator.ofFloat(mProgress, "float", 1f);
		mAnimator.setDuration(TIMEOUT_SECONDS * 1000);

		mAnimator.setInterpolator(new LinearInterpolator());
		mAnimator.addUpdateListener(new AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				mProgress.setProgress((Float) animation.getAnimatedValue());
			}
		});

	}

	@Override
	public void onLearnStart() {
	}

	@Override
	public void onError(int errorCode) {

		Log.d(TAG, "OnError Starting learn!!!: " + errorCode);
		mReceiver.learn(TIMEOUT_SECONDS);
	}

	@Override
	public void onTimeout() {
	}

	@Override
	public void onLearnCancel() {
		Log.d(TAG, "OnCancel");
	}

	@Override
	public void onLearn(Signal s) {
		Log.d(TAG, "onLearn");
		learnConfirm(s);
	}

	protected void learnStart() {
		Log.d(TAG, "LearnStart");
		mCurrentState = State.LEARNING;

		mReceiver.learn(TIMEOUT_SECONDS);

		mProgress.setThumbEnabled(true);
		mProgress.setProgress(0.0F);
		mProgress.setProgressColor(getResources().getColor(R.color.main_red));
		if (mAnimator.isRunning()) {
			mAnimator.cancel();
		}
		mAnimator.addListener(this);
		Log.d("", "LEarn start");
		mAnimator.start();

	}

	protected void learnStop() {
		mCurrentState = State.READY;

		mReceiver.cancel();

		mAnimator.removeAllListeners();
		mAnimator.cancel();
		mProgress.setThumbEnabled(false);
		mProgress.setProgress(0.0F);
		mProgress.setProgressColor(getResources().getColor(R.color.main_red));
	}

	protected void learnConfirm(Signal s) {
		mCurrentState = State.LEARNED;

		mAnimator.removeAllListeners();
		mAnimator.cancel();
		mReceiver.cancel();
		mProgress.setThumbEnabled(false);
		mProgress.setProgress(1.0F);
		mProgress.setProgressColor(getResources().getColor(
				R.color.green_learned));
	}

	@Override
	public void onAnimationCancel(Animator animation) {
	}

	@Override
	public void onAnimationEnd(Animator animation) {
		Log.d(TAG, "Animation end");
		if (getState() == State.LEARNING) {
			onLearnTimeout();
		}
		learnStop();
	}

	protected abstract void onLearnTimeout();

	@Override
	public void onAnimationRepeat(Animator animation) {
	}

	@Override
	public void onAnimationStart(Animator animation) {
	}

	protected Transmitter getTransmitter() {
		return mTransmitter;
	}

}
