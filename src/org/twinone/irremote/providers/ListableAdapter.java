package org.twinone.irremote.providers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.twinone.irremote.R;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

public class ListableAdapter extends BaseAdapter implements Filterable {

	private LayoutInflater mInflater;
	private List<? extends BaseListable> mOriginalItems;
	private List<? extends BaseListable> mCurrentItems;

	@SuppressWarnings("unchecked")
	public ListableAdapter(Context context, Object[] items) {
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// populate mItems
		mOriginalItems = (List<BaseListable>) (List<?>) Arrays.asList(items);
		sort(mOriginalItems);
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
				List<BaseListable> result = new ArrayList<BaseListable>();
				for (BaseListable l : mOriginalItems) {
					if (l.getDisplayName().toLowerCase(Locale.ENGLISH)
							.contains(match)) {
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
			mCurrentItems = (List<? extends BaseListable>) results.values;
			notifyDataSetChanged();
		}

	}

	private void sort(List<? extends BaseListable> items) {
		try {
			Collections.sort(items);
		} catch (Exception e) {
			Log.d("BaseListable", "known sort exception in BaseListable", e);
		}

	}

	public void sort() {
		sort(mCurrentItems);
	}

	@Override
	public int getCount() {
		return mCurrentItems.size();
	}

	@Override
	public BaseListable getItem(int position) {
		return mCurrentItems.get(position);
	}

	public List<? extends BaseListable> getAllItems() {
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
		BaseListable item = mCurrentItems.get(position);

		TextView tv = (TextView) mInflater.inflate(R.layout.listable_element,
				parent, false);
		tv.setText(item.getDisplayName());
		return tv;
	}

	private Filter mFilter;

	@Override
	public Filter getFilter() {
		return mFilter;
	}

}
