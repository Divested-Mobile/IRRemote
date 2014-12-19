package org.twinone.irremote.providers.twinone;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import org.twinone.irremote.R;
import org.twinone.irremote.compat.ToolbarActivity;

public class UploadActivity extends ToolbarActivity {

    private static final String EXTRA_REMOTE_NAME = "org.twinone.irremote.extra.remote_name";

    public static void startFor(String remoteName, Context context) {
        Intent i = new Intent(context, UploadActivity.class);
        i.putExtra(EXTRA_REMOTE_NAME, remoteName);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (!getIntent().getExtras().containsKey(EXTRA_REMOTE_NAME)) {
            throw new RuntimeException(
                    "Call UploadActivity with UploadActivity.startFor()");
        }
        String mRemoteName = getIntent().getExtras().getString(EXTRA_REMOTE_NAME);

        setTitle(getString(R.string.title_upload, mRemoteName));
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new UploadFragment()).commit();
    }

    @Override
    public boolean onNavigateUp() {
        return onSupportNavigateUp();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}
