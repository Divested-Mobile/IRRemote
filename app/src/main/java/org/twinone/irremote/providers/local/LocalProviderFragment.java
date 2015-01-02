package org.twinone.irremote.providers.local;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import org.twinone.irremote.providers.ProviderFragment;
import org.twinone.irremote.ui.SelectRemoteListView;

public class LocalProviderFragment extends ProviderFragment implements AdapterView.OnItemSelectedListener {
    private SelectRemoteListView mListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mListView = new SelectRemoteListView(getActivity());

        mListView.setOnItemSelectedListener(this);
        return mListView;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getActivity(), "Selected " + mListView.getItemAtPosition(position), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        
    }
}
