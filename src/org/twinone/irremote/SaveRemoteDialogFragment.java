/*
 * Copyright 2014 Luuk Willemsen (Twinone)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.twinone.irremote;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class SaveRemoteDialogFragment extends DialogFragment implements
		DialogInterface.OnClickListener {

	private static final String ARG_REMOTE = "org.twinone.irremote.arg.remote";

	public static void showFor(Activity a, Remote remote) {
		SaveRemoteDialogFragment.newInstance(remote).show(
				a.getFragmentManager(), "save_remote_dialog");
	}

	public void show(Activity a) {
		show(a.getFragmentManager(), "save_remote_dialog");
	}

	public static SaveRemoteDialogFragment newInstance(Remote remote) {
		SaveRemoteDialogFragment f = new SaveRemoteDialogFragment();
		Bundle b = new Bundle();
		b.putSerializable(ARG_REMOTE, remote);
		f.setArguments(b);
		return f;
	}

	private Remote mTarget;
	private EditText mRemoteName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mTarget = (Remote) getArguments().getSerializable(ARG_REMOTE);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View view = LayoutInflater.from(getActivity()).inflate(
				R.layout.dialog_save_remote, null, false);
		mRemoteName = (EditText) view.findViewById(R.id.save_remote_name);
		mRemoteName.setText(mTarget.name);

		AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
		ab.setView(view);

		ab.setTitle(R.string.save_remote_title);
		ab.setMessage(R.string.save_remote_text);
		ab.setPositiveButton(R.string.save_remote_save, this);
		ab.setNegativeButton(android.R.string.cancel, null);

		return ab.create();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
		case DialogInterface.BUTTON_POSITIVE:
			mTarget.name = mRemoteName.getText().toString();
			mTarget.save(getActivity());
			if (mListener != null)
				mListener.onRemoteSaved(mTarget.name);
			break;
		}
	}

	private OnRemoteSavedListener mListener;

	public void setListener(OnRemoteSavedListener listener) {
		mListener = listener;
	}

	public interface OnRemoteSavedListener {
		public void onRemoteSaved(String name);
	}
}
