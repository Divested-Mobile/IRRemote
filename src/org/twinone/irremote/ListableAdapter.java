package org.twinone.irremote;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.twinone.irremote.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListableAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<? extends Listable> mItems;

	@SuppressWarnings("unchecked")
	public ListableAdapter(Context context, Object[] items) {
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// populate mItems
		mItems = (List<Listable>) (List<?>) Arrays.asList(items);
		Collections.sort(mItems);
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

	public void sort() {
		Collections.sort(mItems);
	}

	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public Listable getItem(int position) {
		return mItems.get(position);
	}

	public List<? extends Listable> getAllItems() {
		return mItems;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return createViewFromResource(position, convertView, parent);
	}

	private View createViewFromResource(int position, View convertView,
			ViewGroup parent) {
		Listable item = mItems.get(position);

		View view = mInflater.inflate(R.layout.listable_element, parent, false);
		TextView tv = (TextView) view.findViewById(R.id.tvTitle);
		tv.setText(item.getDisplayName());

		return view;
	}

}
