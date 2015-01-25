package org.twinone.irremote.ui;

import android.app.Activity;
import android.content.Intent;

import com.afollestad.materialdialogs.MaterialDialog;

import org.twinone.androidlib.compat.ToolbarActivity;
import org.twinone.irremote.R;
import org.twinone.irremote.compat.Compat;
import org.twinone.irremote.components.AnimHelper;

public class EditRemoteActivity extends ToolbarActivity {

    private static final String EXTRA_REMOTE_NAME = "org.twinone.irremote.intent.extra.menu_main";
    private EditRemoteFragment mEditFragment;

    public static void show(Activity a, String remoteName) {
        Intent i = new Intent(a, EditRemoteActivity.class);
        i.putExtra(EXTRA_REMOTE_NAME, remoteName);
        AnimHelper.startActivity(a, i);
    }

    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(MainActivity.getRequestedOrientation(this));

        setContentView(R.layout.activity_empty);

        String mRemoteName = getIntent().getStringExtra(EXTRA_REMOTE_NAME);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            mEditFragment = (EditRemoteFragment) new EditRemoteFragment().showFor(this, mRemoteName);
        } else {
            mEditFragment = (EditRemoteFragment) getFragmentManager()
                    .findFragmentByTag(BaseRemoteFragment.FRAGMENT_TAG);
        }
    }

    @Override
    public void onBackPressed() {
        finishOrShowSaveDialog();
    }

    /**
     * @return True if the activity finished
     */
    private boolean finishOrShowSaveDialog() {
        if (mEditFragment.isEdited()) {
            showConfirmationDialog();
            return false;
        } else {
            finish();
            return true;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return onNavigateUp();
    }

    @Override
    public boolean onNavigateUp() {
        return finishOrShowSaveDialog();
    }

    private void showConfirmationDialog() {
        MaterialDialog.Builder mb = Compat.getMaterialDialogBuilder(this);
        mb.title(R.string.edit_confirmexitdlg_tit);
        mb.positiveText(R.string.edit_confirmexitdlg_save);
        mb.negativeText(R.string.edit_confirmexitdlg_discard);
        mb.callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                super.onPositive(dialog);
                saveRemote();
                finish();
            }

            @Override
            public void onNegative(MaterialDialog dialog) {
                super.onNegative(dialog);
                finish();
            }
        });
        mb.show();
    }

    private void saveRemote() {
        mEditFragment.saveRemote();
    }

    @Override
    public void finish() {
        super.finish();
        AnimHelper.onFinish(this);
    }
}
