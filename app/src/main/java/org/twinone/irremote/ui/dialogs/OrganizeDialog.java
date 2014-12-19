package org.twinone.irremote.ui.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

import org.twinone.irremote.R;
import org.twinone.irremote.components.AnimHelper;
import org.twinone.irremote.components.RemoteOrganizer;

public class OrganizeDialog extends DialogFragment implements
        DialogInterface.OnClickListener {

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

        AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
        View root = LayoutInflater.from(getActivity()).inflate(
                R.layout.dialog_organize, null);

        mIcons = (CheckBox) root.findViewById(R.id.organize_icons);
        mColors = (CheckBox) root.findViewById(R.id.organize_colors);
        mPositions = (CheckBox) root.findViewById(R.id.organize_positions);
        mCorners = (CheckBox) root.findViewById(R.id.organize_corners);

        ab.setView(root);
        ab.setNegativeButton(android.R.string.cancel, null);
        ab.setPositiveButton(android.R.string.ok, this);

        ab.setTitle(R.string.organize_dlgtit);
        return AnimHelper.addAnimations(ab.create());
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                if (mListener != null)
                    mListener.onOrganize(getFlags());
                break;
        }
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
        public void onOrganize(int flags);
    }

}
