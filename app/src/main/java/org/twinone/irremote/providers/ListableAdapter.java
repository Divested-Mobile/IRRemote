package org.twinone.irremote.providers;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import org.twinone.irremote.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ListableAdapter extends BaseAdapter implements Filterable {

    private final LayoutInflater mInflater;
    private final List<Object> mOriginalItems;
    private final Filter mFilter;
    private List<Object> mCurrentItems;
    private Context mContext;
    protected Context getContext() { return mContext; }

    @SuppressWarnings("unchecked")
    public ListableAdapter(Context context, Object[] items) {
        if (items == null) throw new NullPointerException("Null items");
        mContext = context;
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mOriginalItems =  Arrays.asList(items);
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

    private void sort(List items) {
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
    public Object getItem(int position) {
        return mCurrentItems.get(position);
    }

    public List<Object> getAllItems() {
        return mCurrentItems;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(position, parent);
    }


    protected LayoutInflater getLayoutInflater() {return mInflater;}


    private View createViewFromResource(int position,
                                        ViewGroup parent) {
        Object item = mCurrentItems.get(position);

        TextView tv = (TextView) mInflater.inflate(R.layout.provider_list_item,
                parent, false);
        tv.setText(item.toString());
        return tv;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
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
                List<Object> result = new ArrayList<>();
                for (Object o : mOriginalItems) {
                    if (o.toString().toLowerCase(Locale.ENGLISH)
                            .contains(match)) {
                        result.add(o);
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
            mCurrentItems = (List<Object>) results.values;
            notifyDataSetChanged();
        }

    }

}
