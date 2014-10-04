package org.twinone.irremote.ui.dialogs;

import org.twinone.irremote.R;
import org.twinone.irremote.components.ComponentUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class EditIconDialog extends DialogFragment implements
		DialogInterface.OnClickListener {

	private static final String ARG_ICON = "org.twinone.irremote.ui.SelectIconDialog.icon";

	public static void showFor(Activity a, int color) {
		EditIconDialog.newInstance(color).show(a.getFragmentManager(),
				"select_color_dialog");
	}

	public void show(Activity a) {
		show(a.getFragmentManager(), "save_remote_dialog");
	}

	public static EditIconDialog newInstance(int color) {
		EditIconDialog f = new EditIconDialog();
		Bundle b = new Bundle();
		b.putInt(ARG_ICON, color);
		f.setArguments(b);
		return f;
	}

	private int mSelectedIcon;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSelectedIcon = getArguments().getInt(ARG_ICON);
	}

	private BaseAdapter mAdapter;

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mIconIds.length;
		}

		@Override
		public Object getItem(int position) {
			Drawable d = ComponentUtils.getIconDrawable(getActivity(),
					mIconIds[position]);
			int size = (int) dpToPx(48);
			d.setBounds(0, 0, size, size);
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

	private float dpToPx(float dp) {
		return dp * getActivity().getResources().getDisplayMetrics().density;
	}

	private int[] mIconIds;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		mIconIds = ComponentUtils.ICON_IDS;

		GridView view = new GridView(getActivity());
		int size = (int) dpToPx(30);
		view.setColumnWidth(size);
		view.setNumColumns(5);
		view.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);

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

		AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
		ab.setView(view);
		ab.setNegativeButton(R.string.icon_remove, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (mListener != null) {
					mListener.onIconSelected(0);
				}
			}
		});
		ab.setNegativeButton(android.R.string.cancel, null);
		ab.setTitle(R.string.icon_dlgtit);
		return ab.create();
		// return AnimHelper.addAnimations(ab.create());
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
		case DialogInterface.BUTTON_POSITIVE:
			break;
		}
	}

	private OnIconSelectedListener mListener;

	public void setListener(OnIconSelectedListener listener) {
		mListener = listener;
	}

	public interface OnIconSelectedListener {
		public void onIconSelected(int iconId);
	}

}
