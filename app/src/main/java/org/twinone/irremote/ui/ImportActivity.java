package org.twinone.irremote.ui;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.gson.JsonSyntaxException;

import org.twinone.irremote.R;
import org.twinone.irremote.components.Remote;
import org.twinone.irremote.providers.ProviderActivity;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class ImportActivity extends ProviderActivity {

    private static final String TAG = ImportActivity.class.getSimpleName();
    private Remote mRemote;

    static String readSingleLine(InputStream input) {
        Scanner delim = new Scanner(input).useDelimiter("\\A");
        return delim.hasNext() ? delim.next() : "";
    }

    private void showImportStatus(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }

    @Nullable
    private Remote importRemote() {
        if (getIntent() == null || getIntent().getData() == null) {
            showImportStatus(R.string.empty_remote_tit);
            return null;
        }

        try {
            InputStream istream = getContentResolver().openInputStream(getIntent().getData());
            return Remote.deserialize(readSingleLine(istream));
        } catch (JsonSyntaxException e) {
            showImportStatus(R.string.import_invalid);
            return null;
        } catch (IOException e) {
            showImportStatus(R.string.import_notexist);
            Log.e(TAG, e.getMessage());
            return null;
        }
    }

    @Override
    public void onSaveRemote() {
        super.onSaveButton();
        finish();
    }

    @Override
    public void onRemotePreview() {
        super.onRemotePreview();
        requestSaveRemote(mRemote);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAction(ProviderActivity.ACTION_SAVE_REMOTE);
        setOrganize(false);
        mRemote = importRemote();
        if (mRemote == null) {
            finish();
        } else if (savedInstanceState == null) {
            requestSaveRemote(mRemote);
        }
    }
}
