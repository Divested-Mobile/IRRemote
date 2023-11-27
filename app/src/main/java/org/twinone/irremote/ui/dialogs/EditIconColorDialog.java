package org.twinone.irremote.ui.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatImageView;

import com.afollestad.materialdialogs.MaterialDialog;

import org.twinone.irremote.R;
import org.twinone.irremote.compat.Compat;

public class EditIconColorDialog extends DialogFragment implements DialogInterface.OnClickListener {

    private static final String ARG_COLOR = "org.twinone.irremote.ui.SelectIconColorDialog.color";
    private int[] mColors;
    private OnIconColorSelectedListener mListener;

    public static void showFor(Activity a, int color) {
        EditIconColorDialog.newInstance(color).show(a.getFragmentManager(), "select_icon_color_dialog");
    }

    public static EditIconColorDialog newInstance(int color) {
        EditIconColorDialog f = new EditIconColorDialog();
        Bundle b = new Bundle();
        b.putInt(ARG_COLOR, color);
        f.setArguments(b);
        return f;
    }

    public void show(Activity a) {
        show(a.getFragmentManager(), "select_icon_color_dialog");
    }

    private float dpToPx(float dp) {
        return dp * getActivity().getResources().getDisplayMetrics().density;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mColors = getResources().getIntArray(R.array.material_colors);

        View inflate = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_edit_color_gridview, null);
        GridView view = inflate.findViewById(R.id.edit_color_gridview);
        view.setColumnWidth((int) dpToPx(48));
        int size = (int) dpToPx(16);
        view.setPadding(size, size, size, size);
        view.setVerticalSpacing((int) dpToPx(8));
        view.setHorizontalSpacing((int) dpToPx(8));
        view.setAdapter(new MyAdapter());
        view.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mListener != null) {
                    mListener.onIconColorSelected(position + 2);
                }
                dismiss();
            }
        });

        MaterialDialog.Builder mb = Compat.getMaterialDialogBuilder(getActivity());
        mb.customView(inflate, false);
        mb.negativeText(android.R.string.cancel);

        mb.title(R.string.icon_color_dlgtit);
        return mb.build();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                break;
        }
    }

    public void setListener(OnIconColorSelectedListener listener) {
        mListener = listener;
    }

    public interface OnIconColorSelectedListener {
        void onIconColorSelected(int color);
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mColors.length;
        }

        @Override
        public Object getItem(int position) {
            return mColors[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView iv;
            if (convertView != null) {
                iv = (ImageView) convertView;
            } else {
                iv = new AppCompatImageView(getActivity()) {
                    @Override
                    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
                    }
                };
            }

            ShapeDrawable shapeDrawable = new ShapeDrawable(new OvalShape());
            shapeDrawable.getPaint().setColor(mColors[position]);
            iv.setBackground(shapeDrawable);
            return iv;
        }
    }
}
