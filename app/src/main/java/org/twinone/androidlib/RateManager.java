package org.twinone.androidlib;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import com.afollestad.materialdialogs.MaterialDialog;

import org.twinone.irremote.R;
import org.twinone.irremote.compat.Compat;

public class RateManager {

    private static final String PREF_FILE = "org.twinone.androidlib.share";
    private static final String PREF_KEY_COUNT = "org.twinone.androidlib.rate.count";
    /**
     * After how many times should the share dialog be shown for the first time?
     */
    private static final String PREF_KEY_OFFSET = "org.twinone.androidlib.rate.offset";
    /**
     * After the first time, each how many times should the dialog show?
     */
    private static final String PREF_KEY_REPEAT = "org.twinone.androidlib.rate.repeat";
    private static final int OFFSET_DEFAULT = 7;
    private static final int REPEAT_DEFAULT = 5;

    private static final String PREF_KEY_NEVER = "org.twinone.androidlib.rate.never";

    private static SharedPreferences getPrefs(Context c) {
        return c.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
    }

    public static void show(Context c) {
        show(c, true);
    }

    private static void show(Context c, boolean hasNeverButton) {
        int current = getPrefs(c).getInt(PREF_KEY_COUNT, 0) + 1;
        int offset = getPrefs(c).getInt(PREF_KEY_OFFSET, OFFSET_DEFAULT);
        int repeat = getPrefs(c).getInt(PREF_KEY_REPEAT, REPEAT_DEFAULT);
        boolean never = getPrefs(c).getBoolean(PREF_KEY_NEVER, false);

        if (!never) {
            if (current == offset || current > offset
                    && ((current - offset) % repeat) == 0) {
                getShareEditDialog(c, hasNeverButton).show();
            }
        }
        getPrefs(c).edit().putInt(PREF_KEY_COUNT, current).commit();
    }

    private static void setNever(Context c, boolean never) {
        getPrefs(c).edit().putBoolean(PREF_KEY_NEVER, never).commit();
    }

    private static MaterialDialog.Builder getShareEditDialog(final Context c,
                                                             boolean hasNeverButton) {

        MaterialDialog.Builder mb = Compat.getMaterialDialogBuilder(c);
        mb.cancelable(false);
        mb.title(R.string.rate_dlgtit);
        mb.content(R.string.rate_dlgmsg);
        mb.callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onNegative(MaterialDialog dialog) {
                super.onNegative(dialog);
                setNever(c, true);
            }

            @Override
            public void onPositive(MaterialDialog dialog) {
                super.onPositive(dialog);
                showMarket(c);
                setNever(c, true);
            }
        });
        mb.neutralText(R.string.rate_later);
        mb.positiveText(android.R.string.ok);
        return mb;
    }

    private static void showMarket(Context c) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + c.getPackageName()));
        if (c.getPackageManager().queryIntentActivities(intent, 0).size() > 0) {
            c.startActivity(intent);
        }
    }
}
