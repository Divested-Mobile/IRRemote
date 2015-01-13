package org.twinone.irremote.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.twinone.irremote.R;
import org.twinone.irremote.components.Remote;

import java.util.ArrayList;

public class SelectRemoteListView extends ListView implements
        android.widget.AdapterView.OnItemClickListener {

    private static final String TAG = "SelectRemoteListView";

    private LayoutInflater mInflater;
    private ArrayList<String> mItems;
    private MyAdapter mAdapter;

    public SelectRemoteListView(Context context) {
        super(context);
        init();
    }

    public SelectRemoteListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private void init() {
        setChoiceMode(CHOICE_MODE_SINGLE);
        mInflater = LayoutInflater.from(getContext());
        updateRemotesList();
        setOnItemClickListener(this);
    }

    /**
     * Update the list of remotes after it has been changed on disk
     */
    public void updateRemotesList() {
        mItems = (ArrayList<String>) Remote.getNames(getContext());
        mAdapter = new MyAdapter();
        setAdapter(mAdapter);
        selectRemote(getCheckedItemPosition());
    }

    public void selectRemote(String remoteName) {
        if (remoteName == null || remoteName.isEmpty())
            return;
        if (mItems.contains(remoteName)) {
            selectRemote(mItems.indexOf(remoteName));
        }
    }

    public void selectRemote(int position) {
        if (getCheckedItemPosition() == position)
            return;
        setSelection(position);
        setItemChecked(position, true);
    }

    public String getSelectedRemoteName() {
        return getRemoteName(getCheckedItemPosition());
    }
    public String getRemoteName(int position) {
        return ((String) getItemAtPosition(position));
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
            selectRemote(position);
    }


    private class MyAdapter extends BaseAdapter {

        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) convertView;
            if (view == null)
                view = (TextView) mInflater.inflate(
                        R.layout.main_nav_item, parent, false);
            view.setText(mItems.get(position));
            view.setId(position);
            return view;
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public Object getItem(int position) {
            return mItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }

}
