package org.twinone.irremote.ui.dialogs;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import org.twinone.irremote.R;
import org.twinone.irremote.components.Remote;
import org.twinone.irremote.components.RemoteOrganizer;
import org.twinone.irremote.providers.ProviderActivity;
import org.twinone.irremote.ui.ButtonView;
import org.twinone.irremote.ui.RemoteView;

public class RemotePreviewDialog extends DefaultDialogFragment {

    private static final String ARG_REMOTE = "org.twinone.irremote.arg.remote";

    private Remote mRemote;

    @Override
    public AlertDialog.Builder getDefaultDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setNegativeButton(android.R.string.cancel,
                (dialog, which) -> onCancel(dialog));

        if (ProviderActivity.ACTION_SAVE_REMOTE.equals(getProvider().getAction())) {
            builder.setTitle(R.string.preview_remote_dlgtit_remote);
            builder.setPositiveButton(R.string.save_remote_save,
                    (dialog, which) -> getProvider().performSaveRemote(mRemote));
        } else {
            builder.setTitle(R.string.preview_remote_dlgtit_button);
        }

        if (getProvider().getOrganize()) {
            RemoteOrganizer ro = new RemoteOrganizer(getActivity());
            ro.setButtonSizeDp(56);
            ro.setHorizontalMarginDp(0);
            ro.setButtonSpacingDp(8);
            ro.setVerticalMarginDp(16);
            ro.updateWithoutSaving(mRemote);
        }

        ScrollView scrollView = new ScrollView(getActivity());

        if (ProviderActivity.ACTION_GET_BUTTON.equals(getProvider().getAction())) {
            SelectButtonRemoteView root = new SelectButtonRemoteView(getActivity(), mRemote);
            scrollView.addView(root);

        } else {
            TransmitRemoteView root = new TransmitRemoteView(getActivity(), mRemote);
            scrollView.addView(root);
        }
        builder.setView(scrollView);
        return builder;
    }

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);

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

    public class SelectButtonRemoteView extends RemoteView {
        public SelectButtonRemoteView(Context c, Remote remote) {
            super(c, remote);
        }

        @Override
        protected void setupButton(ButtonView bv) {
            bv.setOnClickListener(mSaveButtonOnClickListener);
        }
    }

    private final View.OnClickListener mSaveButtonOnClickListener = v -> {
        if (!(v instanceof ButtonView)) return;
        ButtonView bv = (ButtonView) v;
        getProvider().requestSaveButton(bv.getButton());
    };

}
