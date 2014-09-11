package org.twinone.irremote.providers;

import org.twinone.irremote.R;
import org.twinone.irremote.TransmitOnTouchListener;
import org.twinone.irremote.components.AnimHelper;
import org.twinone.irremote.components.Button;
import org.twinone.irremote.components.Remote;
import org.twinone.irremote.ir.Signal;
import org.twinone.irremote.ir.io.Transmitter;
import org.twinone.irremote.providers.common.CommonProviderFragment;
import org.twinone.irremote.providers.common.CommonProviderFragment.CommonProviderData;
import org.twinone.irremote.providers.globalcache.GCProviderFragment;
import org.twinone.irremote.providers.globalcache.GlobalCacheProviderData;
import org.twinone.irremote.ui.ButtonView;
import org.twinone.irremote.ui.SaveRemoteDialog;
import org.twinone.irremote.ui.SaveRemoteDialog.OnRemoteSavedListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

public class ProviderActivity extends Activity {

	private String mAction;

	public enum Provider {
		GLOBALCACHE, COMMON
	}

	public String getAction() {
		return mAction;
	}

	/**
	 * This triggers the standalone mode, a remote will be saved within the
	 * ProviderActivity
	 */
	public static final String ACTION_SAVE_REMOTE = "org.twinone.irremote.intent.action.save_remote";

	/**
	 * This triggers the callback mode.<br>
	 * When a button has been selected by the user it's details are sent back as
	 * intent extras to the calling activity via onActivityResult
	 */
	public static final String ACTION_GET_BUTTON = "org.twinone.irremote.intent.action.get_button";

	/**
	 * This extra contains a Button object representing the button that the user
	 * has chosen
	 */
	public static final String EXTRA_RESULT_BUTTON = "org.twinone.irremote.intent.extra.result_buttons";

	private Transmitter mTransmitter;

	protected int mCurrentType;
	private int mExitType;

	public void setCurrentType(int currentType) {
		mCurrentType = currentType;
	}

	public void setExitType(int exitType) {
		mExitType = exitType;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getIntent().getAction() == null) {
			throw new IllegalStateException(
					"ProviderActivity should be called with one of ACTION_SAVE_REMOTE of ACTION_GET_BUTTON specified");
		}

		mAction = getIntent().getAction();
		getActionBar().setDisplayHomeAsUpEnabled(true);

		setContentView(R.layout.activity_empty);

		if (savedInstanceState != null) {
			if (savedInstanceState.containsKey(SAVE_TITLE)) {
				setTitle(savedInstanceState.getString(SAVE_TITLE));
			}
		}

		addFragment(new CommonProviderFragment());

	}

	/**
	 * Use this method to send the selected button back to the calling activity
	 */

	public void saveButton(final Button result) {

		LayoutInflater li = LayoutInflater.from(this);
		View v = li.inflate(R.layout.dialog_save_button, null);
		final ButtonView bv = (ButtonView) v
				.findViewById(R.id.dialog_save_button_button);
		bv.setButton(result);
		bv.setOnTouchListener(new TransmitOnTouchListener(getTransmitter()));

		final AlertDialog.Builder ab = new AlertDialog.Builder(this);
		ab.setTitle(R.string.save_button_dlgtit);
		ab.setMessage(R.string.save_button_dlgmsg);
		ab.setView(v);
		ab.setNegativeButton(android.R.string.cancel, null);
		ab.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent i = new Intent();
						i.putExtra(EXTRA_RESULT_BUTTON, result);
						setResult(Activity.RESULT_OK, i);
						finish();
					}
				});
		AnimHelper.showDialog(ab);

	}

	/**
	 * Use this method to prompt the user to save this remote
	 * 
	 * @param r
	 */
	public void saveRemote(Remote remote) {
		SaveRemoteDialog dialog = SaveRemoteDialog.newInstance(remote);
		dialog.setListener(new OnRemoteSavedListener() {

			@Override
			public void onRemoteSaved(String name) {
				// Finish the activity, we've saved the remote
				Remote.setLastUsedRemoteName(ProviderActivity.this, name);
				Toast.makeText(ProviderActivity.this,
						R.string.remote_saved_toast, Toast.LENGTH_SHORT).show();
				finish();
			}
		});
		dialog.show(this);

	}

	@Override
	public boolean onNavigateUp() {
		if (mCurrentType == mExitType) {
			finish();
		} else {
			getFragmentManager().popBackStack();
		}
		return true;
	}

	private String mTitle;
	private static final String SAVE_TITLE = "save_title";

	@Override
	public void setTitle(CharSequence title) {
		super.setTitle(title);
		getActionBar().setTitle(title);
		mTitle = (String) title;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(SAVE_TITLE, mTitle);
	}

	public void transmit(Signal signal) {
		getTransmitter().transmit(signal);
	}

	public void addFragment(Fragment fragment) {
		getFragmentManager().beginTransaction()
				.replace(R.id.container, fragment).addToBackStack("default")
				.commit();
	}

	public Transmitter getTransmitter() {
		// Lazy initialization
		if (mTransmitter == null) {
			mTransmitter = Transmitter.getInstance(this);
		}
		return mTransmitter;
	}

	@Override
	public void finish() {
		super.finish();
		AnimHelper.onFinish(this);
	}

	public void addCommonProviderFragment(CommonProviderData data) {
		setExitType(CommonProviderData.TARGET_DEVICE_TYPE);

		mCurrentType = data.targetType;
		CommonProviderFragment frag = new CommonProviderFragment();
		Bundle args = new Bundle();
		args.putSerializable(CommonProviderFragment.ARG_DATA, data);
		frag.setArguments(args);
		getFragmentManager().beginTransaction().replace(R.id.container, frag)
				.addToBackStack("default").commit();
		// Update action bar back button:
	}

	public void addGCProviderFragment(GlobalCacheProviderData data) {
		setExitType(CommonProviderData.TARGET_DEVICE_TYPE);

		mCurrentType = data.targetType;
		GCProviderFragment frag = new GCProviderFragment();
		Bundle args = new Bundle();
		args.putSerializable(GCProviderFragment.ARG_URI_DATA, data);
		frag.setArguments(args);
		getFragmentManager().beginTransaction().replace(R.id.container, frag)
				.addToBackStack("default").commit();
		// Update action bar back button:
	}

	public void popAllFragments() {
		getFragmentManager().popBackStack(null,
				FragmentManager.POP_BACK_STACK_INCLUSIVE);
	}

	public void popFragment() {
		getFragmentManager().popBackStack();
	}

	public void switchTo(Provider provider) {
		popAllFragments();
		switch (provider) {
		case COMMON:
			addFragment(new CommonProviderFragment());
			break;
		case GLOBALCACHE:
			addFragment(new GCProviderFragment());
			break;
		}
	}

}
