package org.twinone.irremote.ui.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.twinone.irremote.R;
import org.twinone.irremote.components.AnimHelper;
import org.twinone.irremote.components.Button;
import org.twinone.irremote.components.Remote;
import org.twinone.irremote.components.RemoteOrganizer;

import java.util.Iterator;

public class SaveRemoteDialog extends DialogFragment implements
        DialogInterface.OnClickListener {

    private static final String ARG_REMOTE = "org.twinone.irremote.arg.remote";
    private Remote mRemote;
    private EditText mRemoteName;
    private OnRemoteSavedListener mListener;

    public static void showFor(Activity a, Remote remote) {
        SaveRemoteDialog.newInstance(remote).show(a.getFragmentManager(),
                "save_remote_dialog");
    }

    public static SaveRemoteDialog newInstance(Remote remote) {
        if (remote == null)
            throw new NullPointerException("Remote cannot be null");
        Log.d("", "SaveDialog Name: " + remote.name);
        SaveRemoteDialog f = new SaveRemoteDialog();
        Bundle b = new Bundle();
        b.putSerializable(ARG_REMOTE, remote);
        f.setArguments(b);
        return f;
    }

    public void show(Activity a) {
        show(a.getFragmentManager(), "save_remote_dialog");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRemote = (Remote) getArguments().getSerializable(ARG_REMOTE);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(
                R.layout.dialog_edit_text, null, false);
        mRemoteName = (EditText) view.findViewById(R.id.dialog_edittext_input);
        mRemoteName.setSelectAllOnFocus(true);
        if (mRemote != null) {
            mRemoteName.setText(mRemote.name);
        }
        AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
        ab.setView(view);

        ab.setTitle(R.string.save_remote_title);
        ab.setMessage(R.string.save_remote_text);
        ab.setPositiveButton(R.string.save_remote_save, this);
        ab.setNegativeButton(android.R.string.cancel, null);
        return AnimHelper.addAnimations(ab.create());
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                final String name = mRemoteName.getText().toString();
                if (name == null || name.isEmpty()) {
                    Toast.makeText(getActivity(), R.string.save_remote_empty,
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                mRemote.name = name;
                Iterator<Button> it = mRemote.buttons.iterator();
                while (it.hasNext()) {
                    final Button b = it.next();
                    Log.d("SaveRemoteDialog", "Saving button: " + b.text);
                    if (b.code == null || b.code.isEmpty()) {
                        it.remove();
                    }
                }
                RemoteOrganizer ro = new RemoteOrganizer(getActivity());
                ro.updateWithoutSaving(mRemote);
                RemoteOrganizer.addIcons(mRemote, false);

                mRemote.save(getActivity());
                if (mListener != null)
                    mListener.onRemoteSaved(mRemote.name);
                break;
        }
    }

    public void setListener(OnRemoteSavedListener listener) {
        mListener = listener;
    }

    public interface OnRemoteSavedListener {
        public void onRemoteSaved(String name);
    }
}
