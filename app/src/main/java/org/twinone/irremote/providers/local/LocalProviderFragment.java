package org.twinone.irremote.providers.local;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.twinone.irremote.R;
import org.twinone.irremote.components.Remote;
import org.twinone.irremote.providers.ProviderFragment;

public class LocalProviderFragment extends ProviderFragment implements AdapterView.OnItemClickListener {
    private ListView mListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mListView = new ListView(getActivity());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                R.layout.provider_list_item, Remote.getNames(getActivity()));
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(this);
        return mListView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String selectedName = (String) mListView.getAdapter().getItem(position);
        getProvider().requestSaveRemote(Remote.load(getActivity(), selectedName));
    }
}
