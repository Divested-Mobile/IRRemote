package org.twinone.irremote.ui.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import org.twinone.irremote.providers.ProviderInterface;

public abstract class DefaultDialogFragment extends DialogFragment {

    protected abstract AlertDialog.Builder getDefaultDialog(Bundle savedInstanceState);

    public DefaultDialogFragment show(@NonNull AppCompatActivity activity) {
        show(activity.getSupportFragmentManager(), getClass().getName());
        return this;
    }

    protected ProviderInterface getProvider() {
        return (ProviderInterface) getActivity();
    }

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        if (!(getActivity() instanceof ProviderInterface)) {
            throw new ClassCastException(getClass().getName() + " must be added to an instance of ProviderInterface");
        }
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);

        if (this instanceof SaveButtonDialog) {
            getProvider().onSaveButton();
        } else if (this instanceof RemotePreviewDialog) {
            getProvider().onRemotePreview();
        } else if (this instanceof SaveRemoteDialog) {
            getProvider().onSaveRemote();
        }
    }

    @Override
    @NonNull
    public final Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog dialog = getDefaultDialog(savedInstanceState).create();
        Window window = dialog.getWindow();
        if (window != null)
            window.addFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
        setCancelable(false);
        return dialog;
    }
}
