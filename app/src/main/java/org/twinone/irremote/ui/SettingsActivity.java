package org.twinone.irremote.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import org.twinone.androidlib.compat.ToolbarActivity;
import org.twinone.irremote.R;
import org.twinone.irremote.components.AnimHelper;

public class SettingsActivity extends ToolbarActivity {

    public static SharedPreferences getPreferences(Context c) {
        return c.getSharedPreferences("default", Context.MODE_PRIVATE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_empty);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getFragmentManager().beginTransaction()
                .replace(R.id.container, new SettingsFragment()).commit();
    }

    @Override
    public boolean onSupportNavigateUp() {
        return onNavigateUp();
    }

    @Override
    public boolean onNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        MainActivity.recreate(SettingsActivity.this);
        AnimHelper.onFinish(SettingsActivity.this);
    }

}
