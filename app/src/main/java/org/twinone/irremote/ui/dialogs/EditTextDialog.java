package org.twinone.irremote.ui.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import org.twinone.irremote.R;
import org.twinone.irremote.components.AnimHelper;

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

    @Override
    public void onStart() {
        if (getDialog() != null) {
            AnimHelper.addAnimations(getDialog());
        }
        super.onStart();
    }

    public void show(Activity a) {
        show(a.getFragmentManager(), "edit_text_dialog");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOriginalText = (String) getArguments().getString(ARG_TEXT);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(
                R.layout.dialog_edit_text, null, false);
        mEditText = (EditText) view.findViewById(R.id.dialog_edittext_input);
        mEditText.setText(mOriginalText);

        AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
        ab.setView(view);

        ab.setTitle(R.string.edit_button_title);
        ab.setPositiveButton(R.string.rename_remote_save,
                new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onTextChanged(mEditText.getText().toString());
                    }
                });
        ab.setNegativeButton(android.R.string.cancel, null);

        return ab.create();
    }

    public void setListener(OnTextChangedListener listener) {
        mListener = listener;
    }

    public interface OnTextChangedListener {
        public void onTextChanged(String newText);
    }

}
