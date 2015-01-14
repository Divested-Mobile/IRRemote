package org.twinone.irremote.ui.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;

import org.twinone.irremote.R;
import org.twinone.irremote.compat.Compat;
import org.twinone.irremote.components.Button;
import org.twinone.irremote.components.TransmitOnTouchListener;
import org.twinone.irremote.ir.io.Transmitter;
import org.twinone.irremote.providers.ProviderActivity;
import org.twinone.irremote.ui.ButtonView;

public class SaveButtonDialog extends DialogFragment implements TextWatcher {

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

        mButton.bg = Button.BG_AMBER;
        mButton.setCornerRadius(Float.MAX_VALUE);
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

        MaterialDialog.Builder mb = Compat.getMaterialDialogBuilder(getActivity());
        mb.customView(view, true);

        mb.title(R.string.save_button_dlgtit);
        mb.content(R.string.save_button_dlgmsg);
        mb.positiveText(R.string.save_button_save);
        mb.callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                super.onPositive(dialog);
                mButton.text = mButtonText.getText().toString();
                if (mListener != null) mListener.onSaveButton(mButton);

            }
        });
        mb.negativeText(android.R.string.cancel);
        return mb.build();
    }


    public void setListener(OnSaveButton listener) {
        mListener = listener;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mButtonView.setText(s.toString(), true);
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    public interface OnSaveButton {
        public void onSaveButton(Button button);
    }
}
