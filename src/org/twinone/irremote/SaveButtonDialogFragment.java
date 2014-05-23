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

import org.twinone.irremote.ui.SelectRemoteListView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SaveButtonDialogFragment extends DialogFragment implements
		DialogInterface.OnClickListener {

	private static final String ARG_BUTTON = "org.twinone.irremote.arg.button";

	public static void showFor(Activity a, Button button) {
		SaveButtonDialogFragment.newInstance(button).show(
				a.getFragmentManager(), "save_button_dialog");
	}

	private static SaveButtonDialogFragment newInstance(Button button) {
		SaveButtonDialogFragment f = new SaveButtonDialogFragment();
		Bundle b = new Bundle();
		b.putSerializable(ARG_BUTTON, button);
		f.setArguments(b);
		return f;
	}

	private Button mTargetButton;
	private EditText mButtonName;
	private SelectRemoteListView mListView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mTargetButton = (Button) getArguments().getSerializable(ARG_BUTTON);
		Log.d("", "Target: " + mTargetButton + " " + mTargetButton.text);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View view = LayoutInflater.from(getActivity()).inflate(
				R.layout.dialog_save_button, null, false);
		mButtonName = (EditText) view.findViewById(R.id.save_button_name);
		mButtonName.setText(mTargetButton.text);
		mListView = (SelectRemoteListView) view
				.findViewById(R.id.select_remote_listview);

		AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
		ab.setView(view);
		ab.setTitle(R.string.save_button_title);

		ab.setPositiveButton(R.string.save_button_save, this);
		ab.setNegativeButton(android.R.string.cancel, null);
		return ab.create();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
		case DialogInterface.BUTTON_POSITIVE:
			mTargetButton.text = mButtonName.getText().toString();
			if (mListView.isRemoteSelected()) {
				Remote.addButton(getActivity(),
						mListView.getSelectedRemoteName(), mTargetButton);
			} else {
				Toast.makeText(getActivity(), R.string.select_remote_first,
						Toast.LENGTH_SHORT).show();
			}
			break;
		}
	}

}
