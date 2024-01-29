package org.twinone.irremote.providers;

import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.twinone.androidlib.compat.ToolbarActivity;
import org.twinone.irremote.R;
import org.twinone.irremote.components.Button;
import org.twinone.irremote.components.Remote;
import org.twinone.irremote.components.RemoteOrganizer;
import org.twinone.irremote.ir.io.Transmitter;
import org.twinone.irremote.ui.dialogs.RemotePreviewDialog;
import org.twinone.irremote.ui.dialogs.SaveButtonDialog;
import org.twinone.irremote.ui.dialogs.SaveRemoteDialog;

public abstract class ProviderActivity extends ToolbarActivity implements ProviderInterface {

    /**
     * The user will select a menu_main which will be saved directly
     */
    public static final String ACTION_SAVE_REMOTE = "org.twinone.irremote.intent.action.save_remote";
    /**
     * The user will select a button that will be returned to the calling
     * activity
     */
    public static final String ACTION_GET_BUTTON = "org.twinone.irremote.intent.action.get_button";
    /**
     * This extra contains a Button object representing the button that the user
     * has chosen
     */
    public static final String EXTRA_RESULT_BUTTON = "org.twinone.irremote.intent.extra.result_buttons";
    /**
     * If specified, {@link DefaultProviderActivity} will open this provider instead of
     * the default
     */
    public static final String EXTRA_PROVIDER = "org.twinone.irremote.intent.extra.provider_name";

    private String mAction;
    private Transmitter mTransmitter;
    private boolean mOrganize = true;

    private void showSaveRemoteDialog(AppCompatActivity activity, Remote remote) {
        SaveRemoteDialog.newInstance(remote).show(activity);
    }

    @Override
    public void requestSaveButton(Button button) {
        SaveButtonDialog.newInstance(button).show(this);
    }

    @Override
    public void requestPreviewRemote(Remote remote) {
        RemotePreviewDialog.newInstance(remote).show(this);
    }

    @Override
    public void performSaveButton(Button button) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_RESULT_BUTTON, button);
        setResult(AppCompatActivity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void requestSaveRemote(Remote remote) {
        remote.stripInvalidButtons();
        if (ACTION_GET_BUTTON.equals(getIntent().getAction())) {
            requestPreviewRemote(remote);
        } else {
            showSaveRemoteDialog(this, remote);
        }
    }

    protected void organizeRemote(Remote remote) {
        new RemoteOrganizer(this).updateWithoutSaving(remote);
        RemoteOrganizer.addIcons(remote, false);
    }

    @Override
    public void performSaveRemote(Remote remote) {
        if (mOrganize && remote.details.organize) {
            organizeRemote(remote);
        }
        remote.save(this);
        Remote.setLastUsedRemoteName(this, remote.name);
        Toast.makeText(this, R.string.remote_saved_toast, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public String getAction() {
        return mAction;
    }

    public void setAction(String action) {
        mAction = action;
    }

    @Override
    public boolean getOrganize() {
        return mOrganize;
    }

    public void setOrganize(boolean organize) {
        mOrganize = organize;
    }

    @Override
    public Transmitter getTransmitter() {
        if (mTransmitter == null) {
            mTransmitter = Transmitter.getInstance(this);
        }
        return mTransmitter;
    }

    @Override
    public void onSaveRemote() {
    }

    @Override
    public void onRemotePreview() {
    }

    @Override
    public void onSaveButton() {
    }
}
