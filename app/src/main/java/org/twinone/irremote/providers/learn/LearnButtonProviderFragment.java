package org.twinone.irremote.providers.learn;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.twinone.irremote.R;
import org.twinone.irremote.ir.Signal;

import de.passsy.holocircularprogressbar.HoloCircularProgressBar;

public class LearnButtonProviderFragment extends BaseLearnProviderFragment implements
        View.OnClickListener {

    private Button mLearn;
    private Button mCancel;
    private Button mTest;
    private TextView mStatus;

    private Signal mLearnedSignal;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_learn_button, container, false);
        mLearn = (Button) view.findViewById(R.id.learn_start);
        mCancel = (Button) view.findViewById(R.id.learn_cancel);
        mTest = (Button) view.findViewById(R.id.learn_test);
        mStatus = (TextView) view.findViewById(R.id.learn_status);

        mLearn.setOnClickListener(this);
        mCancel.setOnClickListener(this);
        mTest.setOnClickListener(this);

        HoloCircularProgressBar bar = (HoloCircularProgressBar) view
                .findViewById(R.id.learn_progress);
        setProgressBar(bar);

        return view;

    }

    @Override
    protected void learnConfirm(Signal s) {
        super.learnConfirm(s);
        mStatus.setText(R.string.learn_learned);

        mLearn.setEnabled(true);
        mCancel.setEnabled(false);
        mTest.setEnabled(true);

        mLearnedSignal = s;

    }

    @Override
    protected void onLearnTimeout() {
    }

    @Override
    void learnStop() {
        super.learnStop();
        mStatus.setText(R.string.learn_tit_ready);

        mLearn.setEnabled(true);
        mCancel.setEnabled(false);
        mTest.setEnabled(false);

    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();

        if (id == R.id.learn_start) {
            learnStart();
        } else if (id == R.id.learn_cancel) {
            learnStop();
        } else if (id == R.id.learn_test) {
            if (mLearnedSignal != null) {
                getTransmitter().transmit(mLearnedSignal);
            }
        }
    }

    @Override
    void learnStart() {
        super.learnStart();
        mStatus.setText(R.string.learn_learning);

        mLearn.setEnabled(false);
        mCancel.setEnabled(true);
        mTest.setEnabled(false);

    }

}
