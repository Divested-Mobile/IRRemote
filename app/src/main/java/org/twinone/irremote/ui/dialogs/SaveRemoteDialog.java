package org.twinone.irremote.ui.dialogs;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import org.twinone.irremote.R;
import org.twinone.irremote.components.Remote;

import java.util.List;

public class SaveRemoteDialog extends DefaultDialogFragment {

    private static final String ARG_REMOTE = "org.twinone.irremote.arg.menu_main";
    private Remote mRemote;
    private EditText mRemoteName;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRemote = (Remote) getArguments().getSerializable(ARG_REMOTE);
    }

    @Override
    public AlertDialog.Builder getDefaultDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(
                R.layout.dialog_edit_text, null, false);
        mRemoteName = (EditText) view.findViewById(R.id.dialog_edittext_input);
        mRemoteName.setSelectAllOnFocus(true);
        if (mRemote != null) {
            mRemote.name = getUniqueName(mRemote.name);
            mRemoteName.setText(mRemote.name);
        }

        ScrollView scrollView = new ScrollView(getActivity());
        scrollView.addView(view);
        return new AlertDialog.Builder(getActivity())
                .setMessage(R.string.save_remote_text)
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> onCancel(dialog))
                .setNeutralButton(R.string.save_remote_preview, (dialog, which) -> {
                    if (!checkAndUpdateRemoteName())
                        return;
                    getProvider().requestPreviewRemote(mRemote);
                })
                .setPositiveButton(R.string.save_remote_save, (dialog, which) -> {
                    if (!checkAndUpdateRemoteName())
                        return;
                    getProvider().performSaveRemote(mRemote);
                })
                .setTitle(R.string.save_remote_title)
                .setView(scrollView);
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

    private static final String SPECIAL_CHARS = "\\/%<>=";

    private String stripSpecialChars(String name) {
        for (int i = 0; i < SPECIAL_CHARS.length(); i++) {
            Log.d("", "Special chars 1:" + SPECIAL_CHARS.charAt(0));
            name = name.replace(SPECIAL_CHARS.charAt(i), '-');
        }
        return name;
    }

    private String getUniqueName(String name) {
        name = stripSpecialChars(name);
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
