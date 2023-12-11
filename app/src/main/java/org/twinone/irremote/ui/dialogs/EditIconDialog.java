package org.twinone.irremote.ui.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.core.graphics.drawable.DrawableCompat;

import com.afollestad.materialdialogs.MaterialDialog;

import org.twinone.irremote.R;
import org.twinone.irremote.compat.Compat;
import org.twinone.irremote.components.ComponentUtils;

public class EditIconDialog extends DialogFragment implements
        DialogInterface.OnClickListener {

    private static final String ARG_ICON = "org.twinone.irremote.ui.SelectIconDialog.icon";
    private BaseAdapter mAdapter;
    private int[] mIconIds;
    private OnIconSelectedListener mListener;

    public static EditIconDialog newInstance(int color) {
        EditIconDialog f = new EditIconDialog();
        Bundle b = new Bundle();
        b.putInt(ARG_ICON, color);
        f.setArguments(b);
        return f;
    }

    public void show(Activity a) {
        show(a.getFragmentManager(), "edit_icon_dialog");
    }


    private float dpToPx(float dp) {
        return dp * getActivity().getResources().getDisplayMetrics().density;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mIconIds = ComponentUtils.ICON_IDS;

        GridView view = new GridView(getActivity());
        int size = (int) dpToPx(48);
        view.setColumnWidth(size);

        // Display as many columns as possible to fill the available space.
        view.setNumColumns(-1);
        view.setStretchMode(GridView.STRETCH_SPACING);

        mAdapter = new MyAdapter();
        view.setAdapter(mAdapter);
        view.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (mListener != null) {
                    mListener.onIconSelected(mIconIds[position]);
                }
                dismiss();
            }
        });

        MaterialDialog.Builder mb = Compat.getMaterialDialogBuilder(getActivity());
        mb.customView(view, false);
        mb.negativeText(R.string.icon_remove);
        mb.callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onNegative(MaterialDialog dialog) {
                super.onNegative(dialog);
                if (mListener != null) {
                    mListener.onIconSelected(0);
                }
            }
        });
        mb.neutralText(android.R.string.cancel);
        mb.title(R.string.icon_dlgtit);
        return mb.build();
        // return AnimHelper.addAnimations(ab.create());
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                break;
        }
    }

    public void setListener(OnIconSelectedListener listener) {
        mListener = listener;
    }

    public interface OnIconSelectedListener {
        public void onIconSelected(int iconId);
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mIconIds.length;
        }

        @Override
        public Object getItem(int position) {
            Drawable d = DrawableCompat.wrap(ComponentUtils.getIconDrawable(getActivity(),
                    mIconIds[position]));
            int size = (int) dpToPx(48);
            d.setBounds(0, 0, size, size);
            DrawableCompat.setTint(d, getResources().getColor(android.R.color.white));
            return d;
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView iv;
            if (convertView != null) {
                iv = (ImageView) convertView;
            } else {
                iv = new ImageView(getActivity()) {
                    // Hack to get square images...
                    @Override
                    protected void onMeasure(int widthMeasureSpec,
                                             int heightMeasureSpec) {
                        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
                    }
                };
            }
            int size = (int) dpToPx(8);
            iv.setPadding(size, size, size, size);
            iv.setImageDrawable((Drawable) mAdapter.getItem(position));
            return iv;
        }

    }

}
