package org.twinone.irremote.ui.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

import com.afollestad.materialdialogs.MaterialDialog;

import org.twinone.irremote.R;
import org.twinone.irremote.compat.Compat;
import org.twinone.irremote.components.RemoteOrganizer;

public class OrganizeDialog extends DialogFragment {

    private CheckBox mIcons;
    private CheckBox mColors;
    private CheckBox mPositions;
    private CheckBox mCorners;
    private OrganizeListener mListener;

    public void show(Activity a) {
        show(a.getFragmentManager(), "organize_dialog");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View root = LayoutInflater.from(getActivity()).inflate(
                R.layout.dialog_organize, null);

        mIcons = (CheckBox) root.findViewById(R.id.organize_icons);
        mColors = (CheckBox) root.findViewById(R.id.organize_colors);
        mPositions = (CheckBox) root.findViewById(R.id.organize_positions);
        mCorners = (CheckBox) root.findViewById(R.id.organize_corners);

        MaterialDialog.Builder mb = Compat.getMaterialDialogBuilder(getActivity());
        mb.customView(root, false);
        mb.negativeText(android.R.string.cancel);
        mb.positiveText(android.R.string.ok);
        mb.callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                super.onPositive(dialog);
                if (mListener != null) mListener.onOrganizeRequested(getFlags());
            }
        });

        mb.title(R.string.organize_dlgtit);
        return mb.build();
    }


    private int getFlags() {
        int flags = 0;
        if (mIcons.isChecked())
            flags |= RemoteOrganizer.FLAG_ICON;
        if (mColors.isChecked())
            flags |= RemoteOrganizer.FLAG_COLOR;
        if (mPositions.isChecked())
            flags |= RemoteOrganizer.FLAG_POSITION;
        if (mCorners.isChecked())
            flags |= RemoteOrganizer.FLAG_CORNERS;
        return flags;
    }

    public void setListener(OrganizeListener listener) {
        mListener = listener;
    }

    public interface OrganizeListener {
        public void onOrganizeRequested(int flags);
    }

}
