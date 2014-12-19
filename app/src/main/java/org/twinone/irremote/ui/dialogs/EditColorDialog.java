package org.twinone.irremote.ui.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.twinone.irremote.R;
import org.twinone.irremote.components.AnimHelper;
import org.twinone.irremote.components.ComponentUtils;

public class EditColorDialog extends DialogFragment implements
        DialogInterface.OnClickListener {

    private static final String ARG_COLOR = "org.twinone.irremote.ui.SelectColorDialog.color";
    private String[] mStrings;
    private OnColorSelectedListener mListener;

    public static void showFor(Activity a, int color) {
        EditColorDialog.newInstance(color).show(a.getFragmentManager(),
                "select_color_dialog");
    }

    public static EditColorDialog newInstance(int color) {
        EditColorDialog f = new EditColorDialog();
        Bundle b = new Bundle();
        b.putInt(ARG_COLOR, color);
        f.setArguments(b);
        return f;
    }

    public void show(Activity a) {
        show(a.getFragmentManager(), "select_color_dialog");
    }


    private float dpToPx(float dp) {
        return dp * getActivity().getResources().getDisplayMetrics().density;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mStrings = getResources().getStringArray(R.array.colors);

        ListView view = new ListView(getActivity());
        BaseAdapter mAdapter = new MyAdapter();
        view.setDivider(null);
        view.setAdapter(mAdapter);
        view.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (mListener != null) {
                    mListener.onColorSelected(position + 1);
                }
                dismiss();
            }
        });

        AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
        ab.setView(view);
        ab.setNegativeButton(android.R.string.cancel, null);

        ab.setTitle(R.string.color_dlgtit);
        return AnimHelper.addAnimations(ab.create());
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                break;
        }
    }

    public void setListener(OnColorSelectedListener listener) {
        mListener = listener;
    }

    public interface OnColorSelectedListener {
        public void onColorSelected(int color);
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mStrings.length;
        }

        @Override
        public Object getItem(int arg0) {
            return mStrings[arg0];
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public View getView(int id, View convertView, ViewGroup parent) {
            TextView tv;
            if (convertView != null) {
                tv = (TextView) convertView;
            } else {
                tv = new TextView(getActivity());
                tv.setShadowLayer(1, 1, 1, Color.parseColor("#000000"));
                tv.setTextColor(Color.parseColor("#ffffff"));
                int dp = (int) dpToPx(12);
                tv.setPadding(dp, dp, dp, dp);
                tv.setGravity(Gravity.CENTER_HORIZONTAL);
            }
            tv.setText(mStrings[id]);
            tv.setBackground(ComponentUtils.getGradientDrawable(getActivity(),
                    id + 1, false));
            return tv;
        }
    }

}
