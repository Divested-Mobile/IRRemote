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

import java.util.List;

import org.twinone.irremote.ir.IRTransmitter;
import org.twinone.irremote.ui.SelectRemoteListView.OnSelectListener;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class RemoteActivity extends Activity implements OnSelectListener {

	private Remote mRemote;
	private IRTransmitter mTransmitter;

	private static final String SELECT_REMOTE_TAG = "select_remote";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		List<String> remoteNames = Remote.getNames(this);
		if (remoteNames.isEmpty()) {
			Intent i = new Intent(this, DBActivity.class);
			startActivity(i);
			return;
		}

		mTransmitter = new IRTransmitter(this);
		mTransmitter.setShowBlinker(true);

		mRemote = Remote.load(this, remoteNames.get(0));

		setContentView(R.layout.activity_remote);
		showSelectRemoteDialog();
	}

	@Override
	public void setTitle(CharSequence title) {
		super.setTitle(title);
		getActionBar().setTitle(title);
	}

	public void showSelectRemoteDialog() {
		if (isSelectRemoteDialogShown()) {
			removeSelectRemoteDialog();
		}
		getFragmentManager().beginTransaction()
				.add(new SelectRemoteDialogFragment(), SELECT_REMOTE_TAG)
				.commit();
	}

	public boolean isSelectRemoteDialogShown() {
		return (getFragmentManager().findFragmentByTag(SELECT_REMOTE_TAG) != null);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (isSelectRemoteDialogShown()) {
			showSelectRemoteDialog();
		}
	}

	public void removeSelectRemoteDialog() {
		final Fragment fragment = getFragmentManager().findFragmentByTag(
				SELECT_REMOTE_TAG);
		getFragmentManager().beginTransaction().remove(fragment).commit();
	}

	public Remote getRemote() {
		return mRemote;
	}

	public void transmit(boolean common, int id) {
		final Button b = mRemote.getButton(common, id);
		mTransmitter.transmit(b.getSignal());
	}

	@Override
	public void onRemoteSelected(int position, String remoteName) {
		mRemote = Remote.load(this, remoteName);

		removeSelectRemoteDialog();
		getFragmentManager().beginTransaction()
				.replace(R.id.container, new RemoteMainFragment()).commit();
	}

	@Override
	public void onAddRemoteSelected() {
		Intent i = new Intent(this, DBActivity.class);
		startActivity(i);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.remote, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_action_select_remote:
			showSelectRemoteDialog();
			return true;
		}
		return false;
	}
}
