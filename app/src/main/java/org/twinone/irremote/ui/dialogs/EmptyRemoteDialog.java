package org.twinone.irremote.ui.dialogs;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;

import org.twinone.irremote.R;
import org.twinone.irremote.components.Remote;

public class EmptyRemoteDialog extends DefaultDialogFragment {

    @Override
    public AlertDialog.Builder getDefaultDialog(Bundle savedInstanceState) {
        setCancelable(false);
        return new AlertDialog.Builder(getActivity())
                .setMessage(R.string.empty_remote_msg)
                .setNegativeButton(android.R.string.cancel,
                        (dialog, which) -> onCancel(dialog))
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    Remote remote = new Remote();
                    remote.name = getString(R.string.empty_remote_tit);
                    getProvider().requestSaveRemote(remote);
                })
                .setTitle(R.string.empty_remote_tit);
    }
}
