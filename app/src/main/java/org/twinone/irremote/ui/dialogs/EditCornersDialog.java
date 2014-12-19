package org.twinone.irremote.ui.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.NumberPicker;

import org.twinone.irremote.R;

import java.util.ArrayList;

public class EditCornersDialog extends DialogFragment {

    private static final String ARG_CORNERS = "org.twinone.irremote.ui.EditCornersDialog.corners";
    private float[] mInitialCorners;
    private ArrayList<NumberPicker> mCorners;
    private NumberPicker mTL;
    private NumberPicker mTR;
    private NumberPicker mBL;
    private NumberPicker mBR;
    private NumberPicker mC;
    private CheckBox mAdvanced;
    private View mBottomContainer;
    private View mTopContainer;
    private View mCenterContainer;
    private OnCornersEditedListener mListener;

    public static EditCornersDialog newInstance(float[] initialCorners) {
        EditCornersDialog f = new EditCornersDialog();
        Bundle b = new Bundle();
        b.putFloatArray(ARG_CORNERS, initialCorners);
        f.setArguments(b);
        return f;
    }

    public void show(Activity a) {
        show(a.getFragmentManager(), "edit_corners_dialog");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInitialCorners = getArguments().getFloatArray(ARG_CORNERS);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        ViewGroup content = (ViewGroup) inflater.inflate(
                R.layout.dialog_edit_corners, null);

        mBottomContainer = content
                .findViewById(R.id.edit_corners_container_bottom);
        mTopContainer = content.findViewById(R.id.edit_corners_container_top);
        mCenterContainer = content
                .findViewById(R.id.edit_corners_container_center);
        mTL = (NumberPicker) content.findViewById(R.id.edit_corners_tl);
        mTR = (NumberPicker) content.findViewById(R.id.edit_corners_tr);
        mBL = (NumberPicker) content.findViewById(R.id.edit_corners_bl);
        mBR = (NumberPicker) content.findViewById(R.id.edit_corners_br);
        mC = (NumberPicker) content.findViewById(R.id.edit_corners_c);
        // mAdvanced = (CheckBox)
        // content.findViewById(R.id.edit_corners_advanced);
        // mAdvanced.setOnCheckedChangeListener(new OnCheckedChangeListener() {
        //
        // @Override
        // public void onCheckedChanged(CompoundButton buttonView,
        // boolean isChecked) {
        // setAdvanced(isChecked);
        // }
        // });

        mCorners = new ArrayList<NumberPicker>(4);
        mCorners.add(mTL);
        mCorners.add(mTR);
        mCorners.add(mBL);
        mCorners.add(mBR);
        mCorners.add(mC);

        // GradientDrawable sd = new GradientDrawable();
        // sd.setStroke((int) dpToPx(1), Color.parseColor("#ffffff"));

        for (NumberPicker np : mCorners) {
            // Value units are in (px * gridsize)
            np.setMinValue(0);
            np.setMaxValue(400);
            // np.setBackground(sd);
            // np.setBackground(ComponentUtils.getGradientDrawable(getActivity(),
            // Button.BG_INDIGO, false));
        }
        mTL.setValue((int) mInitialCorners[0]);
        mTR.setValue((int) mInitialCorners[2]);
        mBR.setValue((int) mInitialCorners[4]);
        mBL.setValue((int) mInitialCorners[6]);
        mC.setValue(getAverage());

        setAdvanced(false);

        AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
        ab.setView(content);
        ab.setPositiveButton(android.R.string.ok, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mListener != null) {
                    mListener.onCornersEdited(getCurrentCorners());
                }
            }

        });
        ab.setNegativeButton(android.R.string.cancel, null);
        ab.setTitle(R.string.edit_corners_dlgtit);
        // Don't animate dialogs with widgets inside
        return ab.create();
        // return AnimHelper.addAnimations(ab.create());
    }

    private int getAverage() {
        float total = 0;
        for (float f : mInitialCorners) {
            total += f;
        }
        return (int) total / mInitialCorners.length;
    }

    private boolean isAdvanced() {
        return false;
    }

    /**
     * Updates the UI
     */
    private void setAdvanced(boolean advanced) {
        mBottomContainer.setVisibility(advanced ? View.VISIBLE : View.GONE);
        mTopContainer.setVisibility(advanced ? View.VISIBLE : View.GONE);
        mCenterContainer.setVisibility(advanced ? View.GONE : View.VISIBLE);
    }

    protected float[] getCurrentCorners() {
        final float[] c = new float[8];
        if (isAdvanced()) {
            c[0] = c[1] = mTL.getValue();
            c[2] = c[3] = mTR.getValue();
            c[4] = c[5] = mBR.getValue();
            c[6] = c[7] = mBL.getValue();
        } else {
            c[0] = c[2] = c[4] = c[6] = mC.getValue();
            c[1] = c[3] = c[5] = c[7] = mC.getValue();
        }

        return c;
    }

    public void setListener(OnCornersEditedListener listener) {
        mListener = listener;
    }

    public interface OnCornersEditedListener {
        /**
         * @param corners The (8)corners in the order specified by
         *                {@link GradientDrawable#setCornerRadii(float[])}
         */
        public void onCornersEdited(float[] corners);
    }

}
