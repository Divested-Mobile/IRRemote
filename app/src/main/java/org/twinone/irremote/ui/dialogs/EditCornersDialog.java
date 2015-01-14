package org.twinone.irremote.ui.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;

import org.twinone.irremote.R;
import org.twinone.irremote.compat.Compat;

public class EditCornersDialog extends DialogFragment {

    private OnCornersEditedListener mListener;


    public void show(Activity a) {
        show(a.getFragmentManager(), "edit_corners_dialog");
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        MaterialDialog.Builder mb = Compat.getMaterialDialogBuilder(getActivity());
        mb.items(R.array.corners);
        mb.itemsCallback(new MaterialDialog.ListCallback() {
            @Override
            public void onSelection(MaterialDialog materialDialog, View view, int which, CharSequence charSequence) {
                float size = 0;
                switch (which) {
                    case 0:
                        size = dpToPx(100);
                        break;
                    case 1:
                        size = dpToPx(16);
                        break;
                    case 2:
                        size = 0;
                        break;
                }
                mListener.onCornersEdited(size);
            }
        });
        mb.negativeText(android.R.string.cancel);
        mb.title(R.string.edit_corners_dlgtit);
        // Don't animate dialogs with widgets inside
        return mb.build();
        // return AnimHelper.addAnimations(ab.create());
    }

    public void setListener(OnCornersEditedListener listener) {
        mListener = listener;
    }


    private float dpToPx(float dp) {
        return dp * getResources().getDisplayMetrics().density;
    }


    public interface OnCornersEditedListener {
        /**
         * @param corner The (8)corners in the order specified by
         *               {@link android.graphics.drawable.GradientDrawable#setCornerRadii(float[])}
         */
        public void onCornersEdited(float corner);
    }

}
