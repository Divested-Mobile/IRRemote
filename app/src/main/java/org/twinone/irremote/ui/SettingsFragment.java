package org.twinone.irremote.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import org.twinone.irremote.R;
import org.twinone.irremote.util.FileUtils;

import java.io.File;
import java.util.List;

public class SettingsFragment extends PreferenceFragment implements
        OnSharedPreferenceChangeListener, OnPreferenceClickListener,
        OnPreferenceChangeListener {

    private static final int BG_REQUEST_CODE = 1337;

    private ListPreference mBackground;
    private boolean mNewFixButtonsVal;

    @Override
    public void onResume() {
        super.onResume();

        getPreferenceManager().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
        if (mBackground.getValue().equals("")) {
            String key = getString(R.string.pref_key_bg);
            String value = getString(R.string.pref_val_bg_gallery);
            getPreferenceManager().getSharedPreferences().edit()
                    .putString(key, value).apply();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager pm = getPreferenceManager();
        pm.setSharedPreferencesName("default");
        pm.setSharedPreferencesMode(Context.MODE_PRIVATE);
        addPreferencesFromResource(R.xml.prefs);

        mBackground = (ListPreference) findPreference(getString(R.string.pref_key_bg));
        mBackground.setOnPreferenceClickListener(this);

        Preference mFixButtons = findPreference(getString(R.string.pref_key_fix));
        mFixButtons.setOnPreferenceChangeListener(this);

    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference.getKey().equals(getString(R.string.pref_key_bg))) {
            ((ListPreference) preference).setValue("");
        }
        return false;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference.getKey().equals(getString(R.string.pref_key_fix))) {
            mNewFixButtonsVal = (Boolean) newValue;
            showConfirmFixButtonsDialog();
            return false;
        }
        return true;
    }

    private void showConfirmFixButtonsDialog() {
        AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
        ab.setTitle(R.string.pref_dlg_tit_fix);
        ab.setTitle(R.string.pref_dlg_msg_fix);
        ab.setNegativeButton(android.R.string.cancel, null);
        ab.setPositiveButton(android.R.string.ok, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveFixButtons();
            }
        });
        ab.show();
    }

    private void saveFixButtons() {
        Editor e = getPreferenceManager().getSharedPreferences().edit();
        String key = getString(R.string.pref_key_fix);
        e.putBoolean(key, mNewFixButtonsVal).apply();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
        if (key.equals(getString(R.string.pref_key_bg))) {
            final String value = sp.getString(getString(R.string.pref_key_bg),
                    getString(R.string.pref_val_bg_none));
            if (value.equals(getString(R.string.pref_val_bg_gallery))) {
                startChooseBackgroundActivity();
            } else {
                sp.edit().remove(key).apply();
            }
        }

    }

    private void startChooseBackgroundActivity() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        List<ResolveInfo> ris = getActivity().getPackageManager()
                .queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        if (ris.size() > 0) {
            startActivityForResult(Intent.createChooser(intent, null),
                    BG_REQUEST_CODE);
        } else {
            Toast.makeText(getActivity(), "Error - No gallery app",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int req, int res, Intent data) {
        super.onActivityResult(req, res, data);
        if (res == Activity.RESULT_OK) {

            switch (req) {
                case BG_REQUEST_CODE:
                    File out = new File(getActivity().getFilesDir(), "background");
                    FileUtils.saveImage(getActivity(), data.getData(), out);

                    Uri uri = Uri.fromFile(out);
                    getPreferenceManager()
                            .getSharedPreferences()
                            .edit()
                            .putString(getString(R.string.pref_key_bg_uri),
                                    uri.toString()).apply();

                    Toast.makeText(getActivity(), R.string.bg_changed_ok,
                            Toast.LENGTH_LONG).show();
            }
        }
    }

}
