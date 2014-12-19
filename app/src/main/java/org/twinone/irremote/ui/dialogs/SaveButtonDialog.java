package org.twinone.irremote.ui.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import org.twinone.irremote.R;
import org.twinone.irremote.components.AnimHelper;
import org.twinone.irremote.components.Button;
import org.twinone.irremote.ir.io.Transmitter;
import org.twinone.irremote.providers.ProviderActivity;
import org.twinone.irremote.ui.ButtonView;
import org.twinone.irremote.util.TransmitOnTouchListener;

public class SaveButtonDialog extends DialogFragment implements
        DialogInterface.OnClickListener, TextWatcher {

    private static final String ARG_BUTTON = "org.twinone.irremote.arg.button";
    private Button mButton;
    private ButtonView mButtonView;
    private EditText mButtonText;
    private OnSaveButton mListener;

    public static void showFor(Activity a, Button button) {
        SaveButtonDialog.newInstance(button).show(a);
    }

    public static SaveButtonDialog newInstance(Button button) {
        if (button == null)
            throw new NullPointerException("Button cannot be null");
        SaveButtonDialog f = new SaveButtonDialog();
        Bundle b = new Bundle();
        b.putSerializable(ARG_BUTTON, button);
        f.setArguments(b);
        return f;
    }

    public void show(Activity a) {
        show(a.getFragmentManager(), "save_button_dialog");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mButton = (Button) getArguments().getSerializable(ARG_BUTTON);
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
                R.layout.dialog_save_button, null, false);

        mButtonText = (EditText) view
                .findViewById(R.id.dialog_save_button_text);
        mButtonText.setText(mButton.text);
        mButtonText.addTextChangedListener(this);
        mButtonView = (ButtonView) view
                .findViewById(R.id.dialog_save_button_button);
        mButtonView.setButton(mButton);

        Transmitter transmitter = getProvider().getTransmitter();
        if (transmitter != null)
            mButtonView.setOnTouchListener(new TransmitOnTouchListener(
                    transmitter));

        AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
        ab.setView(view);

        ab.setTitle(R.string.save_button_dlgtit);
        ab.setMessage(R.string.save_button_dlgmsg);
        ab.setPositiveButton(R.string.save_button_save, this);
        ab.setNegativeButton(android.R.string.cancel, null);
        return AnimHelper.addAnimations(ab.create());
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                mButton.text = mButtonText.getText().toString();
                if (mListener != null)
                    mListener.onSaveButton(mButton);
        }
    }

    public void setListener(OnSaveButton listener) {
        mListener = listener;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mButtonView.setText(s.toString(), true);
    }

    @Override
    public void afterTextChanged(Editable s) {
        // TODO Auto-generated method stub

    }

    public interface OnSaveButton {
        public void onSaveButton(Button button);
    }
}
