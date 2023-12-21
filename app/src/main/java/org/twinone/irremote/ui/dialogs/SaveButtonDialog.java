package org.twinone.irremote.ui.dialogs;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;

import androidx.appcompat.app.AlertDialog;

import org.twinone.irremote.R;
import org.twinone.irremote.components.Button;
import org.twinone.irremote.components.TransmitOnTouchListener;
import org.twinone.irremote.ir.io.Transmitter;
import org.twinone.irremote.ui.ButtonView;

public class SaveButtonDialog extends DefaultDialogFragment implements TextWatcher {

    private static final String ARG_BUTTON = "org.twinone.irremote.arg.button";
    private Button mButton;
    private ButtonView mButtonView;
    private EditText mButtonText;
//    private OnSaveButton mListener;

    public static SaveButtonDialog newInstance(Button button) {
        if (button == null)
            throw new NullPointerException("Button cannot be null");
        SaveButtonDialog f = new SaveButtonDialog();
        Bundle b = new Bundle();
        b.putSerializable(ARG_BUTTON, button);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Button b = (Button) getArguments().getSerializable(ARG_BUTTON);
        mButton = new Button(b.text);
        mButton.code = b.code;
        mButton.ic = b.ic;

        mButton.x = mButton.y = 0;
        mButton.bg = Button.BG_TEAL;
        mButton.w = mButton.h = dpToPx(150);
    }
    private float dpToPx(float dp) {
        return dp * getResources().getDisplayMetrics().density;
    }

    @Override
    public AlertDialog.Builder getDefaultDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(
                R.layout.dialog_save_button, null, false);

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

        ScrollView scrollView = new ScrollView(getActivity());
        scrollView.addView(view);
        return new AlertDialog.Builder(getActivity())
                .setMessage(R.string.save_button_dlgmsg)
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> onCancel(dialog))
                .setPositiveButton(R.string.save_button_save, (dialog, which) -> {
                    mButton.text = mButtonText.getText().toString();
                    getProvider().performSaveButton(mButton);
                })
                .setTitle(R.string.save_button_dlgtit)
                .setView(scrollView);
    }


//    public void setListener(OnSaveButton listener) {
//        mListener = listener;
//    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mButtonView.setIcon(0);
        mButtonView.setText(s.toString(), true);
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

//    public interface OnSaveButton {
//        public void onSaveButton(Button button);
//    }
}
