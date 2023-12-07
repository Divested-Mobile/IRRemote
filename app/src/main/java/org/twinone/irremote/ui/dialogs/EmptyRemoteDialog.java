package org.twinone.irremote.ui.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import org.twinone.irremote.R;
import org.twinone.irremote.components.Remote;
import org.twinone.irremote.providers.ProviderActivity;

public class EmptyRemoteDialog extends DialogFragment {

    public static final String DIALOG_TAG = "empty_remote_dialog";

    private ProviderActivity getProvider() {
        return (ProviderActivity) getActivity();
    }

    public void show(Activity activity) {
        show(activity.getFragmentManager(), DIALOG_TAG);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setCancelable(false);
        return new AlertDialog.Builder(getActivity())
                .setMessage(R.string.empty_remote_msg)
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                    onCancel(dialog);
                })
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    Remote remote = new Remote();
                    remote.name = getString(R.string.empty_remote_tit);
                    getProvider().requestSaveRemote(remote);
                })
                .setTitle(R.string.empty_remote_tit)
                .show();
    }
}
