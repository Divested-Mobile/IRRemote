package org.twinone.irremote.ui.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.hardware.ConsumerIrManager;
import android.hardware.ConsumerIrManager.CarrierFrequencyRange;

import androidx.appcompat.app.AlertDialog;

import org.twinone.irremote.R;
import org.twinone.irremote.ui.MainActivity;

public class DebugDialog implements DialogInterface.OnClickListener {
    private final MainActivity mActivity;
    private final ConsumerIrManager mIrManager;

    public DebugDialog(MainActivity main) {
        mActivity = main;
        mIrManager = (ConsumerIrManager) mActivity.getSystemService(Context.CONSUMER_IR_SERVICE);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setTitle(R.string.dlg_debug_freq);
            StringBuilder sb = new StringBuilder();
            if (mIrManager.hasIrEmitter()) {
                for (CarrierFrequencyRange cfr : mIrManager.getCarrierFrequencies()) {
                    sb.append(cfr.getMinFrequency() / 1000.0f).append("k - ").append(cfr.getMaxFrequency() / 1000.0f).append("k").append("\n");
                }
            }
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setMessage(sb.toString());
            builder.show();
        }
    }
}
