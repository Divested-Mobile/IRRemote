package org.twinone.irremote.ui;

import org.twinone.irremote.R;
import org.twinone.irremote.components.AnimHelper;
import org.twinone.irremote.components.Remote;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class RenameRemoteDialog extends DialogFragment implements
		DialogInterface.OnClickListener {

	private static final String ARG_REMOTE = "org.twinone.irremote.arg.remote";

	private String mOriginalRemoteName;
	private EditText mNewRemoteName;

	public static void showFor(Activity a, String remoteName) {
		RenameRemoteDialog.newInstance(remoteName).show(a.getFragmentManager(),
				"save_remote_dialog");
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

	public static RenameRemoteDialog newInstance(String remoteName) {
		RenameRemoteDialog f = new RenameRemoteDialog();
		Bundle b = new Bundle();
		b.putString(ARG_REMOTE, remoteName);
		f.setArguments(b);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mOriginalRemoteName = (String) getArguments().getString(ARG_REMOTE);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View view = LayoutInflater.from(getActivity()).inflate(
				R.layout.dialog_edittext, null, false);
		mNewRemoteName = (EditText) view
				.findViewById(R.id.dialog_edittext_input);
		mNewRemoteName.setText(mOriginalRemoteName);

		AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
		ab.setView(view);

		ab.setTitle(R.string.rename_remote_title);
		ab.setMessage(getString(R.string.rename_remote_message,
				mOriginalRemoteName));
		ab.setPositiveButton(R.string.rename_remote_save, this);
		ab.setNegativeButton(android.R.string.cancel, null);

		return ab.create();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
		case DialogInterface.BUTTON_POSITIVE:
			final String newName = mNewRemoteName.getText().toString();

			Remote.rename(getActivity(), mOriginalRemoteName, newName);

			if (mListener != null)
				mListener.onRemoteRenamed(mOriginalRemoteName, newName);
			break;
		}
	}

	private OnRemoteRenamedListener mListener;

	public void setOnRemoteRenamedListener(OnRemoteRenamedListener listener) {
		mListener = listener;
	}

	public interface OnRemoteRenamedListener {
		public void onRemoteRenamed(String oldName, String newName);
	}

}
