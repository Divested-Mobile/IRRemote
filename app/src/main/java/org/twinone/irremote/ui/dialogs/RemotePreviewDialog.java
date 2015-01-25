package org.twinone.irremote.ui.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;

import org.twinone.irremote.R;
import org.twinone.irremote.compat.Compat;
import org.twinone.irremote.components.Remote;
import org.twinone.irremote.components.RemoteOrganizer;
import org.twinone.irremote.providers.ProviderActivity;
import org.twinone.irremote.ui.ButtonView;
import org.twinone.irremote.ui.RemoteView;

public class RemotePreviewDialog extends DialogFragment {


    private static final String ARG_REMOTE = "org.twinone.irremote.arg.remote";
    public static final String DIALOG_TAG = "preview_remote_dialog";


    private Remote mRemote;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MaterialDialog.Builder b = Compat.getMaterialDialogBuilder(getActivity());
        b.negativeText(android.R.string.cancel);

        if (ProviderActivity.ACTION_SAVE_REMOTE.equals(getProvider().getAction())) {
            b.title(R.string.preview_remote_dlgtit_remote);
            b.positiveText(R.string.save_remote_save);
            b.callback(new MaterialDialog.ButtonCallback() {
                @Override
                public void onPositive(MaterialDialog dialog) {
                    getProvider().performSaveRemote(mRemote);
                }
            });
        } else {
            b.title(R.string.preview_remote_dlgtit_button);
        }

        RemoteOrganizer ro = new RemoteOrganizer(getActivity());
        ro.setButtonSizeDp(56);
        ro.setHorizontalMarginDp(0);
        ro.setButtonSpacingDp(8);
        ro.setVerticalMarginDp(16);
        ro.updateWithoutSaving(mRemote);

        if (ProviderActivity.ACTION_GET_BUTTON.equals(getProvider().getAction())) {
            SelectButtonRemoteView root = new SelectButtonRemoteView(getActivity(), mRemote);
            b.customView(root, true);
        } else {
            TransmitRemoteView root = new TransmitRemoteView(getActivity(), mRemote);
            b.customView(root, true);
        }
        return b.build();
    }


    private ProviderActivity getProvider() {
        return (ProviderActivity) getActivity();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof ProviderActivity)) {
            throw new IllegalStateException("RemotePreviewDialog should be attached to a ProviderActivity");
        }

        if (getArguments() == null || !getArguments().containsKey(ARG_REMOTE)) {
            throw new IllegalStateException("RemotePreviewDialog should be constructed with the newInstance() method");
        }

        mRemote = (Remote) getArguments().getSerializable(ARG_REMOTE);
    }

    public static RemotePreviewDialog newInstance(Remote remote) {
        RemotePreviewDialog f = new RemotePreviewDialog();
        Bundle b = new Bundle();
        b.putSerializable(ARG_REMOTE, remote);
        f.setArguments(b);
        return f;
    }


    public RemotePreviewDialog show(Activity a) {
        show(a.getFragmentManager(), DIALOG_TAG);
        return this;
    }


    public class SelectButtonRemoteView extends RemoteView {
        public SelectButtonRemoteView(Context c, Remote remote) {
            super(c, remote);
        }

        @Override
        protected void setupButton(ButtonView bv) {
            bv.setOnClickListener(mSaveButtonOnClickListener);
        }
    }

    private View.OnClickListener mSaveButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!(v instanceof ButtonView)) return;
            ButtonView bv = (ButtonView) v;
            getProvider().requestSaveButton(bv.getButton());
        }
    };

}
