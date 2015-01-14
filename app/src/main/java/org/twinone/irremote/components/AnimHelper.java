package org.twinone.irremote.components;

import android.app.Activity;
import android.content.Intent;

import org.twinone.irremote.R;

public abstract class AnimHelper {
    public static void startActivity(Activity from, Intent to) {
        from.startActivity(to);
        from.overridePendingTransition(R.anim.slide_in_right, 0);
    }

    /**
     * Will not finish the calling activity
     */
    public static void onFinish(Activity a) {
        a.overridePendingTransition(0, R.anim.slide_out_right);
    }

//    public static Dialog showDialog(AlertDialog.Builder ab) {
//        final Dialog d = ab.create();
//        showDialog(d);
//        return d;
//    }

    /**
     * Convenience method for setting the dialog animations and showing it
     *w
     * @param d
     */
//    private static void showDialog(Dialog d) {
//        addAnimations(d);
//        d.show();
//    }

//    public static Dialog addAnimations(Dialog d) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
//            return d;
//        d.getWindow().getAttributes().windowAnimations = R.style.DialogAnims;
//        return d;
//    }
}
