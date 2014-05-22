package org.twinone.irremote;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

public class ListableAdapter extends BaseAdapter implements Filterable {

	private LayoutInflater mInflater;
	private List<? extends Listable> mOriginalItems;
	private List<? extends Listable> mCurrentItems;

	@SuppressWarnings("unchecked")
	public ListableAdapter(Context context, Object[] items) {
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// populate mItems
		mOriginalItems = (List<Listable>) (List<?>) Arrays.asList(items);
		Collections.sort(mOriginalItems);
		mCurrentItems = mOriginalItems;
		mFilter = new MyFilter();
	}

	/**
	 * Restores the original dataset, with all elements in it
	 */
	public void restoreOriginalDataSet() {
		mCurrentItems = mOriginalItems;
		notifyDataSetChanged();
	}

	private class MyFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			String match = constraint.toString().toLowerCase(Locale.ENGLISH);
			FilterResults results = new FilterResults();
			if (constraint == null || constraint.length() == 0) {
				results.values = mOriginalItems;
				results.count = mOriginalItems.size();
			} else {
				List<Listable> result = new ArrayList<Listable>();
				for (Listable l : mOriginalItems) {
					if (l.getKey().toLowerCase(Locale.ENGLISH).contains(match)) {
						result.add(l);
					}

				}
				results.values = result;
				results.count = result.size();
			}
			return results;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			mCurrentItems = (List<? extends Listable>) results.values;
			notifyDataSetChanged();
		}

	}

	public void sort() {
		Collections.sort(mCurrentItems);
	}

	@Override
	public int getCount() {
		return mCurrentItems.size();
	}

	@Override
	public Listable getItem(int position) {
		return mCurrentItems.get(position);
	}

	public List<? extends Listable> getAllItems() {
		return mCurrentItems;
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
		Listable item = mCurrentItems.get(position);

		View view = mInflater.inflate(R.layout.listable_element, parent, false);
		TextView tv = (TextView) view.findViewById(R.id.tvTitle);
		tv.setText(item.getDisplayName());
		return view;
	}

	private Filter mFilter;

	@Override
	public Filter getFilter() {
		return mFilter;
	}

}
