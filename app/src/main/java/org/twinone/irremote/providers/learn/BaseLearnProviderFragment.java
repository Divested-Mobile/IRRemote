package org.twinone.irremote.providers.learn;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.LinearInterpolator;

import org.twinone.irremote.R;
import org.twinone.irremote.ir.Signal;
import org.twinone.irremote.ir.io.Receiver;
import org.twinone.irremote.ir.io.Receiver.OnLearnListener;
import org.twinone.irremote.ir.io.Transmitter;
import org.twinone.irremote.providers.ProviderFragment;

import de.passsy.holocircularprogressbar.HoloCircularProgressBar;

public abstract class BaseLearnProviderFragment extends ProviderFragment
        implements OnLearnListener, AnimatorListener {

    private static final String TAG = "LearnFragment";
    private static final int TIMEOUT_SECONDS = 10;

    private HoloCircularProgressBar mProgress;
    private ObjectAnimator mAnimator;

    private Transmitter mTransmitter;
    private Receiver mReceiver;

    private State mCurrentState = State.READY;

    State getState() {
        return mCurrentState;
    }

    void setState(State state) {
        mCurrentState = state;
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
        if (mReceiver != null) {
            mReceiver.stop();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // if in debug mode, we can work without a receiver
        if (mReceiver != null) {
            mReceiver.setListener(this);
            mReceiver.start();
        }
    }

    void setProgressBar(HoloCircularProgressBar bar) {
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

    void learnStart() {
        Log.d(TAG, "LearnStart");
        mCurrentState = State.LEARNING;

        mReceiver.learn(TIMEOUT_SECONDS);

        updateProgress();
        // mProgress.setThumbEnabled(true);
        // mProgress.setProgress(0.0F);
        // mProgress.setProgressColor(getResources().getColor(R.color.main_red));
        if (mAnimator.isRunning()) {
            mAnimator.cancel();
        }
        mAnimator.addListener(this);
        mAnimator.start();

    }

    void learnStop() {
        mCurrentState = State.READY;

        mReceiver.cancel();

        mAnimator.removeAllListeners();
        mAnimator.cancel();

        updateProgress();
        // mProgress.setThumbEnabled(false);
        // mProgress.setProgress(0.0F);
        // mProgress.setProgressColor(getResources().getColor(R.color.main_red));
    }

    /**
     * Updates the progress indicator to the current state
     */
    void updateProgress() {
        updateProgress(mCurrentState);
    }

    private void updateProgress(State state) {
        switch (state) {
            case READY:
                mProgress.setThumbEnabled(false);
                mProgress.setProgress(0.0F);
                mProgress.setProgressColor(getResources()
                        .getColor(R.color.learn_progress_color));
                break;
            case SAVED:
            case LEARNED_TRIED:
            case LEARNED:
                mProgress.setThumbEnabled(false);
                mProgress.setProgress(1.0F);
                mProgress.setProgressColor(getResources().getColor(
                        R.color.green_learned));

                break;
            case LEARNING:
                mProgress.setThumbEnabled(true);
                mProgress.setProgress(0.0F);
                mProgress.setProgressColor(getResources()
                        .getColor(R.color.learn_progress_color));
                break;
        }
    }

    void learnConfirm(Signal s) {
        mCurrentState = State.LEARNED;

        mAnimator.removeAllListeners();
        mAnimator.cancel();
        mReceiver.cancel();

        updateProgress();
        // mProgress.setThumbEnabled(false);
        // mProgress.setProgress(1.0F);
        // mProgress.setProgressColor(getResources().getColor(
        // R.color.green_learned));
    }

    @Override
    public void onAnimationCancel(Animator animation) {
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        if (isAdded()) {
            Log.d(TAG, "Animation end");
            if (getState() == State.LEARNING) {
                onLearnTimeout();
            }
            learnStop();
        }
    }

    protected abstract void onLearnTimeout();

    @Override
    public void onAnimationRepeat(Animator animation) {
    }

    @Override
    public void onAnimationStart(Animator animation) {
    }

    Transmitter getTransmitter() {
        return mTransmitter;
    }

    protected enum State {
        /**
         * Ready to start learning
         */
        READY,
        /**
         * Currently learning, user can abort
         */
        LEARNING,
        /**
         * Code correctly learned, currently waiting for the user to try the
         * button out
         */
        LEARNED,
        /**
         * User has tried the code out, and must now decide if it works
         */
        LEARNED_TRIED,
        /**
         * User navigated to a button that was already saved
         */
        SAVED
    }

}
