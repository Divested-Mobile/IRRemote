package org.twinone.irremote.providers.twinone;

import org.twinone.irremote.R;
import org.twinone.irremote.providers.ProviderActivity;
import org.twinone.irremote.providers.twinone.RemoteDownloader.DownloadListener;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class DownloadActivity extends ProviderActivity implements
		DownloadListener {

	private static final String PARAM_ID = "id";

	private RemoteDownloader mDownloader;
	private String mRemoteId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (!validateIntent()) {
			finish();
			return;
		}

		// If the id is not in the URI, ask the user to input it manually
		if (extractIdFromIntent()) {
			onIdAvailable();
		} else {
			getIdFromUser();
		}
	}

	private boolean extractIdFromIntent() {
		String id = getIntent().getData().getQueryParameter(PARAM_ID);
		if (id == null || id.isEmpty())
			return false;
		mRemoteId = id;
		return true;
	}

	private void getIdFromUser() {
		AlertDialog.Builder ab = new AlertDialog.Builder(this);
		ab.setTitle(R.string.download_dlgtit);
		ab.setMessage(R.string.download_dlgmsg);
		final View v = LayoutInflater.from(this).inflate(
				R.layout.dialog_edit_text, null);
		final EditText et = (EditText) v
				.findViewById(R.id.dialog_edittext_input);

		ab.setView(v);
		ab.setPositiveButton(R.string.download_ok, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				mRemoteId = et.getText().toString();
				onIdAvailable();
			}
		});
		ab.setNegativeButton(android.R.string.cancel, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});
		ab.show();
	}

	private void onIdAvailable() {
		mDownloader = new RemoteDownloader();
		mDownloader.download(mRemoteId, this);
	}

	@Override
	public void onDownload(TransferableRemoteDetails details, int statusCode) {
		// TODO show results or inform error
		saveRemote(details.remote);
	}

	private boolean validateIntent() {
		Uri data = getIntent().getData();
		if (data == null)
			return false;
		if (!data.getScheme().equals(getPackageName()))
			return false;
		if (!data.getAuthority().equals("launch"))
			return false;
		return true;
	}
}
