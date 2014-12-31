package org.twinone.irremote.ui.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import org.twinone.irremote.R;

public class EditCornersDialog extends DialogFragment {

    private OnCornersEditedListener mListener;


    public void show(Activity a) {
        show(a.getFragmentManager(), "edit_corners_dialog");
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());

        ab.setItems(R.array.corners,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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
        ab.setNegativeButton(android.R.string.cancel, null);
        ab.setTitle(R.string.edit_corners_dlgtit);
        // Don't animate dialogs with widgets inside
        return ab.create();
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
