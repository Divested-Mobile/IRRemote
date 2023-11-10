package org.twinone.irremote.ui.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import org.twinone.irremote.R;
import org.twinone.irremote.compat.Compat;

public class EditTextSizeDialog extends DialogFragment implements
        DialogInterface.OnClickListener {

    private static final String ARG_INITIAL_POSITION = "org.twinone.irremote.ui.ScrollDialog.initial_value";
    private int mInitialPosition;
    private TextView mText;
    private SeekBar mSlider;
    private int mDefaultValue;
    private int mMax;
    private int mMin;
    private OnTextSizeChangedListener mListener;

    public static void showFor(Activity a, int initialValue) {
        EditTextSizeDialog.newInstance(initialValue).show(
                a.getFragmentManager(), "scroll_dialog");
    }

    public static EditTextSizeDialog newInstance(int initialPosition) {
        EditTextSizeDialog f = new EditTextSizeDialog();
        Bundle b = new Bundle();
        b.putInt(ARG_INITIAL_POSITION, initialPosition);
        f.setArguments(b);
        return f;
    }

    public void show(Activity a) {
        show(a.getFragmentManager(), "scroll_dialog");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInitialPosition = getArguments().getInt(ARG_INITIAL_POSITION);

        mMin = getResources().getInteger(R.integer.def_min_text_size);
        mMax = getResources().getInteger(R.integer.def_max_text_size);
        mDefaultValue = getResources().getInteger(R.integer.def_text_size);

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(
                R.layout.slider_dialog, null);
        mText = (TextView) v.findViewById(R.id.delay_text);
        mSlider = (SeekBar) v.findViewById(R.id.delay_slider);

        CheckBox mDefault = (CheckBox) v.findViewById(R.id.delay_cb_default);
        mDefault.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    setProgress(mDefaultValue);
                }
                mSlider.setEnabled(!isChecked);
            }
        });

        mSlider.setMax(mMax - mMin);
        setProgress(mInitialPosition);

        mSlider.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onProgressChanged(SeekBar bar, int prog, boolean arg2) {
                setProgress(getProgress());
            }
        });

        MaterialDialog.Builder mb = Compat.getMaterialDialogBuilder(getActivity());
        mb.customView(v, true);
        mb.negativeText(android.R.string.cancel);
        mb.positiveText(android.R.string.ok);
        mb.callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                super.onPositive(dialog);
                if (mListener != null) mListener.onTextSizeChanged(getProgress());

            }
        });
        mb.title(R.string.edit_option_text_size);
        return mb.build();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                break;
        }
    }

    private int getProgress() {
        return mSlider.getProgress() + mMin;
    }

    /**
     * @param progress The progress as the user would see it
     */
    private void setProgress(int progress) {
        mSlider.setProgress(progress - mMin);
        mText.setText(String.valueOf(progress));
    }

    public void setListener(OnTextSizeChangedListener listener) {
        mListener = listener;
    }

    public interface OnTextSizeChangedListener {
        public void onTextSizeChanged(int newSize);
    }

}
