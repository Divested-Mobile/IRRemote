package org.twinone.irremote.ui.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;

import org.twinone.irremote.R;
import org.twinone.irremote.compat.Compat;

public class EditTextDialog extends DialogFragment {

    private static final String ARG_TEXT = "org.twinone.irremote.arg.initial_text";
    private String mOriginalText;
    private EditText mEditText;
    private OnTextChangedListener mListener;

    public static void showFor(Activity a, String remoteName) {
        EditTextDialog.newInstance(remoteName).show(a.getFragmentManager(),
                "edit_text_dialog");
    }

    public static EditTextDialog newInstance(String buttonText) {
        EditTextDialog f = new EditTextDialog();
        Bundle b = new Bundle();
        b.putString(ARG_TEXT, buttonText);
        f.setArguments(b);
        return f;
    }

    public void show(Activity a) {
        show(a.getFragmentManager(), "edit_text_dialog");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOriginalText = getArguments().getString(ARG_TEXT);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(
                R.layout.dialog_edit_text, null, false);
        mEditText = (EditText) view.findViewById(R.id.dialog_edittext_input);
        mEditText.setText(mOriginalText);

        MaterialDialog.Builder mb = Compat.getMaterialDialogBuilder(getActivity());
        mb.customView(view, false);

        mb.title(R.string.edit_button_title);
        mb.positiveText(R.string.rename_remote_save);
        mb.callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                super.onPositive(dialog);
                if (mListener != null) mListener.onTextChanged(mEditText.getText().toString());
            }
        });
        mb.negativeText(android.R.string.cancel);
        return mb.build();
    }

    public void setListener(OnTextChangedListener listener) {
        mListener = listener;
    }

    public interface OnTextChangedListener {
        public void onTextChanged(String newText);
    }

}
