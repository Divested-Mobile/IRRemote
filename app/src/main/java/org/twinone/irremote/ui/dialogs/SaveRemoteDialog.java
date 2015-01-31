package org.twinone.irremote.ui.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import org.twinone.irremote.R;
import org.twinone.irremote.compat.Compat;
import org.twinone.irremote.components.Remote;
import org.twinone.irremote.providers.ProviderActivity;

import java.util.List;

public class SaveRemoteDialog extends DialogFragment {

    private static final String ARG_REMOTE = "org.twinone.irremote.arg.menu_main";
    public static final String DIALOG_TAG = "save_remote_dialog";
    private Remote mRemote;
    private EditText mRemoteName;
//    private OnRemoteSavedListener mListener;

    public static void showFor(Activity a, Remote remote) {
        SaveRemoteDialog.newInstance(remote).show(a.getFragmentManager(),
                DIALOG_TAG);
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
        show(a.getFragmentManager(), DIALOG_TAG);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRemote = (Remote) getArguments().getSerializable(ARG_REMOTE);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(getActivity() instanceof ProviderActivity)) {
            throw new ClassCastException(
                    "SaveButtonDialog must be added to an instance of ProviderActivity");
        }
    }

    private ProviderActivity getProvider() {
        return (ProviderActivity) getActivity();
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(
                R.layout.dialog_edit_text, null, false);
        mRemoteName = (EditText) view.findViewById(R.id.dialog_edittext_input);
        mRemoteName.setSelectAllOnFocus(true);
        if (mRemote != null) {
            mRemote.name = getSingularName(mRemote.name);
            mRemoteName.setText(mRemote.name);
        }
        MaterialDialog.Builder mb = Compat.getMaterialDialogBuilder(getActivity());
        mb.customView(view, true);

        mb.title(R.string.save_remote_title);
        mb.content(R.string.save_remote_text);
        mb.positiveText(R.string.save_remote_save);
        mb.neutralText(R.string.save_remote_preview);
        mb.negativeText(android.R.string.cancel);
        mb.callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                if (!checkAndUpdateRemoteName())
                    return;
                getProvider().performSaveRemote(mRemote);
            }

            @Override
            public void onNeutral(MaterialDialog dialog) {
                if (!checkAndUpdateRemoteName())
                    return;
                getProvider().requestPreviewRemote(mRemote);
            }
        });
        return mb.build();
    }

    private boolean checkAndUpdateRemoteName() {
        final String name = mRemoteName.getText().toString();
        if (name == null || name.isEmpty()) {
            Toast.makeText(getActivity(), R.string.err_name_empty,
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        mRemote.name = name;
        return true;
    }

    /**
     * Return a add-safe remote name so that when saving a remote under this name it won't overwrite an existing remote
     *
     * @param name The original name the user has selected
     * @return A write-safe name
     */
    private String getSingularName(String name) {
        List<String> names = Remote.getNames(getActivity());
        while (names.contains(name)) {
            Log.d("", "Names contains: " + name);
            name = name.trim();
            if (name == null || name.length() < 3) {
                name += " (2)";
                continue;
            }
            int l = name.length();
            if (name.charAt(l - 3) != '(' || name.charAt(l - 1) != ')') {
                name += " (2)";
                continue;
            }
            char n = name.charAt(l - 2);
            if (n < '0' || n > '9') {
                name += " (2)";
                continue;
            }
            int num = (int) (n - '0') + 1;
            name = name.substring(0, l - 3).trim() + " (" + num + ")";
        }
        return name;
    }


//    public void setListener(OnRemoteSavedListener listener) {
//        mListener = listener;
//    }

//    public interface OnRemoteSavedListener {
//        public void onRemoteSaved(String name);
//
//        public void onRequestPreview(Remote remote);
//    }
}
