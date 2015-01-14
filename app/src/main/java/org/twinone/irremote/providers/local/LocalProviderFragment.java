package org.twinone.irremote.providers.local;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.twinone.irremote.R;
import org.twinone.irremote.components.Remote;
import org.twinone.irremote.providers.ProviderFragment;
import org.twinone.irremote.ui.SelectRemoteListView;

import java.util.List;

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
        Toast.makeText(getActivity(), "Selected " + mListView.getItemAtPosition(position), Toast.LENGTH_SHORT).show();
        String selectedName = (String) mListView.getAdapter().getItem(position);
        getProvider().saveRemote(Remote.load(getActivity(), selectedName));
    }
}
