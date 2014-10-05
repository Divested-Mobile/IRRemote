package org.twinone.irremote.ui.dialogs;

import org.twinone.irremote.R;
import org.twinone.irremote.components.AnimHelper;
import org.twinone.irremote.ui.ButtonView;

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
import android.widget.NumberPicker;

public class EditSizeDialog extends DialogFragment {

	private static final String ARG_INITIAL_W = "org.twinone.irremote.arg.initial_w";
	private static final String ARG_INITIAL_H = "org.twinone.irremote.arg.initial_h";

	public static void showFor(Activity a, int width, int height) {
		EditSizeDialog.newInstance(width, height).show(a.getFragmentManager(),
				"edit_text_dialog");
	}

	@Override
	public void onStart() {
		if (getDialog() != null) {
			AnimHelper.addAnimations(getDialog());
		}
		super.onStart();
	}

	public void show(Activity a) {
		show(a.getFragmentManager(), "save_remote_dialog");
	}

	public static EditSizeDialog newInstance(int w, int h) {
		EditSizeDialog f = new EditSizeDialog();
		Bundle b = new Bundle();
		b.putInt(ARG_INITIAL_W, w);
		b.putInt(ARG_INITIAL_H, h);
		f.setArguments(b);
		return f;
	}

	private int mWidth;
	private int mHeight;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mWidth = getArguments().getInt(ARG_INITIAL_W);
		mHeight = getArguments().getInt(ARG_INITIAL_H);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View sizeView = LayoutInflater.from(getActivity()).inflate(
				R.layout.edit_size_dialog, null);

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

	private OnSizeChangedListener mListener;

	public void setListener(OnSizeChangedListener listener) {
		mListener = listener;
	}

	public interface OnSizeChangedListener {
		public void onSizeChanged(int blocksW, int blocksH);
	}

}
