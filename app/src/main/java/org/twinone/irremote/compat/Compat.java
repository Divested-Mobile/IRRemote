package org.twinone.irremote.compat;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import org.twinone.irremote.R;

/**
 * Created by twinone on 1/14/15.
 */
public class Compat {

    public static final MaterialDialog.Builder getMaterialDialogBuilder(Context c) {
        MaterialDialog.Builder mb = new MaterialDialog.Builder(c);
        mb.theme(Theme.DARK);
        mb.positiveColorRes(R.color.material_teal_200);
        mb.negativeColorRes(R.color.material_teal_200);
        mb.neutralColorRes(R.color.material_teal_200);
        return mb;
    }


}
