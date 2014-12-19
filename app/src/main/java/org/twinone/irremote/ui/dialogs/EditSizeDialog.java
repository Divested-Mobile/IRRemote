package org.twinone.irremote.ui.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import org.twinone.irremote.R;
import org.twinone.irremote.components.AnimHelper;

public class EditSizeDialog extends DialogFragment {

    private static final String ARG_INITIAL_W = "org.twinone.irremote.arg.initial_w";
    private static final String ARG_INITIAL_H = "org.twinone.irremote.arg.initial_h";
    private int mWidth;
    private int mHeight;
    private OnSizeChangedListener mListener;

    public static void showFor(Activity a, int width, int height) {
        EditSizeDialog.newInstance(width, height).show(a.getFragmentManager(),
                "edit_size_dialog");
    }

    public static EditSizeDialog newInstance(int w, int h) {
        EditSizeDialog f = new EditSizeDialog();
        Bundle b = new Bundle();
        b.putInt(ARG_INITIAL_W, w);
        b.putInt(ARG_INITIAL_H, h);
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
        show(a.getFragmentManager(), "edit_size_dialog");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWidth = getArguments().getInt(ARG_INITIAL_W);
        mHeight = getArguments().getInt(ARG_INITIAL_H);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View sizeView = LayoutInflater.from(getActivity()).inflate(
                R.layout.dialog_edit_size, null);

        final NumberPicker npw = (NumberPicker) sizeView
                .findViewById(R.id.sizepicker_width);
        npw.setMaxValue(40);
        npw.setMinValue(1);
        npw.setValue(mWidth);
        final NumberPicker nph = (NumberPicker) sizeView
                .findViewById(R.id.sizepicker_height);
        nph.setMaxValue(40);
        nph.setMinValue(1);
        nph.setValue(mHeight);

        final AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
        ab.setTitle(R.string.edit_button_title);

        ab.setView(sizeView);

        ab.setNegativeButton(android.R.string.cancel, null);
        ab.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mListener != null) {
                            mListener.onSizeChanged(npw.getValue(), nph.getValue());
                        }

                    }
                });
        return ab.create();
    }

    public void setListener(OnSizeChangedListener listener) {
        mListener = listener;
    }

    public interface OnSizeChangedListener {
        public void onSizeChanged(int blocksW, int blocksH);
    }

}
